package com.example.taskreminder;

import com.example.taskreminder.R;

import java.sql.RowId;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.StateSet;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TimePicker;
import android.widget.Toast;


public class ReminderEditActivity extends Activity {
	private static final String TAG = "Reminder";
	
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String TIME_FORMAT = "kk:mm";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";
	
	private EditText mTitleText;
	private EditText mBodyText;
	private Button mDateButton;
	private Button mTimeButton;
	private Button mConfirmButton;
	private Long mRowId;
	private RemindersDbAdapter mDbHelper;
	private Calendar mCalendar;

	
	private static final int DATE_PICKER_DIALOG = 0;
	private static final int TIME_PICKER_DIALOG = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mDbHelper = new RemindersDbAdapter(this);
		
		setContentView(R.layout.reminder_edit);

		mCalendar = Calendar.getInstance();

		mTitleText = (EditText)findViewById(R.id.title);
		mConfirmButton = (Button)findViewById(R.id.confirm);
		mBodyText = (EditText)findViewById(R.id.body);
		
		mDateButton = (Button)findViewById(R.id.reminder_date);
		mTimeButton = (Button)findViewById(R.id.reminder_time);
		
		mRowId = savedInstanceState != null ? savedInstanceState.getLong(RemindersDbAdapter.KEY_ROWID) : null;

		registerButtonListenersAndSetDefaultText();
		
	}
	
	private void setRowIdFromIntent() {
		if(mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(RemindersDbAdapter.KEY_ROWID) :  null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mDbHelper.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mDbHelper.open();
		setRowIdFromIntent();
		populateFields();
	}
	
	private void populateFields() {
		if(mRowId != null) {
			Cursor reminder = mDbHelper.fetchReminder(mRowId);
			startManagingCursor(reminder);
			mTitleText.setText(reminder.getString(reminder.getColumnIndexOrThrow(RemindersDbAdapter.KEY_TITLE)));
			mBodyText.setText(reminder.getString(reminder.getColumnIndexOrThrow(RemindersDbAdapter.KEY_BODY)));
			SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT); 
			Date date = null;
			try {
				String dateString = reminder.getString(reminder.getColumnIndexOrThrow(RemindersDbAdapter.KEY_DATA_TIME));
				date = dateTimeFormat.parse(dateString);
				mCalendar.setTime(date);
			} catch (ParseException e) {
				Log.e("ReminderEditActivity", e.getMessage(), e);
			}
		} else {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			
			String defaultTitleKey = getString(R.string.pref_task_title_key);
			String defaultTimeKey = getString(R.string.pref_default_time_from_now_key);
			
			String defaulTitle = prefs.getString(defaultTitleKey, "");
			String defaulTime = prefs.getString(defaultTimeKey, "");
			
			if("".equals(defaulTitle) == false) {
				mTitleText.setText(defaulTitle);
			}
			
			if("".equals(defaulTime) == false) {
				mCalendar.add(Calendar.MINUTE, Integer.parseInt(defaulTime));
			}
		}
		
		updateDateButtonText();
		updateTimeButtonText();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(RemindersDbAdapter.KEY_ROWID, mRowId);
	}
	
	private void registerButtonListenersAndSetDefaultText() {
		mDateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DATE_PICKER_DIALOG);
			}
		});
		
		mTimeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(TIME_PICKER_DIALOG);
			}
		});
		
		mConfirmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveState();
				setResult(RESULT_OK);
				Toast.makeText(ReminderEditActivity.this
						, getString(R.string.task_saved_message)
						, Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		
		updateDateButtonText();
		updateTimeButtonText();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DATE_PICKER_DIALOG:
				return showDatePicker();
			case TIME_PICKER_DIALOG:
				return showTimePicker();
		}
		
		return super.onCreateDialog(id);
	}

	private TimePickerDialog showTimePicker() {
		TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				mCalendar.set(Calendar.MINUTE, minute);
				updateTimeButtonText();
			}
		}, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
		return timePicker;
	}

	private DatePickerDialog showDatePicker() {
		DatePickerDialog datePicker = new DatePickerDialog(ReminderEditActivity.this, 
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker  view, int year, int monthOfYear, int dayOfMonth) {
						mCalendar.set(Calendar.YEAR, year);
						mCalendar.set(Calendar.MONTH, monthOfYear);
						mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						updateDateButtonText();
					}
		}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
		return datePicker;
	}
	
	private void updateDateButtonText() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		String dateForButton = dateFormat.format(mCalendar.getTime());
		mDateButton.setText(dateForButton);
	}
	
	private void updateTimeButtonText() {
		SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
		String timeForButton = timeFormat.format(mCalendar.getTime());
		mTimeButton.setText(timeForButton);
	}

	private void saveState() {
		String title = mTitleText.getText().toString();
		String body = mBodyText.getText().toString();
		
		SimpleDateFormat dateTimeFormat = new 
			SimpleDateFormat(DATE_TIME_FORMAT);
		String reminderDateTime = 
			dateTimeFormat.format(mCalendar.getTime());
		
		if(mRowId == null) {
			long id = mDbHelper.createReminder(title, body, reminderDateTime);
			if(id > 0) {
				mRowId = id;
			}
		} else {
			mDbHelper.updateReminder(mRowId, title, body, reminderDateTime);
		}
		
		new ReminderManager(this).setReminder(mRowId, mCalendar);
			
	}
	
}


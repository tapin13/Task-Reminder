package com.example.taskreminder;

import com.example.taskreminder.R;

import android.R.bool;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.support.v4.app.NavUtils;
//import android.support.v4.widget.SimpleCursorAdapter;

public class ReminderListActivity extends ListActivity {

	private static final String TAG = "Reminder";
	
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	
	private RemindersDbAdapter mDbHelper;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_list);
        
//        String[] items = new String[] { "Foo", "Bar", "Fizz", "Bin" };
//        
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.reminder_row, R.id.text1, items);
//        setListAdapter(adapter);

        mDbHelper = new RemindersDbAdapter(this);
        mDbHelper.open();
        fillData();
        
        registerForContextMenu(getListView());
    }

    private void fillData() {
		Cursor remindersCursor = mDbHelper.fetchAllReminders();
		startManagingCursor(remindersCursor);
		
		String[] from  = new String[]{RemindersDbAdapter.KEY_TITLE};
		
		int[] to = new int[]{R.id.text1};
		
		SimpleCursorAdapter reminders = new SimpleCursorAdapter(this, R.layout.reminder_row, remindersCursor, from, to);
		
		setListAdapter(reminders);
	}

	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	Log.v(TAG, "l:" + l + " v: " + v + " p: " + position + " id: " + id);
    	super.onListItemClick(l, v, position, id);
    	
    	Intent i = new Intent(this, ReminderEditActivity.class);
    	i.putExtra(RemindersDbAdapter.KEY_ROWID, id);
    	startActivityForResult(i, ACTIVITY_EDIT);
    }
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	fillData();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
			case R.id.menu_delete:
				AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
				mDbHelper.deleteReminder(info.id);
				fillData();
				
				Log.v(TAG, "menu_delete pressed");
				return true;
    	}
    	
    	return super.onContextItemSelected(item);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	
    	MenuInflater mi = getMenuInflater();
    	mi.inflate(R.menu.list_menu_item_longpress, menu);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater mi = getMenuInflater();
    	mi.inflate(R.menu.list_menu, menu);
        //getMenuInflater().inflate(R.menu.activity_reminder_list, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featuredId, MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.menu_insert:
			Log.v(TAG, "menu_insert pressed");
			createReminder();
			return true;
		}
    	
    	return super.onMenuItemSelected(featuredId, item);
    }

    private void createReminder() {
    	Intent i = new Intent(this, ReminderEditActivity.class);
    	startActivityForResult(i, ACTIVITY_CREATE);
    }
    
}

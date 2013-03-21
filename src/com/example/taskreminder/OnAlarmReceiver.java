package com.example.taskreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnAlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		long rowid = intent.getExtras().getLong(RemindersDbAdapter.KEY_ROWID);
		
		Log.e("Reminder", "onReceive msg: " + rowid);
		
		WakeReminderIntentService.acquireStaticLock(context);
		
		Intent i = new Intent(context, ReminderService.class);
		i.putExtra(RemindersDbAdapter.KEY_ROWID, rowid);
		context.startService(i);
		
	}

}

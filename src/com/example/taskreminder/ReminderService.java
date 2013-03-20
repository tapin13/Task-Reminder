package com.example.taskreminder;

import android.content.Intent;

public class ReminderService extends WakeReminderIntentService {
	public ReminderService() {
		super("ReminderService");
	}

	@Override
	void doReminderWork(Intent intent) {
		Long rowId = intent.getExtras().getLong(RemindersDbAdapter.KEY_ROWID);
	}
}

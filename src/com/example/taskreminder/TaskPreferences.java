package com.example.taskreminder;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.text.method.DialerKeyListener;

public class TaskPreferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		addPreferencesFromResource(R.xml.task_preferences);
		
		EditTextPreference timeDefault = (EditTextPreference)findPreference(getString(R.string.pref_default_time_from_now_key));
		timeDefault.getEditText().setKeyListener(DialerKeyListener.getInstance());
	}
}

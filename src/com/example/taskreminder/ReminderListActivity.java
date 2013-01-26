package com.example.taskreminder;

import com.example.taskreminder.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class ReminderListActivity extends ListActivity {

	private static final String TAG = "Reminder";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_list);
        
        String[] items = new String[] { "Foo", "Bar", "Fizz", "Bin" };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.reminder_row, R.id.text1, items);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	Log.v(TAG, "l:" + l + " v: " + v + " p: " + position + " id: " + id);
    	//super.onListItemClick(l, v, position, id);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_reminder_list, menu);
        return true;
    }

    
}

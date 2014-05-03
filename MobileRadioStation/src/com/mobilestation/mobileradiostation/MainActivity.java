package com.mobilestation.mobileradiostation;

import com.mobilestation.mobileradiostation.controllers.MainGridAdapter;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class MainActivity extends Activity {
	
	private GridView mGridMainItem;
	
	private MainItem[] MAIN_ITEMS = {
			new MainItem("Broadcast", R.drawable.broadcast, BroadcastActivity.class),
			new MainItem("Record", R.drawable.top_record, RecordActivity.class),
			new MainItem("Edit", R.drawable.top_edit, PlayListActivity.class)
			
			//new MainItem("Upload", R.drawable.top_internet, RecordActivity.class)
	};
	public static final String TAG = "radio_station";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
        
		setContentView(R.layout.activity_main);
		mGridMainItem = (GridView)findViewById(R.id.grid_main);
		
		mGridMainItem.setOnItemClickListener(new OnItemClickListener(){
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				GridView gridView = (GridView)parent;
				MainItem item = (MainItem)gridView.getItemAtPosition(position);
				
				Intent intent = new Intent(getApplicationContext(), 
						(Class<?>)item.getClassName());
				
				if(item.getClassName().equals(PlayListActivity.class)){
					intent.putExtra(Utils.LIST_MODE, Utils.MODE_EDIT);
					
				} else if (item.getClassName().equals(RecordActivity.class)){
					intent.putExtra(Utils.RECORD_MODE, Utils.MODE_RECORD);
					
				}
				startActivity(intent);
			}
		});
	}

	@Override
	public void onResume(){
		super.onResume();
		MainGridAdapter adapter = new MainGridAdapter(this, R.layout.grid_item, MAIN_ITEMS);
		mGridMainItem.setAdapter(adapter);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

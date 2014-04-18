package com.mobilestation.mobileradiostation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class BroadcastActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.broadcast_main);
		
		Button imgLive = (Button)findViewById(R.id.btn_start_live);
		Button imgRecorded = (Button)findViewById(R.id.btn_start_recorded_audio);
		//imgLive.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
		imgLive.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		imgRecorded.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		
		imgLive.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
				intent.putExtra(Utils.RECORD_MODE, Utils.MODE_LIVE);
				startActivity(intent);
			}
			
		});
		imgRecorded.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), PlayListActivity.class);
				intent.putExtra(Utils.LIST_MODE, Utils.MODE_PLAY);
				startActivity(intent);
			}
		});
	}
}

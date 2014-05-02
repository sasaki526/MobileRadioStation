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
		
		ImageView imgLive = (ImageView)findViewById(R.id.btn_start_live);
		imgLive.setImageResource(R.drawable.live);
		ImageView imgRecorded = (ImageView)findViewById(R.id.btn_start_recorded_audio);
		imgRecorded.setImageResource(R.drawable.archive);
		
		imgLive.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), LiveActivity.class);
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

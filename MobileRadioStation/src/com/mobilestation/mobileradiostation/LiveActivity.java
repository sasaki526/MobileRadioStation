package com.mobilestation.mobileradiostation;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.mobilestation.bobileradiostation.views.MicButton;
import com.mobilestation.bobileradiostation.views.SoundButton;
import com.mobilestation.mobileradiostation.controller.SoundFileAdapter;

/**
 * Sound Mixer 
 * 
 * @author masa
 *
 */
public class LiveActivity extends Activity{

	
	/* Code for getting the URI to the sound file. */
	private static final int CODE_SOUND_URI = 0;
	
	/* Widgets */
	SeekBar mMicBar = null;
	TextView mMicLabel = null;
	//ListView mMicList = null;
	MicButton mMicButton = null;
	
	SeekBar mSoundBar = null;
	TextView mSoundLabel = null;
	SoundButton mSoundButton = null;

	Button mSelecter = null;
	TextView mSelectedTitle = null;
	ListView mSoundList = null;
	
	Thread mRT = null;

	ArrayAdapter<Uri> mNextSongs = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.live_main);
		
		
		/* Construct MIC Track */
		LinearLayout micLayout = (LinearLayout)findViewById(R.id.mic_track);
		mMicLabel = (TextView)findViewById(R.id.mic_label);
		mMicBar = (SeekBar)findViewById(R.id.mic_bar);
		
		//mMicBar = new MixingBar(LiveActivity.this);
		//LinearLayout.LayoutParams paramsMic = new LinearLayout.LayoutParams(
		//	LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1 );
		//paramsMic.setMargins(0, 16, 0, 0);
		
		//mMicBar.setLayoutParams(paramsMic);
		//micLayout.addView(mMicBar);
		
		mMicBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int volume, boolean arg2) {
				mMicButton.setLRVolume(volume / 10.0f,volume / 10.0f);
				mMicLabel.setText(" Volume:" + Integer.toString(volume));
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				
			}
			
		});

		mMicButton = new MicButton(LiveActivity.this);
		LinearLayout.LayoutParams paramsMicButton = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT,1.0f);
		mMicButton.setLayoutParams(paramsMicButton);
		mMicBar.setMax(mMicButton.getMaxVolume() * 10 ); //Improve Resolution x10
		micLayout.addView(mMicButton);

				
		
		/* Construct Sound Track */
		LinearLayout soundLayout = (LinearLayout)findViewById(R.id.sound_track);

		mSoundLabel = (TextView)findViewById(R.id.sound_label);
		mSoundBar = (SeekBar)findViewById(R.id.sound_bar);
		
		//mSoundBar = new MixingBar(LiveActivity.this);
		//LinearLayout.LayoutParams paramsSound = new LinearLayout.LayoutParams(
		//	LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1);
		//paramsSound.setMargins(0, 16, 0, 0);
		//mSoundBar.setLayoutParams(paramsSound);
		//soundLayout.addView(mSoundBar);
		
		mSoundBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			
			@Override
			public void onProgressChanged(SeekBar arg0, int volume, boolean arg2) {
				mSoundButton.setLRVolume(volume / 10.0f ,volume / 10.0f );
				mSoundLabel.setText(" Volume:"+ Integer.toString(volume));
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {	
			}
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {	
			}
		});
		
		mSoundButton = new SoundButton(LiveActivity.this);
		LinearLayout.LayoutParams paramsSoundButton = new LinearLayout.LayoutParams(
				0, LayoutParams.WRAP_CONTENT,1.0f);
		mSoundButton.setLayoutParams(paramsSoundButton);
		mSoundBar.setMax(mSoundButton.getMaxVolume() * 10); //Improve Resolution x10
		
		OnClickListener clicker = new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				mSoundButton.onPlay();
			}
			
		};
		mSoundButton.setOnClickListener(clicker);
		soundLayout.addView(mSoundButton);
		
		
		/* Selecter Section */
		mSelecter = (Button)findViewById(R.id.sound_select);
		mSelecter.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				showFileChooser();
			}
			
		});
		mSelectedTitle = (TextView)findViewById(R.id.sound_selected);
		
		mSoundList = (ListView)findViewById(R.id.sound_list);
		mNextSongs = new SoundFileAdapter(LiveActivity.this, R.layout.songs, new ArrayList<Uri>());
		mSoundList.setAdapter(mNextSongs);
		mSoundList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Uri uri = (Uri) arg0.getItemAtPosition(arg2);
				
				mSoundButton.setSoundUri(uri);
				mSelectedTitle.setText(Utils.uriToDisplayName(LiveActivity.this, uri));
				
			}
		});
	}
	
	
	private void showFileChooser(){
		
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);	
		startActivityForResult(Intent.createChooser(intent	, "Select a sound file to play"),
				CODE_SOUND_URI);
		
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){
		case CODE_SOUND_URI:

			if ( resultCode == RESULT_OK ){
				
				Uri selectedItem = data.getData();
				Log.i("test",selectedItem.toString());
				int pos = mNextSongs.getPosition(selectedItem);
				if ( pos == -1 ){
					mNextSongs.add(selectedItem);
					mNextSongs.notifyDataSetChanged();
				}
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	protected void onPause() {
		
		mSoundButton.stopSound();
		mMicButton.stopMic();
		
		super.onPause();
	}


	
	
}

package com.mobilestation.mobileradiostation;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.mobilestation.mobileradiostation.controllers.SoundFileAdapter;
import com.mobilestation.mobileradiostation.models.Track;
import com.mobilestation.mobileradiostation.views.MicButton;
import com.mobilestation.mobileradiostation.views.SoundButton;

/**
 * Sound Mixer 
 * 
 * @author masa
 *
 */
public class LiveActivity extends Activity{

	
	/* Code for getting the URI to the sound file. */
	private static final int CODE_SOUND_URI = 0;
	
	/* Widgets for the MIC track*/
	TextView mMicLabel = null;
	SeekBar mMicBar = null;
	TextView mMicStatusLabel = null;
	MicButton mMicButton = null;
	
	/* Widgets for Ch1 (Stereo LR) */
	TextView mSoundLabel_ch1 = null;
	SeekBar mSoundBar_ch1 = null;
	TextView mSoundStatusLabel_ch1 = null;
	SoundButton mSoundButton_ch1 = null;

	/* Widgets for Ch2 (Stereo LR) */
	TextView mSoundLabel_ch2 = null;
	SeekBar mSoundBar_ch2 = null;
	TextView mSoundStatusLabel_ch2 = null;
	SoundButton mSoundButton_ch2 = null;

	/* Widgets for Selector */
	RadioGroup mSelector = null;
	RadioButton mSelectedTitle_ch1 = null;
	RadioButton mSelectedTitle_ch2 = null;
	
	
	ListView mSoundList = null;
	
	Thread mRT = null;

	ArrayAdapter<Uri> mNextSongs = null;
	private void createMICTrack(){

		/* Construct MIC Track */
		LinearLayout micLayout = (LinearLayout)findViewById(R.id.mic_track);
		mMicLabel = (TextView)findViewById(R.id.mic_label);
		mMicBar = (SeekBar)findViewById(R.id.mic_bar);
		
		
		mMicBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int volume, boolean arg2) {
				mMicButton.setLRVolume(volume / 10.0f,volume / 10.0f);
				mMicLabel.setText(" Vol:" + Integer.toString(volume));
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				
			}
			
		});

		/* Display Status such as "On Air"*/
		mMicStatusLabel = (TextView) findViewById(R.id.mic_status_label);
		mMicButton = new MicButton(LiveActivity.this,mMicStatusLabel);
		mMicButton.setImageResource(R.drawable.mic);
		LinearLayout.LayoutParams paramsMicButton = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT,1.0f);
		mMicButton.setLayoutParams(paramsMicButton);
		mMicBar.setMax(mMicButton.getMaxVolume() * 10 ); //Improve Resolution x10
		micLayout.addView(mMicButton);


	}
	
	private void createSoundTrackCh1(){
		
		/* Construct Sound Track */
		LinearLayout soundLayout = (LinearLayout)findViewById(R.id.sound_track);

		/* Label for display the volume */
		mSoundLabel_ch1 = (TextView)findViewById(R.id.sound_label);
		
		/* SeekBar for chenging the volume */
		mSoundBar_ch1 = (SeekBar)findViewById(R.id.sound_bar);
		mSoundBar_ch1.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			
			@Override
			public void onProgressChanged(SeekBar arg0, int volume, boolean arg2) {
				mSoundButton_ch1.setLRVolume(volume / 10.0f ,volume / 10.0f );
				mSoundLabel_ch1.setText(" Vol:"+ Integer.toString(volume));
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {	
			}
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {	
			}
		});
		
		/* Label for display the status of the track such as Play or Elapsed Time */
		mSoundStatusLabel_ch1 = (TextView)findViewById(R.id.sound_status_label);
		
		mSoundButton_ch1 = new SoundButton(LiveActivity.this,mSoundStatusLabel_ch1);
		mSoundButton_ch1.setImageResource(R.drawable.sound);
		
		LinearLayout.LayoutParams paramsSoundButton = new LinearLayout.LayoutParams(
				0, LayoutParams.WRAP_CONTENT,1.0f);
		mSoundButton_ch1.setLayoutParams(paramsSoundButton);
		mSoundBar_ch1.setMax(mSoundButton_ch1.getMaxVolume() * 10); //Improve Resolution x10
		
		OnClickListener clicker = new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				mSoundButton_ch1.onPlay();
			}
			
		};
		mSoundButton_ch1.setOnClickListener(clicker);
		soundLayout.addView(mSoundButton_ch1);

		
	}
	
	
	private void createSoundTrackCh2(){

		/* Construct Sound Track (Ch2) */
		LinearLayout soundLayout = (LinearLayout)findViewById(R.id.sound_track_ch2);
		
		/* Label for volume */
		mSoundLabel_ch2 = (TextView)findViewById(R.id.sound_label_ch2);
		
		/* SeekBar for changing the volume */
		mSoundBar_ch2 = (SeekBar)findViewById(R.id.sound_bar_ch2);
		mSoundBar_ch2.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			
			@Override
			public void onProgressChanged(SeekBar arg0, int volume, boolean arg2) {
				mSoundButton_ch2.setLRVolume(volume / 10.0f ,volume / 10.0f );
				mSoundLabel_ch2.setText(" Vol:"+ Integer.toString(volume));
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {	
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {	
			}
		});
		
		mSoundStatusLabel_ch2 = (TextView)findViewById(R.id.sound_status_label_ch2);
		
		mSoundButton_ch2 = new SoundButton(LiveActivity.this,mSoundStatusLabel_ch2);
		mSoundButton_ch2.setImageResource(R.drawable.sound);
		
		LinearLayout.LayoutParams paramsSoundButton = new LinearLayout.LayoutParams(
				0, LayoutParams.WRAP_CONTENT,1.0f);
		mSoundButton_ch2.setLayoutParams(paramsSoundButton);
		mSoundBar_ch2.setMax(mSoundButton_ch2.getMaxVolume() * 10); //Improve Resolution x10

		OnClickListener clicker = new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				mSoundButton_ch2.onPlay();
			}
			
		};
		mSoundButton_ch2.setOnClickListener(clicker);
		soundLayout.addView(mSoundButton_ch2);

		
	}
	
	
	private void createSoundChooser(){
		
		mSelector = (RadioGroup)findViewById(R.id.track_selector);
		/* Selector for channel 1 */
		mSelectedTitle_ch1 = (RadioButton)findViewById(R.id.sound_selected_ch1);	
		/* Selector for channel 2 */
		mSelectedTitle_ch2 = (RadioButton)findViewById(R.id.sound_selected_ch2);	

		
		mSoundList = (ListView)findViewById(R.id.sound_list);
		mNextSongs = new SoundFileAdapter(LiveActivity.this, R.layout.songs, getAudioResources());
		mSoundList.setAdapter(mNextSongs);
	
		mSoundList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Uri uri = (Uri) arg0.getItemAtPosition(arg2);
				
				String duration = "";
				
				
				/* Get the URI to the audio file for play */
				int id = mSelector.getCheckedRadioButtonId();
				RadioButton selected = (RadioButton) findViewById(id);
				
				if ( selected == null ){
					/* Not Selected Yet */
						return ;
				}else if ( selected.getId() == mSelectedTitle_ch1.getId()){
					mSoundButton_ch1.setSoundUri(uri);
					duration = mSoundButton_ch1.getDurationString();
							
				}else if ( selected.getId() == mSelectedTitle_ch2.getId() ){
					mSoundButton_ch2.setSoundUri(uri);
					duration = mSoundButton_ch2.getDurationString();
					
				}else {
					/* NEVER COMES HERE */
					return;
				}
				selected.setText("Selected: " + 
						Utils.uriToDisplayName(LiveActivity.this, uri)
						+ " [ "
						+ duration 
						+ " ]");
				
			}
		});
		
		mSoundList.setOnItemLongClickListener(new OnItemLongClickListener(){

			
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
			
					/* DELETE AUDIO FILE FROM THE LIST */
					Uri uri = (Uri) arg0.getItemAtPosition(arg2);
					mNextSongs.remove(uri);
					mNextSongs.notifyDataSetChanged();
					
					Toast.makeText(LiveActivity.this, 
							Utils.uriToDisplayName(LiveActivity.this, uri) + " deleted.", 
							Toast.LENGTH_SHORT).show();
					
				return false;
			}
			
		});
		

		
	}
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.live_main);
			
		createMICTrack();
						
		createSoundTrackCh1();
				
		createSoundTrackCh2();
		
		createSoundChooser();
				
        
	}
	
	
	/**
	 * Adjust the size of SeekBar on running device.
	 */
	private void configureMICTrack(){
		
		/* Change the size of thumb for MIC */
		Resources micres = getResources();
        Drawable micthumb = micres.getDrawable(R.drawable.micnob);
        Bitmap originalnob = ((BitmapDrawable)micthumb).getBitmap();
        
        int mich = (int) (mMicBar.getMeasuredHeight() * 1.3);
        int micw = (int) (mich * 0.7);
        Bitmap scaledmicnob = Bitmap.createScaledBitmap(originalnob, micw, mich, true);
        Drawable newMicThumb = new BitmapDrawable(micres, scaledmicnob);
        newMicThumb.setBounds(0, 0, newMicThumb.getIntrinsicWidth(), newMicThumb.getIntrinsicHeight());
        mMicBar.setThumb(newMicThumb);
        
	}
	
	/**
	 * Adjust size of SeekBar on running device.
	 */
	private void configureSoundTrackCh1(){
	
		/* Make and Change the size of thumb for Sound */
        Resources soundres = getResources();
        Drawable soundthumb = soundres.getDrawable(R.drawable.soundnob);
        Bitmap originalsoundthumb = ((BitmapDrawable)soundthumb).getBitmap();
        
        int soundh = (int) (mSoundBar_ch1.getMeasuredHeight() * 1.3);
        int soundw = (int) (soundh * 0.7);
        Bitmap scaledsoundnob = Bitmap.createScaledBitmap(originalsoundthumb, soundw, soundh, true);
        Drawable newSoundThumb = new BitmapDrawable(soundres, scaledsoundnob);
        newSoundThumb.setBounds(0, 0, newSoundThumb.getIntrinsicWidth(), newSoundThumb.getIntrinsicHeight());
        mSoundBar_ch1.setThumb(newSoundThumb);
      
	}
	
	/**
	 * Adjust size of SeekBar on running device.
	 */
	private void configureSoundTrackCh2(){
	
		/* Make and Change the size of thumb for Sound */
        Resources soundres = getResources();
        Drawable soundthumb = soundres.getDrawable(R.drawable.soundnob);
        Bitmap originalsoundthumb = ((BitmapDrawable)soundthumb).getBitmap();
        
        int soundh = (int) (mSoundBar_ch2.getMeasuredHeight() * 1.3);
        int soundw = (int) (soundh * 0.7);
        Bitmap scaledsoundnob = Bitmap.createScaledBitmap(originalsoundthumb, soundw, soundh, true);
        Drawable newSoundThumb = new BitmapDrawable(soundres, scaledsoundnob);
        newSoundThumb.setBounds(0, 0, newSoundThumb.getIntrinsicWidth(), newSoundThumb.getIntrinsicHeight());
        mSoundBar_ch2.setThumb(newSoundThumb);
      
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus){
		
		/* compute the size of thumbs */
		configureMICTrack();
		configureSoundTrackCh1();
		configureSoundTrackCh2();
		
		super.onWindowFocusChanged(hasFocus);
			
    }
	
	
	/**
	 * Model Provider 
	 * 
	 * MODEL: URIs indicate audio files in the device.
	 * @return array of the URL found in the running device. 
	 */
	private ArrayList<Uri> getAudioResources(){
		
        ArrayList<Uri> audiofiles = new ArrayList<Uri>();
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
                        Track.COLUMNS, 
                        null,
                        null,
                        null);
        while( cursor.moveToNext() ){
                audiofiles.add( ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                		cursor.getLong( cursor.getColumnIndex( MediaStore.Audio.Media._ID )) ));
        }
        cursor.close();
        return audiofiles;		
	}
	

	@Override
	protected void onPause() {
		
		mMicButton.stopMic();
		mSoundButton_ch1.stopSound();
		mSoundButton_ch2.stopSound();
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}


	
	
}

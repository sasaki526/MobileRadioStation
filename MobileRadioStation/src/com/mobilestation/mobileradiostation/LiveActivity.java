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
	TextView mSoundLabel = null;
	SeekBar mSoundBar = null;
	TextView mSoundStatusLabel = null;
	SoundButton mSoundButton = null;

	/* Widgets for Ch2 (Stereo LR) */
	TextView mSoundLabel_ch2 = null;
	SeekBar mSoundBar_ch2 = null;
	TextView mSoundStatusLabel_ch2 = null;
	SoundButton mSoundButton_ch2 = null;

	
	
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

		mMicStatusLabel = (TextView) findViewById(R.id.mic_status_label);
		mMicButton = new MicButton(LiveActivity.this,mMicStatusLabel);
		mMicButton.setImageResource(R.drawable.mic);
		LinearLayout.LayoutParams paramsMicButton = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT,1.0f);
		mMicButton.setLayoutParams(paramsMicButton);
		mMicBar.setMax(mMicButton.getMaxVolume() * 10 ); //Improve Resolution x10
		micLayout.addView(mMicButton);

				
		
		/* Construct Sound Track */
		LinearLayout soundLayout = (LinearLayout)findViewById(R.id.sound_track);

		mSoundLabel = (TextView)findViewById(R.id.sound_label);
		mSoundBar = (SeekBar)findViewById(R.id.sound_bar);
		
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
		
		mSoundStatusLabel = (TextView)findViewById(R.id.sound_status_label);
		
		mSoundButton = new SoundButton(LiveActivity.this,mSoundStatusLabel);
		mSoundButton.setImageResource(R.drawable.sound);
		
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
		
		
		/* Selector Section 
		mSelecter = (Button)findViewById(R.id.sound_select);
		mSelecter.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				//showFileChooser();
			}
			
		});*/
		mSelectedTitle = (TextView)findViewById(R.id.sound_selected);
		
		mSoundList = (ListView)findViewById(R.id.sound_list);
		mNextSongs = new SoundFileAdapter(LiveActivity.this, R.layout.songs, getAudioResources());
		mSoundList.setAdapter(mNextSongs);
	
		mSoundList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Uri uri = (Uri) arg0.getItemAtPosition(arg2);
				
				mSoundButton.setSoundUri(uri);
				String duration = mSoundButton.getDurationString();
				mSelectedTitle.setText("SOUND LIST: " + 
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
	public void onWindowFocusChanged(boolean hasFocus){
		
		/* Change the size of thumb for MIC */
		Resources micres = getResources();
        Drawable micthumb = micres.getDrawable(R.drawable.micnob);
        Bitmap originalnob = ((BitmapDrawable)micthumb).getBitmap();
        
        int mich = (int) (mMicBar.getMeasuredHeight() * 1.3);
        int micw = mich /2;
        Bitmap scaledmicnob = Bitmap.createScaledBitmap(originalnob, micw, mich, true);
        Drawable newMicThumb = new BitmapDrawable(micres, scaledmicnob);
        newMicThumb.setBounds(0, 0, newMicThumb.getIntrinsicWidth(), newMicThumb.getIntrinsicHeight());
        mMicBar.setThumb(newMicThumb);
        
        /* Change the size of thumb for Sound */
        Resources soundres = getResources();
        Drawable soundthumb = soundres.getDrawable(R.drawable.soundnob);
        Bitmap originalsoundthumb = ((BitmapDrawable)soundthumb).getBitmap();
        
        int soundh = (int) (mSoundBar.getMeasuredHeight() * 1.3);
        int soundw = soundh/2;
        Bitmap scaledsoundnob = Bitmap.createScaledBitmap(originalsoundthumb, soundw, soundh, true);
        Drawable newSoundThumb = new BitmapDrawable(soundres, scaledsoundnob);
        newSoundThumb.setBounds(0, 0, newSoundThumb.getIntrinsicWidth(), newSoundThumb.getIntrinsicHeight());
        mSoundBar.setThumb(newSoundThumb);
		
        
        super.onWindowFocusChanged(hasFocus);
	}
	private ArrayList<Uri> getAudioResources(){
        ArrayList<Uri> tracks = new ArrayList<Uri>();
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
                        Track.COLUMNS, 
                        null,
                        null,
                        null);
        while( cursor.moveToNext() ){
                tracks.add( ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                		cursor.getLong( cursor.getColumnIndex( MediaStore.Audio.Media._ID )) ));
        }
        cursor.close();
        return tracks;		
	}
	
	
//	private void showFileChooser(){
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//		intent.setType("*/*");
	//		intent.addCategory(Intent.CATEGORY_OPENABLE);	
	//startActivityForResult(Intent.createChooser(intent	, "Select a sound file to play"),
	//		CODE_SOUND_URI);
	//
	//}

	/*
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
*/

	@Override
	protected void onPause() {
		
		mSoundButton.stopSound();
		mMicButton.stopMic();
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}


	
	
}

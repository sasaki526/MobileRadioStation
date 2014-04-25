package com.mobilestation.bobileradiostation.views;


import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.view.View;
import android.widget.Button;

/**
 * Button for Line out from MIC in.
 * 
 * This button is intended to be used toggle button to get the voice from MIC
 * and provide it to the jack.
 * 
 * By clicking this button, real time play starts by getting voice through MIC.
 * and then, you could stop it by clicking again.
 */
public class MicButton extends Button {
	
	//private static final String TAG = "MicButton";
	
	MicButtonHelper mMic = null;
	Thread mRT  = null;
	AudioManager mAudioManager = null;
	
	
	OnClickListener clicker = new OnClickListener(){

		@Override
		public void onClick(View v) {
			onMic();
		}
		
	};

	/**
	 * Contor
	 * @param context
	 */
	public MicButton(Context context) {
		super(context);
		mMic = new MicButtonHelper();
		setOnClickListener(clicker);
		setText("MIC");
		
		mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	}

	/**
	 * Returns The MAX volume index of music stream.
	 * @return The maximum valid volume index.
	 */
	public int getMaxVolume(){
		return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}
	
	/**
	 * Sets the volume index for left and right.
	 *  
	 * @param l left volume index
	 * @param r right volume index
	 */
	public void setLRVolume(float l, float r){
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		mMic.setLRVolume(l, r);
		
	}

	/**
	 * Interface method to start and stop your play
	 */
	public void onMic(){
		if ( mMic.isRunning() ){
			stopMic();
		}else {
			startMic();
		}
		
	}
	
	
	/**
	 * Stops reading from MIC and writing your voice.
	 */
	public void stopMic(){
		
		if ( mRT == null ){
			return;
		}
		
		mMic.terminate();
		try {
			mRT.join();
			setText("MIC OFF");
			setTextColor(Color.rgb(0, 100, 0));			

		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		
	}
	
	/**
	 * DJ Play Starts
	 */
	private void startMic(){
		mRT = new Thread(mMic);
		mMic.getSet();
		mRT.start();
		
		setText("ON AIR");
		setTextColor(Color.RED);

	}
	
	
	

}

package com.mobilestation.mobileradiostation.views;


import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Button for Line out from MIC in.
 * 
 * This button is intended to be used toggle button to get the voice from MIC
 * and provide it to the jack.
 * 
 * By clicking this button, real time play starts by getting voice through MIC.
 * and then, you could stop it by clicking again.
 */
public class MicButton extends ImageView {
	
	//private static final String TAG = "MicButton";
	
	private TextView text = null;
	
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

	public MicButton(Context context, TextView mMicStatusLabel) {
		super(context);
		mMic = new MicButtonHelper();
		setOnClickListener(clicker);
		
		mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	
		text = mMicStatusLabel;
		text.setText("MIC");
		
	}

	/**
	 * Returns The MAX volume index of music stream.
	 * @return The maximum valid volume index.
	 */
	public float getMaxVolume(){
		Log.i("MAXVolume(MIC):",String.valueOf(AudioTrack.getMaxVolume()));
		Log.i("minVolume(MIC):",String.valueOf(AudioTrack.getMinVolume()));

		return AudioTrack.getMaxVolume();
	}
	
	/**
	 * Sets the volume index for left and right.
	 *  
	 * @param l left volume index
	 * @param r right volume index
	 */
	public void setLRVolume(float l, float r){
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
			text.setText("MIC OFF");
			text.setTextColor(Color.rgb(0, 100, 0));			

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
		
		text.setText("ON AIR");
		text.setTextColor(Color.RED);

	}
	
	
	

}

package com.mobilestation.mobileradiostation.views;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.mobilestation.mobileradiostation.R;

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
	
	MicButtonHelper mMic = null;
	Thread mRT  = null;
	AudioManager mAudioManager = null;
	Handler mHandler;

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
		this(context, null);
	}

	public MicButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MicButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		mMic = new MicButtonHelper();
		this.setImageResource(R.drawable.round_background);
		this.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
		setOnClickListener(clicker);

		mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

		mHandler = new Handler();
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
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					MicButton.this.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
				}
			});
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

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				MicButton.this.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
			}
		});
	}
	
	
	

}

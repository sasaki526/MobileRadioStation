package com.mobilestation.mobileradiostation.views;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

public class MicButtonHelper implements Runnable {
	
	private static final String TAG = "MicInOutProvider";
	
	private AudioRecord mMicIn = null;
	private AudioTrack mMicOut = null;
	
	private volatile boolean mRunning;
	private float mLeftVolume;
	private float mRightVolume;
	
	
	/**
	 * Contor.
	 */
	public MicButtonHelper(){
		mRunning = false;
		mLeftVolume = 0;
		mRightVolume = 0;
	}
	
	
	/**
	 * Sets the volume index for left and right.
	 * @param l left
	 * @param r right
	 */
	public void setLRVolume(float l, float r){
		mLeftVolume = l;
		mRightVolume = r;
	}
	
	/** 
	 * Force to stop routing voice from MIC to speaker
	 */
	public void terminate(){
		mRunning = false;
	}
	
	/**
	 * Returns true if playing back voice
	 * @return
	 */
	public boolean isRunning(){
		return mRunning;
	}

	/**
	 * Makes the status running.
	 * @param run
	 */
	public void getSet(){
		mRunning = true;
	}
	/**
	 * Execute to play
	 */
	@Override
	public void run() {
		Log.i(TAG, "MIC:TRUE");
		/* MIC ON */
		int freq = 8000;
		int bufferSize = AudioRecord.getMinBufferSize(freq,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mMicIn = new AudioRecord(MediaRecorder.AudioSource.MIC, freq,
                AudioFormat.CHANNEL_IN_MONO,
                MediaRecorder.AudioEncoder.AMR_NB, bufferSize);
        mMicIn.startRecording();

        /* Speaker ON */
        mMicOut = new AudioTrack(AudioManager.STREAM_MUSIC,
        		freq,
                AudioFormat.CHANNEL_OUT_MONO,
                MediaRecorder.AudioEncoder.AMR_NB, 
                bufferSize,
                AudioTrack.MODE_STREAM);
        mMicOut.setPlaybackRate(freq);
        mMicOut.play();
        
        /* Feeding Voice */
        byte[] buffer = new byte[bufferSize];
	    while (mRunning) {
               try {
            	  
                   mMicIn.read(buffer, 0, bufferSize);
                   mMicOut.write(buffer, 0, buffer.length);
                   mMicOut.setStereoVolume(mLeftVolume,mRightVolume);
                   
               } catch (Throwable t) {
              	 Log.e("Error", "Read write failed");
              	 t.printStackTrace();
               }
         }
	    
        mMicOut.stop();
        mMicOut.release();
        mMicOut = null;

        mMicIn.stop();
        mMicIn.release();
        mMicIn = null;

	}

}

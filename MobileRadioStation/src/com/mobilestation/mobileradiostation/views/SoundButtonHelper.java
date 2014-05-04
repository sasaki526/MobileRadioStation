package com.mobilestation.mobileradiostation.views;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.mobilestation.mobileradiostation.Utils;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTimestamp;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * Play the sound you selected.
 * 
 * @author masa
 *
 */
public class SoundButtonHelper implements Runnable {
	
	Context mContext;
	
	/* the uri indicates sound file. (*.wav file for now ) */
	Uri mUri;
	
	/* Status */
	volatile boolean mRunning;
	
	/* Volume index */
	float mLeftVolume;
	float mRightVolume;
	
	int mDuration;
	int mElapsedTime;
	
	/**
	 * Contor
	 * @param context
	 */
	public SoundButtonHelper(Context context){
		mContext = context;
		mUri = null;
		mRunning = false;
		mLeftVolume = 0;
		mRightVolume = 0;
		mDuration = 0;
		mElapsedTime = 0;
	}
	
	/**
	 * Sets the uri
	 * @param uri
	 */
	public void setSoundUri(Uri uri){
		mUri = uri;
	}

	/**
	 * Returns the status
	 * @return
	 */
	public boolean isRunning(){
		return mRunning;
	}
	
	
	/**
	 * Force to stop playing.
	 */
	public void terminate(){
		mRunning = false;
	}
	
	public int getDuration(){
		return mDuration;
	}
	
	public void setDuration(){
		mDuration = Utils.uriToDurationInmsec(mContext, mUri);
	}
	public int getCurrentDuration(){
		return mElapsedTime;
	}
	/**
	 * Sets the volume index for left and right
	 * @param l left volume index
	 * @param r right volume index
	 */
	public void setLRVolume(float l, float r){
		mLeftVolume = l;
		mRightVolume = r;
	}
	
	/**
	 * Get ready.
	 * 
	 * Call this before starting thread.
	 */
	public boolean getSet(){

		if ( mUri == null ){
			Toast.makeText(mContext, "Please Select Sound file you want to play.", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		
		if ( !Utils.isPCM(mContext,mUri) || !Utils.is44100Hz(mContext,	mUri)){
			Toast.makeText(mContext, "Sorry. 44100Hz and 16bps required for now.", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		setDuration();
		
		mRunning = true;
		return true;
	}
	/**
	 * Play the sound.
	 */
	@Override
	public void run() {
		
		
		int bufSize = AudioTrack.getMinBufferSize(44100, 
					AudioFormat.CHANNEL_IN_STEREO, 
					AudioFormat.ENCODING_PCM_16BIT);
	    
	    AudioTrack anTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 
	    								44100, 
	    								AudioFormat.CHANNEL_OUT_STEREO, 
	    								AudioFormat.ENCODING_PCM_16BIT, 
	    								bufSize, 
	    								AudioTrack.MODE_STREAM);
	    
	    
		anTrack.setPlaybackPositionUpdateListener(new OnPlaybackPositionUpdateListener(){

			@Override
			public void onMarkerReached(AudioTrack arg0) {
				
			}

			@Override
			public void onPeriodicNotification(AudioTrack arg0) {
				
			}
	    	
	    });
	    
		int i = 0;
		int workbuffer = 512;
	    byte[] pieceOfMusic = new byte[workbuffer];
	    long dataWritten = 0;
		try {
	    	
	    	
	    	InputStream in = mContext.getContentResolver().openInputStream(mUri);
	        
	        
	        DataInputStream soundStream = new DataInputStream(in);

	        anTrack.play();
	        while(mRunning && (i = soundStream.read(pieceOfMusic, 0, workbuffer)) > -1){
	        	
	            dataWritten += anTrack.write(pieceOfMusic, 0, i);
	            anTrack.setStereoVolume(mLeftVolume, mRightVolume);
	            mElapsedTime = (int) (mDuration - (dataWritten * 1000 / (44100.0 * 2 * 2)));
	        }
	        anTrack.stop();
	        anTrack.release();
	        anTrack = null;
	        soundStream.close();
	        in.close();

	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	}

	public float getMinVolume() {
		return AudioTrack.getMinVolume();
	}

	public float getMaxVolue() {
		return AudioTrack.getMaxVolume();
	}

}

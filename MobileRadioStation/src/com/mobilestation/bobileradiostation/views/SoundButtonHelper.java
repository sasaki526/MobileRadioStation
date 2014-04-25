package com.mobilestation.bobileradiostation.views;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
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
	    
	    int i = 0;
		int workbuffer = 512;
	    byte[] pieceOfMusic = new byte[workbuffer];
	    try {
	    	
	    	
	        InputStream in = mContext.getContentResolver().openInputStream(mUri);
	        DataInputStream soundStream = new DataInputStream(in);

	        anTrack.play();
	        while(mRunning && (i = soundStream.read(pieceOfMusic, 0, workbuffer)) > -1){
	        	
	            anTrack.write(pieceOfMusic, 0, i);
	            anTrack.setStereoVolume(mLeftVolume, mRightVolume);
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

}

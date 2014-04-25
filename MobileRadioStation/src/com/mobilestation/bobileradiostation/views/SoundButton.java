package com.mobilestation.bobileradiostation.views;


import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;

/**
 * Button for playing music.
 * 
 * By Clicking this button, you could play your music selected.
 * and then, you can stop the music by clicking again.
 * 
 * @author masa
 *
 */
public class SoundButton extends Button {

	private SoundButtonHelper mSoundProvider = null;
	private Thread mRT = null;
	private AudioManager mAudioManager = null;
	
	
	/**
	 * Contor
	 * @param context
	 */
	public SoundButton(Context context) {
		super(context);
		mSoundProvider = new SoundButtonHelper(context);
		mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		setText("Sound");
		
	}

	/**
	 * Set the uri indicates your sound file 
	 * which contains PCM data specifically 44100Hz sampling rated for now.
	 * 
	 * @param uri
	 */
	public void setSoundUri(Uri uri ){
		mSoundProvider.setSoundUri(uri);
	}
	
	/**
	 * Play and stop the music.
	 * 
	 * Interface method for play
	 */
	public void onPlay(){
		if ( mSoundProvider.isRunning() ){
			Log.i("test","true");
			stopSound();
		}else {
			Log.i("test","FALSE");
			startSound();
		}
		
	}
	
	/**
	 * Stops the music.
	 * Implementation method to stop the music.
	 */
	public void stopSound(){
		

		if ( mRT == null ){
			return;
		}
		
		mSoundProvider.terminate();
		try {
			mRT.join();
			mRT = null;
			setText("Play");
			setTextColor(Color.rgb(0, 100, 0));			

		} catch (InterruptedException e) {
			e.printStackTrace();
		}		

	}
	
	/**
	 * Starts your sound.
	 */
	private void startSound(){
		mRT = new Thread(mSoundProvider);
		if ( mSoundProvider.getSet() ){
			mRT.start();

			setText("Stop");
			setTextColor(Color.RED);
			
		}

	}

	/**
	 * Return the maximum volume index you can set.
	 * @return
	 */
	public int getMaxVolume(){
		return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}
	
	/**
	 * Sets the volume index for left and right.
	 * 
	 * @param l left volume index.
	 * @param r right volume index.
	 */
	public void setLRVolume(float l, float r) {
		mSoundProvider.setLRVolume(l, r);
		
	}

	/**
	 * Returns the status of sound play 
	 * @return true when running otherwise false.
	 */
	public boolean isRunning() {
		return mSoundProvider.isRunning();
	}
	

	
}


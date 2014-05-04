package com.mobilestation.mobileradiostation.views;


import java.util.Timer;
import java.util.TimerTask;

import com.mobilestation.mobileradiostation.R;
import com.mobilestation.mobileradiostation.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Button for playing music.
 * 
 * By Clicking this button, you could play your music selected.
 * and then, you can stop the music by clicking again.
 * 
 * @author masa
 *
 */
public class SoundButton extends ImageView {

	private TextView text = null;
	private SoundButtonHelper mSoundProvider = null;
	private Thread mRT = null;
	private AudioManager mAudioManager = null;
	
	private Timer mTimer = null;
	class UpdateTime extends TimerTask {

		private Handler handle = new Handler();
		@Override
		public void run() {
			
			handle.post(new Runnable (){		
				@Override
				public void run() {
					text.setText(Utils.foramtTime(mSoundProvider.getCurrentDuration()));	
							
				}
			});
		}
	
	}
	/**
	 * Contor
	 * @param context
	 */
	public SoundButton(Context context, TextView mSoundStatusLabel, String label) {
		super(context);
		mSoundProvider = new SoundButtonHelper(context);
		mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		
	
		text = mSoundStatusLabel;
		text.setText(label);
			
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
			text.setText("Play");
			text.setTextColor(Color.rgb(0, 100, 0));	
			
			mTimer.cancel();

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
			text.setText("Stop");
			text.setTextColor(Color.RED);
			
			mTimer = new Timer();
			UpdateTime tt = new UpdateTime();
			mTimer.scheduleAtFixedRate(tt, 1000, 1000);
			
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

	public String getDurationString() {
		mSoundProvider.setDuration();
		return Utils.foramtTime(mSoundProvider.getDuration());
	}
	

	
}


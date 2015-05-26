package com.mobilestation.mobileradiostation.views;


import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.mobilestation.mobileradiostation.R;
import com.mobilestation.mobileradiostation.Utils;
import com.mobilestation.mobileradiostation.myplayer.BasePlayer;
import com.mobilestation.mobileradiostation.myplayer.Mp3Player;
import com.mobilestation.mobileradiostation.myplayer.WavPlayer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.style.UpdateAppearance;
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
    private Context mContext;

	/* Display 'PLAY', 'STOP' or Elapsed Time */
	private TextView text = null;
	
	/* Actually Play Music On Another Thread (mRT). */
	//private SoundButtonHelper mSoundProvider = null; // ym
    private BasePlayer mPlayer;
	private Thread mRT = null;

    private float mLeftVol;
    private float mRightVol;

	/* Timer for Update View to display Elapsed Time */
	private Timer mTimer = null;

    private static final int MP3_BUFSIZE = 1024;
    private static final String MP3_EXT = ".mp3";
    private static final String WAV_EXT = ".wav";

	/**
	 * HELPER
	 * 
	 * Show Elapsed Time 
	 */
	class UpdateTime extends TimerTask {

		private Handler handle = new Handler();
		@Override
		public void run() {		
			handle.post(new Runnable (){		
				@Override
				public void run() {
					//text.setText(Utils.formatTime(mSoundProvider.getElapsedTime())); // ym
                    text.setText(Utils.formatTime(mPlayer.getElapsedTime()));
					
				}
			});
		}
	
	}
	/**
	 * Contor
	 * @param context
	 */
	public SoundButton(Context context, TextView mSoundStatusLabel, String label, boolean repeat) {
		super(context);
//		mSoundProvider = new SoundButtonHelper(context,repeat); // ym
	    mContext = context;
		text = mSoundStatusLabel;
		text.setText(label);
			
	}

	/**
	 * Set the uri indicates your sound file 
	 * which contains PCM data specifically 44100Hz sampling rated for now.
	 * 
	 * @param uri
	 */
	public void setSoundUri(Uri uri )
    {
		//mSoundProvider.setSoundUri(uri);
        if(uri!=null) {
            String path = getPath(uri);
            if (path.endsWith(MP3_EXT)) {
                mPlayer = new Mp3Player(path, MP3_BUFSIZE);
            } else if (path.endsWith(WAV_EXT)) {
                mPlayer = new WavPlayer(path);
            }
        }
	} // ym

    public void setLoop(boolean loop){
        if(mPlayer!=null){
            mPlayer.setLoop(loop);
        }
    }
	
	/**
	 * Play and stop the music.
	 * 
	 * Interface method for play
	 */
	public void onPlay(){ // ym
//		if ( mSoundProvider.isRunning() ){
        if(mPlayer != null) {
            if (mPlayer.isPlaying()) {
                Log.i("test", "true");
                stopSound();

            } else {
                Log.i("test", "FALSE");
                startSound();
            }
        }
		
	}
	
	/**
	 * Stops the music.
	 * Implementation method to stop the music.
	 */
	public void stopSound(){
        if(mPlayer!=null){
            mPlayer.stop();
            text.setText("Play");
            text.setTextColor(Color.rgb(0, 100, 0));
        }
        if(mTimer!=null){
            mTimer.cancel();
        }
//		if ( mRT == null ){
//			return;
//		}
//
//		mSoundProvider.terminate(); // ym
//		try {
//			mRT.join();
//			mRT = null;
//			text.setText("Play");
//			text.setTextColor(Color.rgb(0, 100, 0));
//
//			mTimer.cancel();
//
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
	}




	/**
	 * Starts your sound.
	 */
	private void startSound(){
        if(mPlayer != null) {
            mTimer = new Timer();
            mTimer.schedule(new UpdateTime(), 0, 1000);
            mPlayer.setLRVolume(mLeftVol, mRightVol);
            mPlayer.play();
            text.setText("Stop");
            text.setTextColor(Color.RED);
        }
//		mRT = new Thread(mSoundProvider);
//		if ( mSoundProvider.getSet() ){
//			mRT.start();
//			text.setText("Stop");
//			text.setTextColor(Color.RED);
//
//			mTimer = new Timer();
//			UpdateTime tt = new UpdateTime();
//			mTimer.scheduleAtFixedRate(tt, 1000, 1000);
//
//		}
//
	}

	/**
	 * Return the maximum volume index you can set.
	 * @return
	 */
	public float getMaxVolume(){ // ym
//		Log.i("MAXVolume(Sound):",String.valueOf(mSoundProvider.getMaxVolue()));
//		Log.i("minVolume(Sound):",String.valueOf(mSoundProvider.getMinVolume()));

//        return mSoundProvider.getMaxVolue();
        return AudioTrack.getMaxVolume();
	}
	
//	public float getMinVoluem(){
//		return mSoundProvider.getMinVolume();
//	} // ym
    public float getMinVolume(){
        return AudioTrack.getMinVolume();
    }

	/**
	 * Sets the volume index for left and right.
	 * 
	 * @param l left volume index.
	 * @param r right volume index.
	 */
	public void setLRVolume(float l, float r) { // ym
		//mSoundProvider.setLRVolume(l, r);
        if(mPlayer!=null){
            mPlayer.setLRVolume(l, r);
        }
        mLeftVol = l;
        mRightVol = r;
	}

	/**
	 * Returns the status of sound play 
	 * @return true when running otherwise false.
	 */
//	public boolean isRunning() {
//		return mSoundProvider.isRunning();
//	} // ym
    public boolean isRunning() {
        if(mPlayer != null){
            return mPlayer.isPlaying();
        } else {
            return false;
        }

    }


	public String getDurationString() {
//		mSoundProvider.setDuration(); // ym
//		return Utils.foramtTime(mSoundProvider.getDuration()); // ym
        return Utils.formatTime(mPlayer.getDuration());
	}
	
    private String getPath(Uri uri){
        ContentResolver contentResolver = mContext.getContentResolver();
        String[] columns = { MediaStore.Audio.Media.DATA };
        Cursor cursor = contentResolver.query(uri, columns, null, null, null);
        cursor.moveToFirst();
        String path = cursor.getString(0);
        cursor.close();
        return path;
    }
	
}


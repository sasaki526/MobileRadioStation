package com.mobilestation.mobileradiostation.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.mobilestation.mobileradiostation.R;
import com.mobilestation.mobileradiostation.Utils;
import com.mobilestation.mobileradiostation.myplayer.BasePlayer;
import com.mobilestation.mobileradiostation.myplayer.Mp3Player;
import com.mobilestation.mobileradiostation.myplayer.WavPlayer;

import java.util.Timer;
import java.util.TimerTask;

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

    private int mColor;
    private boolean mRepeat;

	/* Actually Play Music On Another Thread (mRT). */
	//private SoundButtonHelper mSoundProvider = null; // ym
    private BasePlayer mPlayer;
	private Thread mRT = null;

    private float mLeftVol;
    private float mRightVol;

    private SoundButtonListener mListener;

	/* Timer for Update View to display Elapsed Time */
	private Timer mTimer = null;
    private Handler mHandler;

    private static final int MP3_BUFSIZE = 1024;
    private static final String MP3_EXT = ".mp3";
    private static final String WAV_EXT = ".wav";

    public interface SoundButtonListener {
        public void onReachEnd();
        public void onTimeChanged(long elapsedTime);
    }

	/**
	 * HELPER
	 * 
	 * Show Elapsed Time 
	 */
	class UpdateTime extends TimerTask {
//		@Override
		public void run() {		
			mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onTimeChanged(mPlayer.getElapsedTime());
                }
			});
		}
	}
	/**
	 * Contor
	 * @param context
	 */
	public SoundButton(Context context) {
		this(context, null);
	}

    public SoundButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SoundButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.SoundButton);
        mColor = typedArray.getColor(R.styleable.SoundButton_color, Color.BLACK);
        mRepeat = typedArray.getBoolean(R.styleable.SoundButton_repeat, false);
        mHandler = new Handler();
        this.setColorFilter(mColor, PorterDuff.Mode.SRC_IN);
        this.setImageResource(R.drawable.play_button);
    }


	/**
	 * Set the uri indicates your sound file 
	 * which contains PCM data specifically 44100Hz sampling rated for now.
	 * 
	 * @param uri
	 */
	public void setSoundUri(Uri uri ) {
		//mSoundProvider.setSoundUri(uri);
        if(uri!=null) {
            String path = Utils.getPath(mContext, uri);
            if (path.endsWith(MP3_EXT)) {
                mPlayer = new Mp3Player(path, MP3_BUFSIZE);
            } else if (path.endsWith(WAV_EXT)) {
                mPlayer = new WavPlayer(path);
            }
            if (mPlayer != null) {
                mPlayer.setListener(new BasePlayer.PlayStatusListener() {
                    @Override
                    public void onReachEnd() {
                        if (mListener != null) {
                            mListener.onReachEnd();
                        }
                    }
                });
            }
        }
	} // ym

    public void setLoop(boolean loop) {
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
                stopSound();
            } else {
                startSound();
            }
        }
		
	}
	
	/**
	 * Stops the music.
	 * Implementation method to stop the music.
	 */
	public void stopSound() {
        if(mPlayer!=null){
            mPlayer.stop();
//            mPlayer.pause();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    SoundButton.this.setImageResource(R.drawable.play_button);
                }
            });
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
	public void startSound() {
        if(mPlayer != null) {
            mTimer = new Timer();
            mTimer.schedule(new UpdateTime(), 0, 1000);
            mPlayer.setLRVolume(mLeftVol, mRightVol);
            mPlayer.play();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    SoundButton.this.setImageResource(R.drawable.pause_button);
                }
            });
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
     * Pauses your sound
     */
    public void pauseSound() {
        if(mPlayer != null) {
            mPlayer.pause();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    SoundButton.this.setImageResource(R.drawable.play_button);
                }
            });
        }

        if(mTimer!=null){
            mTimer.cancel();
        }
    }

	/**
	 * Return the maximum volume index you can set.
	 * @return
	 */
	public float getMaxVolume() { // ym
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

    public String getPath() {
        if (mPlayer == null) {
            return null;
        } else {
            return mPlayer.getPath();
        }
    }


	public String getDurationString() {
//		mSoundProvider.setDuration(); // ym
//		return Utils.foramtTime(mSoundProvider.getDuration()); // ym
        return Utils.formatTime(mPlayer.getDuration());
	}

    public void setListener(SoundButtonListener listener) {
        mListener = listener;
    }
	
}


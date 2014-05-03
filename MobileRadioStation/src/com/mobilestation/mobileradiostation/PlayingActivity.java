package com.mobilestation.mobileradiostation;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.mobilestation.mobileradiostation.RecordActivity.MyTimerTask;
import com.mobilestation.mobileradiostation.controller.TrackListAdapter;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PlayingActivity extends Activity {
	
	private String mFilePath;
	private String mTitle;
	private long mDuration;
	
	private TextView mTvTitle;
	private TextView mTvPlayed;
	private TextView mTvDuration;
	private ImageView mIvPlayPause;
	private ProgressBar mProgBar;
	
	private MediaPlayer mMediaPlayer;
	private int mResumePoint;
	
	private MyTimerTask mTimerTask = null;
	private Timer   mTimer   = null;
	private Handler mHandler = new Handler();

	private static final String TAG = MainActivity.TAG;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.playing_main);
		
		mFilePath = getIntent().getStringExtra(Utils.FILE_PATH);
		mTitle = getIntent().getStringExtra(Utils.TITLE);
		mDuration = getIntent().getLongExtra(Utils.DURATION, 0);
		
		mTvTitle = (TextView)findViewById(R.id.txt_playing_title);
		mTvPlayed = (TextView)findViewById(R.id.txt_now_played);
		mTvDuration = (TextView)findViewById(R.id.txt_total_length);
		mIvPlayPause = (ImageView)findViewById(R.id.img_play_pause);
		mProgBar = (ProgressBar)findViewById(R.id.progress_play);
		
		mTvTitle.setText(mTitle);
		mTvDuration.setText(TrackListAdapter.msToTime(mDuration));
		mTvPlayed.setText(TrackListAdapter.msToTime(0));
		mProgBar.setMax((int)(mDuration/1000));
		mIvPlayPause.setBackgroundResource(R.drawable.play);
		
		mResumePoint = 0;
		mMediaPlayer = new MediaPlayer();
		
		mIvPlayPause.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mMediaPlayer.isPlaying()){
					mMediaPlayer.pause();
            		mResumePoint = mMediaPlayer.getCurrentPosition();
            		mIvPlayPause.setBackgroundResource(R.drawable.play);
				} else {
					if(mResumePoint==0){
						//playAudio(mFilePath);
						playAudio();
						mTimerTask = new MyTimerTask();
		                mTimer = new Timer(true);
		                mTimer.schedule(mTimerTask, 0, 100);
					} else {
						resumeAudio(mResumePoint);
					}
					mIvPlayPause.setBackgroundResource(R.drawable.pause);
				}
			}
		});
	}
	
//	private void playAudio(String path){
	private void playAudio(){
		try {
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(mFilePath);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void resumeAudio(int resumePoint){
		mMediaPlayer.seekTo(resumePoint);
		mMediaPlayer.start();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		mMediaPlayer.pause();
		mResumePoint = 0;
		if(mTimer!=null){
			mTimer.cancel();
			mTimer = null;
		}
	}
	
	class MyTimerTask extends TimerTask{
	     @Override
	     public void run() {
	         // mHandlerを通じてUI Threadへ処理をキューイング
	         mHandler.post( new Runnable() {
	             public void run() {
	            	 long currentTime = (long)mMediaPlayer.getCurrentPosition();
	                 mProgBar.setProgress((int)currentTime/1000);
	                 mTvPlayed.setText(TrackListAdapter.msToTime(currentTime));
	             }
	         });
	     }
	 }

}
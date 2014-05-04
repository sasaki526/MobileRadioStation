package com.mobilestation.mobileradiostation;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.mobilestation.mobileradiostation.controllers.TrackListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RecordActivity extends Activity implements View.OnClickListener {
	final static String EXTERNAL_DIR = Environment.getExternalStorageDirectory().getPath();
	private MediaRecorder recorder = null;
	//private Button btn = null;
	private TextView mTvLiveMode;
	private ImageView mIvRec;
	private TextView mTvTime;
	boolean bIsRecording = false;
	private String mFileName;
	private String mFilePath;
	
	private MyTimerTask mTimerTask = null;
	private Timer   mTimer   = null;
	private Handler mHandler = new Handler();
	private long mStartTime;
	
	private String mMode;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_main);

		mIvRec = (ImageView)findViewById(R.id.img_rec);
		mTvTime = (TextView)findViewById(R.id.txt_recorded_time);
		mTvLiveMode = (TextView)findViewById(R.id.txt_live_mode);
		mIvRec.setBackgroundResource(R.drawable.record);
		mIvRec.setOnClickListener(this);
		// MediaRecoderÇÃçÏê¨
		recorder = new MediaRecorder();
		
		Intent intent = getIntent();
		mMode = intent.getStringExtra(Utils.RECORD_MODE);
		
		if(mMode.equals(Utils.MODE_LIVE)){
			mIvRec.setVisibility(View.GONE);
			//mTvTime.setVisibility(View.GONE);
		} else if(mMode.equals(Utils.MODE_RECORD)){
			mTvLiveMode.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
		if(mMode.equals(Utils.MODE_LIVE)){
			mStartTime = System.currentTimeMillis();
			mTimerTask = new MyTimerTask();
            mTimer = new Timer(true);
            mTimer.schedule(mTimerTask, 0, 100);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mIvRec) {
			if (!bIsRecording) {
				Calendar cal = Calendar.getInstance();
				String year = Integer.toString(cal.get(Calendar.YEAR));
				String month = Integer.toString(cal.get(Calendar.MONTH));
				String dayOfMonth = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
				String hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
				String minute = Integer.toString(cal.get(Calendar.MINUTE));
				String second = Integer.toString(cal.get(Calendar.SECOND));
				mFileName =year+month+dayOfMonth + "_" + hour+minute+second + ".3gp"; 
				mFilePath = EXTERNAL_DIR + "/" + mFileName;
				
				Log.d("seto", "filename: "+mFilePath);
				
				recorder.setOutputFile(mFilePath);
				recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				try {
					recorder.prepare();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// ò^âπäJén
				recorder.start();
				mStartTime = System.currentTimeMillis();
				mIvRec.setBackgroundResource(R.drawable.stop);
				
				mTimerTask = new MyTimerTask();
                mTimer = new Timer(true);
                mTimer.schedule(mTimerTask, 0, 100);
                
				bIsRecording = true;
			} else {
				// ò^âπí‚é~
				recorder.stop();
				recorder.reset();
				mIvRec.setBackgroundResource(R.drawable.record);
				bIsRecording = false;
				
				mTimer.cancel();
                mTimer = null;
				
				String[] paths = {mFilePath};
				String[] mimeTypes = {"audio/3gp"};
				MediaScannerConnection.scanFile(getApplicationContext(),
				                                paths,
				                                mimeTypes,
				                                null);

				String toastText = "Audio was saved. File name is "+mFileName;
				Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
				
				finish();
			}
		}
	}
	
	class MyTimerTask extends TimerTask{
	     @Override
	     public void run() {
	         mHandler.post( new Runnable() {
	             public void run() {
	            	 String timeStr = TrackListAdapter.msToTime(System.currentTimeMillis()-mStartTime); 
	             
	                 mTvTime.setText(timeStr);
	             }
	         });
	     }
	 }
	@Override
	protected void onDestroy() {
		super.onDestroy();
		recorder.release();
	}	

}

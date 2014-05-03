package com.mobilestation.mobileradiostation;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.mobilestation.mobileradiostation.RecordActivity.MyTimerTask;
import com.mobilestation.mobileradiostation.controllers.TrackListAdapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class EditActivity extends Activity{
	private TextView mTvTitle;
	private TextView mTvStartTime;
	private TextView mTvEndTime;
	
	private TextView mTvOnAirTime;
	
	private ImageView mIvStart;
	private ImageView mIvEnd;
	private ImageView mIvStartScreen;
	private ImageView mIvEndScreen;	
	private ImageView mIvPlay;
	private ImageView mIvCut;
	private ImageView mIvPlan;
	private LinearLayout mLlStartTime;
	
	private MyTimerTask mTimerTask = null;
	private Timer   mTimer   = null;
	private Handler mHandler = new Handler();
	
	private String mFilePath;
	private String mTitle;
	private long mDuration;
	
	private String mDateStr;
	private String mTimeStr;
	
	private int mStartX;
	private int mStartY;
	private int mOrigX;

	private int mLineY;	
	private int mTimeY;
	
	private int mWindowWidth;
	private int mLineLength;
	
	DatePickerDialog datePickerDialog;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_main);
		
		mTvTitle = (TextView)findViewById(R.id.txt_edit_title);
		mTvStartTime = (TextView)findViewById(R.id.txt_cut_start);
		mTvEndTime = (TextView)findViewById(R.id.txt_cut_end);
		mIvStart = (ImageView)findViewById(R.id.line_start);
		mIvEnd = (ImageView)findViewById(R.id.line_end);
		mIvPlay = (ImageView)findViewById(R.id.img_edit_play);
		mIvCut = (ImageView)findViewById(R.id.img_edit_cut);
		mIvPlan = (ImageView)findViewById(R.id.img_edit_set_time);
		mIvStartScreen = (ImageView)findViewById(R.id.img_start_screen);
		mIvEndScreen = (ImageView)findViewById(R.id.img_end_screen);
		mTvOnAirTime = (TextView)findViewById(R.id.txt_onair);
		//mLlStartTime = (LinearLayout)findViewById(R.id.linear_start_time);
		
		mFilePath = getIntent().getStringExtra(Utils.FILE_PATH);
		mTitle = getIntent().getStringExtra(Utils.TITLE);
		mDuration = getIntent().getLongExtra(Utils.DURATION, 0);

		mTvTitle.setText(mTitle);
		mIvCut.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					Log.d("hoge", "down");
					mIvCut.setBackgroundResource(R.drawable.cut_touch1);
					mTimerTask = new MyTimerTask();
		            mTimer = new Timer(true);
		            mTimer.schedule(mTimerTask, 200, 100);
					break;
				case MotionEvent.ACTION_UP:
					Log.d("hoge", "up");
					
					break;
				}
			return false;	
			}
		});

		mIvStart.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// タッチしている位置取得
				int x = (int) event.getRawX();
				int y = (int) event.getRawY();
				
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					Log.d("hoge", "down");
					//mOrigX = mRelativeStart.getRight();
					mOrigX = view.getLeft();
					mStartX = x;
					break;
				case MotionEvent.ACTION_MOVE:
					Log.d("hoge", "move");
					int left = mOrigX+(x-mStartX);
					view.layout(left, mLineY, left+view.getWidth(), mLineY+view.getHeight());
					mIvStartScreen.layout(0, mLineY, left+view.getWidth()/2, mLineY+view.getHeight());
					mTvStartTime.setText(TrackListAdapter.msToTime(left*mDuration/mTvTitle.getWidth()));
					//mLlStartTime.layout(left-mTvStartTime.getWidth()/2, mTimeY, left+mTvStartTime.getWidth()/2, mTimeY+mTvStartTime.getHeight());
			//mIvStartScreen.layout(0, mLineY, left, mLineY+mLineLength);
					//RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(left, mIvStartScreen.getHeight());
					//mIvStartScreen.setLayoutParams(lp);
					break;
				case MotionEvent.ACTION_UP:
					Log.d("hoge", "up");
					break;
				}
				
				Log.d("hoge", "line is touched");
				return true;
			}
		});
		
		mIvEnd.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// タッチしている位置取得
				int x = (int) event.getRawX();
				int y = (int) event.getRawY();
				
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					Log.d("hoge", "down");
					mOrigX = view.getLeft();
					mStartX = x;
					break;
				case MotionEvent.ACTION_MOVE:
					Log.d("hoge", "move");
					int left = mOrigX+(x-mStartX);
					view.layout(left, mLineY, left + view.getWidth(), mLineY+view.getHeight());
					mIvEndScreen.layout(left+view.getWidth()/2, mLineY, mTvTitle.getRight(), mLineY+view.getHeight());
					//mTvEndTime.layout(left-mTvEndTime.getWidth()/2, mTimeY, left+mTvEndTime.getWidth()/2, mTimeY+mTvEndTime.getHeight());
					mTvEndTime.setText(TrackListAdapter.msToTime(left*mDuration/mTvTitle.getWidth()));
					break;
				case MotionEvent.ACTION_UP:
					Log.d("hoge", "up");
					break;
				}
				
				Log.d("hoge", "end line is touched");
				return true;
			}
		});
		mIvPlay.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				Log.d("hoge", "play button is touched");
				return false;
			}
		});
		
		mIvPlan.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//日付情報の初期設定
				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				int monthOfYear = calendar.get(Calendar.MONTH);
				int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			 
				//日付設定ダイアログの作成
				datePickerDialog = new DatePickerDialog(EditActivity.this, DateSetListener, year, monthOfYear, dayOfMonth);
				//日付設定ダイアログの表示
				datePickerDialog.show();				
			}
		});
	}
	
	private DatePickerDialog.OnDateSetListener DateSetListener = new DatePickerDialog.OnDateSetListener(){
        public void onDateSet(android.widget.DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            Log.d("DatePicker","year:" +year + " monthOfYear:" + monthOfYear + " dayOfMonth:" + dayOfMonth);
            mDateStr = Integer.toString(year)+"/"+Integer.toString(monthOfYear)+"/"+Integer.toString(dayOfMonth);
            
            final Calendar calendar = Calendar.getInstance();
            final int hour = calendar.get(Calendar.HOUR_OF_DAY);
            final int minute = calendar.get(Calendar.MINUTE);

        	final TimePickerDialog timePickerDialog = new TimePickerDialog(EditActivity.this,
            		  new TimePickerDialog.OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker arg0, int hour, int minute) {
							mTimeStr = Integer.toString(hour)+":"+Integer.toString(minute);
							mTvOnAirTime.setText(mDateStr+" "+mTimeStr);
						}
            		  }, hour, minute, true);
        	timePickerDialog.show();
        }
	};
	
	@Override
	public void onResume(){
		super.onResume();
/*		mLineY = mIvStart.getTop();
		mTimeY = mTvStartTime.getTop();
		
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		mWindowWidth = display.getWidth();
		mLineLength=mIvStart.getHeight();
		
		mIvStart.layout(100, mLineY, 100+mIvStart.getWidth(), mLineY+mLineLength);
		mIvEnd.layout(800, mLineY, mWindowWidth-100+mIvEnd.getWidth(), mLineY+mLineLength);
		mIvStartScreen.layout(0, mLineY, mIvStart.getLeft(), mLineY+mLineLength);
		mIvEndScreen.layout(mIvEnd.getLeft(), mLineY, mWindowWidth, mLineY+mLineLength);
		*/
		mTvStartTime.setText("00:00:00");
		mTvEndTime.setText("00:00:00");
	}
	
	@Override
	public void onStart(){
		super.onStart();
				mLineY = mIvStart.getTop();
		mTimeY = mTvStartTime.getTop();
		
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		mWindowWidth = display.getWidth();
		mLineLength=mIvStart.getHeight();
		
		mIvStart.layout(100, mLineY, 100+mIvStart.getWidth(), mLineY+mLineLength);
		mIvEnd.layout(800, mLineY, mWindowWidth-100+mIvEnd.getWidth(), mLineY+mLineLength);
		mIvStartScreen.layout(0, mLineY, mIvStart.getLeft(), mLineY+mLineLength);
		mIvEndScreen.layout(mIvEnd.getLeft(), mLineY, mWindowWidth, mLineY+mLineLength);
	
	}
	class MyTimerTask extends TimerTask{
	     @Override
	     public void run() {
	         // mHandlerを通じてUI Threadへ処理をキューイング
	         mHandler.post( new Runnable() {
	             public void run() {
	                 mIvCut.setBackgroundResource(R.drawable.cut);
	                 mTimer.cancel();
	                 mTimer = null;
	             }
	         });
	     }
	 }
}

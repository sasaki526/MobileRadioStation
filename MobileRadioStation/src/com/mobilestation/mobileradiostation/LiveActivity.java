package com.mobilestation.mobileradiostation;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.mobilestation.mobileradiostation.controllers.PlayListAdapter;
import com.mobilestation.mobileradiostation.controllers.SoundFileAdapter;
import com.mobilestation.mobileradiostation.models.SongInfo;
import com.mobilestation.mobileradiostation.views.MicButton;
import com.mobilestation.mobileradiostation.views.SoundButton;

import java.util.ArrayList;

/**
 * Sound Mixer Page
 * 
 * @author masa
 *
 */
public class LiveActivity extends Activity {

	
	/* Widgets for the MIC track*/
	SeekBar mMicBar = null;
	MicButton mMicButton = null;
	
	/* Widgets for Ch1 (Stereo LR) */
	SeekBar mSoundBar_ch1 = null;
	SoundButton mSoundButton_ch1 = null;

	/* Widgets for Ch2 (Stereo LR) */
	SeekBar mSoundBar_ch2 = null;
	SoundButton mSoundButton_ch2 = null;

	/* Selection of Sound */
	ListView mSoundList = null;
	
	/* Cross fader */
	SeekBar mCrossFader = null;
	private int mCrossFaderCurrentValue;
	private int mCrossFaderMaxValue;

	ListView mLvPlayList1;
	ListView mLvPlayList2;
	ArrayList<Uri> mPlayList1;
	ArrayList<Uri> mPlayList2;
	PlayListAdapter mList1Adapter;
	PlayListAdapter mList2Adapter;

	private int mCurrentSongId1;
	private int mCurrentSongId2;
	
	Thread mRT = null;

	SoundFileAdapter mSongListAdapter = null;

    private Handler mHandler;

	private void changeVolumeMIC(int track_volume){
		float output_level;
		/* Add Fader Effects */
		float fader_effects = (float) mCrossFaderCurrentValue / mCrossFaderMaxValue;
		output_level =  ((track_volume) / 100.0f) * fader_effects;
		
		/* Output to the speaker */
		mMicButton.setLRVolume(output_level,output_level);

	}
	private void changeVolumeCh1(int track_volume){
		float output_level;
		/* Add Fader Effects */
			float fader_effects = (float) mCrossFaderCurrentValue / mCrossFaderMaxValue;
			output_level =  ((track_volume) / 100.0f) * fader_effects;
			
			
		/* Output the audio data modified by fader */
			mSoundButton_ch1.setLRVolume(output_level,output_level );
	
	}
	
	private void changeVolumeCh2(int track_volume){
		/* Add Fader Effects */
		float fader_effects = 1 - ( (float) mCrossFaderCurrentValue / mCrossFaderMaxValue ) ;
		float output_level =  (track_volume / 100.0f) * fader_effects;
		
		/* Output the audio data which is modified by the fader. */
		mSoundButton_ch2.setLRVolume(output_level,output_level);
		
	}
	
	private void changeVolumeCrossfader(int fader_level){
		mCrossFaderCurrentValue = fader_level;
		
		if ( mSoundBar_ch1 != null && mSoundBar_ch2 != null && mMicBar != null ){
			changeVolumeCh1(mSoundBar_ch1.getProgress());
			changeVolumeCh2(mSoundBar_ch2.getProgress());
			changeVolumeMIC(mMicBar.getProgress());
		}
	}
	private void createMICTrack(){
	
		/* Construct MIC Track */
		LinearLayout micLayout = (LinearLayout)findViewById(R.id.mic_track);
		mMicBar = (SeekBar)findViewById(R.id.mic_bar);
		mMicButton = (MicButton) findViewById(R.id.mic_button);
		
		mMicBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar arg0, int volume, boolean arg2) {
                changeVolumeMIC(volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

        });

        mMicBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver observer = mMicBar.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    observer.removeOnGlobalLayoutListener(this);
                } else {
                    observer.removeGlobalOnLayoutListener(this);
                }

                LayerDrawable layer = (LayerDrawable) mMicBar.getProgressDrawable();
                Drawable drawable = layer.findDrawableByLayerId(android.R.id.background);
                Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.soundline);
                int width = mMicBar.getWidth();
                int height = mMicBar.getHeight();
                background = Bitmap.createScaledBitmap(background, width, height, false);
                drawable = new BitmapDrawable(getResources(), background);

                layer.setDrawableByLayerId(android.R.id.background, drawable);
            }
        });

	
		/* Display Status such as "On Air"*/
		mMicBar.setMax((int) (mMicButton.getMaxVolume() * 100)); //Improve Resolution x100

		/* Init Display */
		changeVolumeMIC(mMicBar.getProgress());

	}


	private void createSoundTrackCh1(){
		
		/* Construct Sound Track */
		LinearLayout soundLayout = (LinearLayout)findViewById(R.id.sound_track);

		/* SeekBar for changing the volume */
		mSoundBar_ch1 = (SeekBar)findViewById(R.id.sound_bar);
		mSoundBar_ch1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar arg0, int volume, boolean arg2) {

                if (arg0.getId() == mSoundBar_ch1.getId()) {
                    changeVolumeCh1(volume);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }
        });

        mSoundBar_ch1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver observer = mSoundBar_ch1.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    observer.removeOnGlobalLayoutListener(this);
                } else {
                    observer.removeGlobalOnLayoutListener(this);
                }

                LayerDrawable layer = (LayerDrawable) mSoundBar_ch1.getProgressDrawable();
                Drawable drawable = layer.findDrawableByLayerId(android.R.id.background);
                Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.soundline);
                int width = mSoundBar_ch1.getWidth();
                int height = mSoundBar_ch1.getHeight();
                background = Bitmap.createScaledBitmap(background, width, height, false);
                drawable = new BitmapDrawable(getResources(), background);

                layer.setDrawableByLayerId(android.R.id.background, drawable);
            }
        });

		
        mSoundButton_ch1 = (SoundButton) findViewById(R.id.sound_button_1);
		mSoundButton_ch1.setListener(new SoundButton.SoundButtonListener() {
            @Override
            public void onReachEnd() {
                mCurrentSongId1++;
                if (mList1Adapter.getCount() > 0) {
                    if (mCurrentSongId1 >= mList1Adapter.getCount()) {
                        mCurrentSongId1 = 0;
                    }
                    mLvPlayList1.smoothScrollToPosition(mCurrentSongId1);
                    playSong(mSoundButton_ch1, mList1Adapter, mCurrentSongId1);
                }

            }

            @Override
            public void onTimeChanged(long elapsedTime) {
                mList1Adapter.setElapsedTime(elapsedTime);
            }
        });

		mSoundBar_ch1.setMax((int) (mSoundButton_ch1.getMaxVolume() * 100)); //Improve Resolution x100
		
		OnClickListener clicker = new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (mSoundButton_ch1.isRunning()) {
//					mSoundButton_ch1.stopSound();
                    mSoundButton_ch1.pauseSound();
				} else {
					if (mList1Adapter.getCount() > 0) {
						if (mCurrentSongId1 >= mList1Adapter.getCount()) {
							mCurrentSongId1 = 0;
						}
                        String selectedPath = Utils.getPath(LiveActivity.this, mList1Adapter.getItem(mCurrentSongId1).getUri());
                        String currentPath = mSoundButton_ch1.getPath();
                        if (currentPath != null && currentPath.equals(selectedPath)) {
                            mSoundButton_ch1.startSound();
                        } else {
                            playSong(mSoundButton_ch1, mList1Adapter, mCurrentSongId1);
                        }
					}
				}
			}
		};
		mSoundButton_ch1.setOnClickListener(clicker);

		/* Init Display */
		changeVolumeCh1(mSoundBar_ch1.getProgress());
	}

    private void playSong(final SoundButton button, final PlayListAdapter adapter, final int id) {
        SongInfo info = adapter.getItem(id);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                adapter.setPlayingSong(id);
            }
        });
        button.setSoundUri(info.getUri());
        String duration = button.getDurationString();

//        button.onPlay();
        button.startSound();
    }
	
	private void createSoundTrackCh2(){

		/* Construct Sound Track (Ch2) */
		LinearLayout soundLayout = (LinearLayout)findViewById(R.id.sound_track_ch2);
		
		/* SeekBar for changing the volume */
		mSoundBar_ch2 = (SeekBar)findViewById(R.id.sound_bar_ch2);
		mSoundBar_ch2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar arg0, int volume, boolean arg2) {
                if (arg0.getId() == mSoundBar_ch2.getId()) {
                    changeVolumeCh2(volume);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }
        });

        mSoundBar_ch2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver observer = mSoundBar_ch2.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    observer.removeOnGlobalLayoutListener(this);
                } else {
                    observer.removeGlobalOnLayoutListener(this);
                }

                LayerDrawable layer = (LayerDrawable) mSoundBar_ch2.getProgressDrawable();
                Drawable drawable = layer.findDrawableByLayerId(android.R.id.background);
                Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.soundline);
                int width = mSoundBar_ch2.getWidth();
                int height = mSoundBar_ch2.getHeight();
                background = Bitmap.createScaledBitmap(background, width, height, false);
                drawable = new BitmapDrawable(getResources(), background);

                layer.setDrawableByLayerId(android.R.id.background, drawable);
            }
        });

		
        mSoundButton_ch2 = (SoundButton) findViewById(R.id.sound_button_2);

		mSoundButton_ch2.setListener(new SoundButton.SoundButtonListener() {
			@Override
			public void onReachEnd() {
				mCurrentSongId2++;
				if (mList2Adapter.getCount() > 0) {
					if (mCurrentSongId2 >= mList2Adapter.getCount()) {
						mCurrentSongId2 = 0;
					}
                    mLvPlayList2.smoothScrollToPosition(mCurrentSongId2);
                    playSong(mSoundButton_ch2, mList2Adapter, mCurrentSongId2);


				}
			}

            @Override
            public void onTimeChanged(long elapsedTime) {
                mList2Adapter.setElapsedTime(elapsedTime);
            }
		});
		
		mSoundBar_ch2.setMax((int) (mSoundButton_ch2.getMaxVolume() * 100)); //Improve Resolution x100

		OnClickListener clicker = new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (mSoundButton_ch2.isRunning()) {
                    mSoundButton_ch2.pauseSound();
				} else {
					if (mList2Adapter.getCount() > 0) {
						if (mCurrentSongId2 >= mList2Adapter.getCount()) {
							mCurrentSongId2 = 0;
						}

                        String selectedPath = Utils.getPath(LiveActivity.this, mList2Adapter.getItem(mCurrentSongId2).getUri());
                        String currentPath = mSoundButton_ch2.getPath();
                        if (currentPath != null && currentPath.equals(selectedPath)) {
                            mSoundButton_ch2.startSound();
                        } else {
                            playSong(mSoundButton_ch2, mList2Adapter, mCurrentSongId2);
                        }

					}
				}
			}
		};
		mSoundButton_ch2.setOnClickListener(clicker);

		/* Init Display */
		changeVolumeCh2(mSoundBar_ch2.getProgress());
		
	}

	private void createPlayLists() {
		mLvPlayList1 = (ListView)findViewById(R.id.playlist_1);
		mLvPlayList2 = (ListView)findViewById(R.id.playlist_2);

		mList1Adapter = new PlayListAdapter(this, new ArrayList(), 1);
		mList2Adapter = new PlayListAdapter(this, new ArrayList(), 2);

		mLvPlayList1.setAdapter(mList1Adapter);
		mLvPlayList2.setAdapter(mList2Adapter);

		mCurrentSongId1 = 0;
		mCurrentSongId2 = 0;

	}

	private void createSoundChooser(){
		mSoundList = (ListView)findViewById(R.id.sound_list);
		mSongListAdapter = new SoundFileAdapter(LiveActivity.this, getSongInfo());
		mSoundList.setAdapter(mSongListAdapter);
	
		mSoundList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {

				SongInfo info = (SongInfo) arg0.getItemAtPosition(position);

				if (id == 1) {
					mList1Adapter.add(info);
                    mLvPlayList1.smoothScrollToPosition(mList1Adapter.getCount()-1);
				} else if (id == 2) {
					mList2Adapter.add(info);
                    mLvPlayList2.smoothScrollToPosition(mList2Adapter.getCount()-1);

				}

				String duration = "";
			}
		});
	}

	private void createCrossfader() {
		/* SeekBar for changing the volume */
		mCrossFader = (SeekBar) findViewById(R.id.cross_fader);
		mCrossFader.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int volume, boolean arg2) {
				if (arg0.getId() == mCrossFader.getId()) {
					changeVolumeCrossfader(arg0.getProgress());					
				}
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {	
			}
		});

        mCrossFader.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver observer = mCrossFader.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    observer.removeOnGlobalLayoutListener(this);
                } else {
                    observer.removeGlobalOnLayoutListener(this);
                }

                LayerDrawable layer = (LayerDrawable)mCrossFader.getProgressDrawable();
                Drawable drawable = layer.findDrawableByLayerId(android.R.id.background);
                Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.crossfader_new);
                int width = mCrossFader.getWidth();
                int height = mCrossFader.getHeight();
                background = Bitmap.createScaledBitmap(background, width, height, false);
                drawable = new BitmapDrawable(getResources(), background);

                layer.setDrawableByLayerId(android.R.id.background, drawable);
            }
        });

		/*
		 * No reason to be 100. Change it if you want.
		 */
		mCrossFaderMaxValue  = 100;
		mCrossFader.setMax(mCrossFaderMaxValue); 
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.live_main);
			
		mHandler = new Handler();

		createCrossfader();
		
	    createMICTrack();
	    createSoundTrackCh1();
		createSoundTrackCh2();
		createSoundChooser();

		createPlayLists();
    }


	@Override
	public void onWindowFocusChanged(boolean hasFocus){
		super.onWindowFocusChanged(hasFocus);
    }
	
	
	/**
	 * Model Provider 
	 * 
	 * MODEL: URIs indicate audio files in the device.
	 * @return array of the URL found in the running device. 
	 */
    private ArrayList<SongInfo> getSongInfo() {
        ArrayList<SongInfo> songInfos = new ArrayList<SongInfo>();
        ContentResolver resolver = this.getContentResolver();
        String column[] = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE+ "=?";
        String mp3Mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
        String wavMime = MimeTypeMap.getSingleton().getMimeTypeFromExtension("wav");
        String[] selectionArgs = new String[]{wavMime, mp3Mime};
        Cursor cursor = resolver.query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
                        column,
                        selection,
                        selectionArgs,
                        null);
        while( cursor.moveToNext() ){
            Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

            songInfos.add(new SongInfo(uri, title, duration, 0));
        }
        cursor.close();
        return songInfos;
	}
	

	@Override
	protected void onPause() {
		
		mMicButton.stopMic();
		mSoundButton_ch1.stopSound();
		mSoundButton_ch2.stopSound();
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}
}

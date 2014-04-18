package com.mobilestation.mobileradiostation;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PlayListActivity  extends Activity{
	private ArrayList<Track> mTracks;
	private ListView mLvTracks;
	private TrackListAdapter mTrackListAdapter;
	private Boolean mFlgPlaying;
	
	private String mPlayingAudioPath;
	private int mResumePoint;
	
	private MediaPlayer mMediaPlayer;
	
	private String mMode;
	
	public static final String TAG = "AudioPlayTest";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist_main);
		
		Intent intent = getIntent();
		mMode = intent.getStringExtra(Utils.LIST_MODE);
		
		mMediaPlayer = new MediaPlayer();
		mFlgPlaying = false;
		mPlayingAudioPath = new String();
		mLvTracks = (ListView)findViewById(R.id.list_tracks);
		
		mLvTracks.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
				Log.d(TAG, "onItemClick");
				ListView listView = (ListView) parent;
				Log.d(TAG, "onItemClick2");
                Track track = (Track) listView.getItemAtPosition(position);
                Log.d(TAG, "onItemClick3");
                if (mMode.equals(Utils.MODE_PLAY)){
                	Intent i = new Intent(getApplicationContext(), PlayingActivity.class);
                    i.putExtra(Utils.FILE_PATH, track.getPath());
                    i.putExtra(Utils.TITLE, track.getTitle());
                    i.putExtra(Utils.DURATION, track.getDuration());
                    startActivity(i);
                } else if (mMode.equals(Utils.MODE_EDIT)){
                    //Intent i = new
                	Log.d(TAG, "edit");
                	Intent i = new Intent(getApplicationContext(), EditActivity.class);
                    i.putExtra(Utils.FILE_PATH, track.getPath());
                    i.putExtra(Utils.TITLE, track.getTitle());
                    i.putExtra(Utils.DURATION, track.getDuration());
                    startActivity(i);
                }
                
                /*if(mMediaPlayer.isPlaying()){
                	if(track.getPath().equals(mPlayingAudioPath)){
                		mMediaPlayer.pause();
                		mResumePoint = mMediaPlayer.getCurrentPosition();
                		Log.d(TAG, "pause");
                	} else {
                		mPlayingAudioPath = track.getPath();
                		playAudio(mPlayingAudioPath);
                	}
                	mFlgPlaying=false;
                } else {
                	if(track.getPath().equals(mPlayingAudioPath)){
                		resumeAudio(mResumePoint);
                	} else {
                		mPlayingAudioPath = track.getPath();
                		playAudio(mPlayingAudioPath);
                	}
                }*/
			}
		});
	}
	
	private void playAudio(String path){
    	try {
        	mMediaPlayer.reset();
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mFlgPlaying=true;
			Log.d(TAG, "play");
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
	protected void onResume(){
		super.onResume();
		mTracks = getAudioResources();
		mTrackListAdapter = new TrackListAdapter(this, R.layout.track_item, mTracks);
		mLvTracks.setAdapter(mTrackListAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private ArrayList<Track> getAudioResources(){
        ArrayList<Track> tracks = new ArrayList<Track>();
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
                        Track.COLUMNS, 
                        null,
                        null,
                        null);
        while( cursor.moveToNext() ){
                tracks.add(new Track(cursor));
        }
        cursor.close();
        return tracks;		
	}
}


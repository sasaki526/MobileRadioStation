package com.mobilestation.mobileradiostation.controllers;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobilestation.mobileradiostation.R;
import com.mobilestation.mobileradiostation.models.Track;

public class TrackListAdapter extends ArrayAdapter<Track>{
    private Context mContext;
    private int mResourceId;
    private ArrayList<Track> mTrackList;
    private LayoutInflater mInflater;
        
	
	public TrackListAdapter(Context context, int resourceId, ArrayList<Track> items){
		super(context, 0, items);
		mContext = context;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResourceId = resourceId;
		//mTrackList = new ArrayList<Track>();
		mTrackList = items;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent){
		Track track = mTrackList.get(position);
		ViewHolder holder;
		
		if(convertView != null){
			holder = (ViewHolder)convertView.getTag();
		} else {
			convertView = mInflater.inflate(mResourceId, null);
			holder = new ViewHolder();
			holder.tvTitle = (TextView)convertView.findViewById(R.id.txt_audio_title);
			holder.tvArtist = (TextView)convertView.findViewById(R.id.txt_audio_artist);
			holder.tvDuration = (TextView)convertView.findViewById(R.id.txt_audio_duration);
			convertView.setTag(holder);
		}
		holder.tvTitle.setText(track.getTitle());
		holder.tvArtist.setText(track.getArtist());
		holder.tvDuration.setText(msToTime(track.getDuration()));
		
		return convertView;
	}
	
	private static class ViewHolder{
		TextView tvTitle;
		TextView tvArtist;
		TextView tvDuration;
	}
	
	public static String msToTime(long duration){
		int rest = 0;
		
		int totalSec = (int)duration/1000;
		int hour = totalSec/3600;
		rest = totalSec%3600;
		int minute = rest/60;
		int sec = rest%60;
		
		String time = makeTwoDigitString(hour)+":"+makeTwoDigitString(minute)+":"+makeTwoDigitString(sec);
		
		return time;
	}
	
	private static String makeTwoDigitString(int number){
		String numStr=new String();
		if(number<10){
			numStr = "0"+Integer.toString(number);
		} else {
			numStr = Integer.toString(number);
		}
		
		return numStr;
	}
}

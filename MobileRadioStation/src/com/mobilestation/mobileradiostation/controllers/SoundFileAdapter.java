package com.mobilestation.mobileradiostation.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilestation.mobileradiostation.R;
import com.mobilestation.mobileradiostation.Utils;
import com.mobilestation.mobileradiostation.models.SongInfo;

import java.util.ArrayList;
import java.util.List;

public class SoundFileAdapter extends BaseAdapter {

	Context mContext;
	List<SongInfo> mSongList;

	@Override
	public int getCount() {
		return mSongList.size();
	}

	@Override
	public SongInfo getItem(int position) {
		return mSongList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public SoundFileAdapter(Context context, ArrayList<SongInfo> arrayList) {
		mContext = context;
		mSongList = arrayList;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;

		if ( convertView == null ){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.songs, null);
			holder = new ViewHolder();
			holder.tvTitle = (TextView) convertView.findViewById(R.id.song_name);
			holder.tvDuration = (TextView) convertView.findViewById(R.id.song_duration);
			holder.tvAdd1 = (TextView) convertView.findViewById(R.id.txt_add_track1);
			holder.tvAdd2 = (TextView) convertView.findViewById(R.id.txt_add_track2);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}

		SongInfo info = getItem(position);
		
		holder.tvTitle.setText(info.getTitle());
		holder.tvTitle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((ListView) parent).performItemClick(v, position, 0);
			}
		});
		holder.tvDuration.setText(Utils.formatTime(info.getDuration()));
		holder.tvDuration.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((ListView) parent).performItemClick(v, position, 0);
			}
		});

		holder.tvAdd1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((ListView) parent).performItemClick(v, position, 1);
			}
		});

		holder.tvAdd2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((ListView) parent).performItemClick(v, position, 2);
			}
		});

		return convertView;
	}

	public void remove(int position) {
		mSongList.remove(position);
		notifyDataSetChanged();
	}

	public void add(SongInfo info) {
		mSongList.add(info);
		notifyDataSetChanged();
	}

	private class ViewHolder {
		TextView tvTitle;
		TextView tvDuration;
		TextView tvAdd1;
		TextView tvAdd2;
	}
	
	

}

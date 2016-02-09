package com.mobilestation.mobileradiostation.controllers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilestation.mobileradiostation.R;
import com.mobilestation.mobileradiostation.Utils;
import com.mobilestation.mobileradiostation.models.SongInfo;

import java.util.ArrayList;

/**
 * Created by miyamo on 2016/01/31.
 */
public class PlayListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<SongInfo> mPlayList;
    private int mPlayingPosition;
    private int mTrackColor;


    public PlayListAdapter (Context context, ArrayList<SongInfo> items, int trackNo) {
        mContext = context;
        mPlayList = items;
        mPlayingPosition = 0;
        switch (trackNo) {
            case 2:
                mTrackColor = mContext.getResources().getColor(R.color.track2);
                break;
            case 1:
            default:
                mTrackColor = mContext.getResources().getColor(R.color.track1);
                break;
        }
    }

    @Override
    public int getCount() {
        return mPlayList.size();
    }

    @Override
    public SongInfo getItem(int position) {
        return mPlayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final int index = position;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.playlist_item, null);
            holder = new ViewHolder();
            holder.layoutItem = (LinearLayout) convertView.findViewById(R.id.layout_playlist_item);
            holder.imgIcon = (ImageView) convertView.findViewById(R.id.img_now_playing);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.txt_song_title);
            holder.tvElapsedTime = (TextView) convertView.findViewById(R.id.txt_elapsed_time);
            holder.tvDuration = (TextView) convertView.findViewById(R.id.txt_duration);
            holder.tvDelete = (TextView) convertView.findViewById(R.id.txt_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        SongInfo info = getItem(position);
        holder.tvTitle.setText(info.getTitle());
        holder.tvDuration.setText(Utils.formatTime(info.getDuration()));
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(index);
            }
        });

        if (mPlayingPosition == position) {
            holder.layoutItem.setBackgroundColor(mTrackColor);
            holder.imgIcon.setImageResource(R.drawable.arrow);
            holder.imgIcon.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            holder.tvElapsedTime.setVisibility(View.VISIBLE);
            holder.tvElapsedTime.setText(Utils.formatTime(info.getElapsedTime())+"/");
        } else {
            holder.layoutItem.setBackgroundColor(Color.TRANSPARENT);
            holder.imgIcon.setImageBitmap(null);
            holder.tvElapsedTime.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void remove(int position) {
        mPlayList.remove(position);
        notifyDataSetChanged();
    }

    public void add(SongInfo info) {
        mPlayList.add(info);
        notifyDataSetChanged();
    }

    public void setPlayingSong(int position) {
        mPlayingPosition = position;
        notifyDataSetChanged();
    }

    public void setElapsedTime(long elapsedTime) {
        SongInfo info = getItem(mPlayingPosition);
        info.setElapsedTime(elapsedTime);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        LinearLayout layoutItem;
        ImageView imgIcon;
        TextView tvTitle;
        TextView tvElapsedTime;
        TextView tvDuration;
        TextView tvDelete;
    }

}


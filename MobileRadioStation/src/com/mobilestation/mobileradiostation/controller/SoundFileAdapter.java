package com.mobilestation.mobileradiostation.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mobilestation.mobileradiostation.R;
import com.mobilestation.mobileradiostation.Utils;
import com.mobilestation.mobileradiostation.models.Track;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SoundFileAdapter extends ArrayAdapter<Uri> {

	Context mContext = null;
	int mResource;
	List<Uri> mSongs = null;
	
	public SoundFileAdapter(Context context, int resource,
			ArrayList<Uri> arrayList) {
		super(context, resource, arrayList);
		
		mContext = context;
		mResource = resource;
		mSongs = arrayList;
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if ( convertView == null ){
			view = LayoutInflater.from(mContext).inflate(mResource,null);
		}else{
			view = convertView;
		}
		TextView text = (TextView) view.findViewById(R.id.song_name);
		Uri uri = getItem(position);
		
		 
		text.setText(Utils.uriToDisplayName(mContext, uri));
		
		
		return view;
	}
	
	

}

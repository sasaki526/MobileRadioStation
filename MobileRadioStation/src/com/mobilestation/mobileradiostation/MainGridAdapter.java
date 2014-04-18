package com.mobilestation.mobileradiostation;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainGridAdapter extends ArrayAdapter<MainItem>{
    private int mResourceId;
    private Context mContext;
    private ArrayList<MainItem> mMainItemList;
    private LayoutInflater mInflater;

    public MainGridAdapter(Context context, int resource, MainItem[] items) {
        super(context, resource, items);
        mResourceId = resource;
        mContext = context;
        mMainItemList = new ArrayList<MainItem>();
        for(int i=0; i<items.length; i++){
        	mMainItemList.add(items[i]);
        }
//        	mMainItemList = (ArrayList<MainItem>) Arrays.asList(items);
        	mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	MainItem item = mMainItemList.get(position);
    	ViewHolder holder;
 
        if (convertView != null) {
        	holder = (ViewHolder)convertView.getTag();
        } else {
            convertView = mInflater.inflate(mResourceId, null);
            holder = new ViewHolder();
            holder.ivIcon = (ImageView)convertView.findViewById(R.id.img_main_icon);
            holder.tvName = (TextView)convertView.findViewById(R.id.txt_item_name);
            convertView.setTag(holder);
        }
        holder.ivIcon.setBackgroundResource(item.getResourceId());
        holder.tvName.setText(item.getName());
 
        return convertView;
    }
    
    private static class ViewHolder{
    	ImageView ivIcon;
    	TextView tvName;
    }
}

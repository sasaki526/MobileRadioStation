package com.mobilestation.mobileradiostation;

public class MainItem {
	private String mName;
	private int mResourceId;
	private Class<?> mClassName;
	MainItem(String name, int resourceId, Class<?> className){
		mName = name;
		mResourceId = resourceId;
		mClassName = className;
	}
	
	public String getName(){
		return mName;
	}
	public int getResourceId(){
		return mResourceId;
	}
	public Class<?> getClassName(){
		return mClassName;
	}
}

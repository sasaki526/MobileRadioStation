package com.mobilestation.mobileradiostation;

/**
 * ?
 * @author 
 */
public class MainItem {
	private String mName;
	private int mResourceId;
	private Class<?> mClassName;
	
	/**
	 * 
	 */
	MainItem(String name, int resourceId, Class<?> className){
		mName = name;
		mResourceId = resourceId;
		mClassName = className;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName(){
		return mName;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getResourceId(){
		return mResourceId;
	}
	
	/**
	 * 
	 * @return
	 */
	public Class<?> getClassName(){
		return mClassName;
	}
}

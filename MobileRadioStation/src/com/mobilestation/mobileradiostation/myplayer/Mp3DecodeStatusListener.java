package com.mobilestation.mobileradiostation.myplayer;

import java.util.EventListener;

public interface Mp3DecodeStatusListener extends EventListener {
	
	public void onDecoded(byte[] buffer);
	
	public void onReachEnd();

}

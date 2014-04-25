package com.mobilestation.mobileradiostation;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class Utils {
	public static final String LIST_MODE = "com.mobilestation.mobileradiostation.LIST_MODE";
	public static final String MODE_PLAY = "com.mobilestation.mobileradiostation.MODE_PLAY";
	public static final String MODE_EDIT = "com.mobilestation.mobileradiostation.MODE_EDIT";
	
	public static final String FILE_PATH = "com.mobilestation.mobileradiostation.FILE_PATH";
	public static final String TITLE = "com.mobilestation.mobileradiostation.TITLE";
	public static final String DURATION = "com.mobilestation.mobileradiostation.DURATION";
	
	public static final String RECORD_MODE="com.mobilestation.mobileradiostation.RECORD_MODE";
	public static final String MODE_RECORD="com.mobilestation.mobileradiostation.MODE_RECORD";
	public static final String MODE_LIVE="com.mobilestation.mobileradiostation.MODE_LIVE";
	
	public static String uriToDisplayName(Context context, Uri uri){
	
		String[] column = { MediaStore.Audio.Media.DISPLAY_NAME };
		ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                        uri, 
                        column, 
                        null,
                        null,
                        null);
       
        cursor.moveToFirst();
	    String displayName =  cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.DISPLAY_NAME)); 
	    if ( displayName == null ){
	    	return uri.toString();
	    }else{
	    	    Log.i("test",displayName);
	    	    return displayName; 
	    	}

	}
}

package com.mobilestation.mobileradiostation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

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
	
	public static int uriToDuration(Context context, Uri uri){
		
		String[] column = { MediaStore.Audio.Media.DURATION };
		ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                        uri, 
                        column, 
                        null,
                        null,
                        null);
       
        cursor.moveToFirst();
	    return  cursor.getInt( cursor.getColumnIndex( MediaStore.Audio.Media.DISPLAY_NAME)); 
	    

	}
	
	private static final String RIFF_HEADER = "RIFF";
	  private static final String WAVE_HEADER = "WAVE";
	  private static final String FMT_HEADER = "fmt ";
	  private static final String DATA_HEADER = "data";
	   
	  private static final int HEADER_SIZE = 44;
	   
	  private static final String CHARSET = "ASCII";
	 
	 
	  public static boolean isPCM(Context context, Uri uri)  {

		  InputStream wav = null;
		  try {

				wav= context.getContentResolver().openInputStream(uri);

			    ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
			    buffer.order(ByteOrder.LITTLE_ENDIAN);
			 
			    wav.read(buffer.array(), buffer.arrayOffset(), buffer.capacity());
				
			    buffer.rewind();
			    buffer.position(20);
			    int format = buffer.getShort();
			    Log.i("format",String.valueOf(format));
			 	return format == 1;
		  	
				
					
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				try {
					wav.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				try {
					wav.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return false;
			}
		  }
	  
	  public static boolean is44100Hz(Context context, Uri uri)  {
		  
		  InputStream wav = null;
		  try {

			  wav= context.getContentResolver().openInputStream(uri);
			  ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
			  buffer.order(ByteOrder.LITTLE_ENDIAN);
			 
				wav.read(buffer.array(), buffer.arrayOffset(), buffer.capacity());
			 
			    buffer.rewind();
			    buffer.position(24);
			    int rate = buffer.getInt();
			    Log.i("rate",String.valueOf(rate));
				return 44100 == rate;
		  	
				
					
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				try {
					wav.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				try {
					wav.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return false;
			}
	 
	  }
}

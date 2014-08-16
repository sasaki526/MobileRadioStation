package com.mobilestation.mobileradiostation;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
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
	
	/* to make sure what kind of WAV file.*/
	private static final int HEADER_SIZE = 44;
	 
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
	    cursor.close();
	    if ( displayName == null ){
	    	return uri.toString();
	    }else{
	    	    Log.i("test",displayName);
	    	    return displayName; 
	    }

	}
	
	public static String foramtTime(int milisec){
	
		int s = (milisec / 1000) % 60;
		int m = (milisec / (1000 * 60)) % 60;
		int h = (milisec / (1000 * 60 * 60)) % 24;	
		return String.format("%02d:%02d:%02d", h,m,s);
		}
	
	public static int uriToDurationInmsec(Context context, Uri uri){
		int duration = 0;
		MediaPlayer temp = new MediaPlayer();
		try {
			temp.setDataSource(context, uri);
			temp.prepare();
			duration = temp.getDuration();
			temp.release();
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return duration;

	}
	
	 	 
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
	  
	  public static boolean isMp3(Context context, Uri uri)  {

		  	String mime = context.getContentResolver().getType(uri);
			if  ( "audio/mpeg".equals(mime) ) {
				return true;
			}
		  
		  return false;
	  }
	  
	  
	  
	  
	  public static byte[] decode (String path, int startMs, int maxMs)
	  throws IOException {
		  ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024); 
		  float totalMs = 0;
		  boolean seeking = true;
		  File file = new File(path);
		  InputStream input = new BufferedInputStream(new FileInputStream(file), 8* 1024);
		  
		  try{
			  Bitstream bitstream = new Bitstream(input);
			  Decoder decorder = new Decoder();
			  boolean done = false;
			  while( !done ){
				  Header frameHeader = bitstream.readFrame();
				  if ( frameHeader == null ){
					  done = true;
				  } else {
					  totalMs += frameHeader.ms_per_frame();
					  
					  if ( totalMs >= startMs ){
						  seeking = false;
					  }
					  
					  if (! seeking ) {
						  SampleBuffer output = (SampleBuffer) decorder.decodeFrame(frameHeader,bitstream);
						  if ( output.getSampleFrequency() != 44100 ||
								  output.getChannelCount() != 2 ){
							  throw new RuntimeException("decode error");
							  
						  }
						  
						  short[] pcm = output.getBuffer();
						  for ( short s : pcm ){
							  outStream.write( s & 0xff );
							  outStream.write(( s >> 8 ) & 0xff );
						  }
					  }
					  
					  if ( totalMs >= ( startMs + maxMs) ){
						  done = true;
					  }
				  }
				  bitstream.closeFrame();
				  
			  }
			  return outStream.toByteArray();
			
	  } catch ( RuntimeException e){
		  	throw new IOException("Bitstream error : " + e);
	  } catch ( DecoderException e ){
		  throw new IOException("Decoder error : " + e);
	  } catch (BitstreamException e) {
		e.printStackTrace();
	}finally {
		  if ( input != null ) {
			  input.close();
		  }
	 }
		return null;
	  }
}

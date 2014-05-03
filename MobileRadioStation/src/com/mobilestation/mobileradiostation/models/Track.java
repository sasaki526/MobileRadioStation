package com.mobilestation.mobileradiostation.models;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class Track {
	private long id;
	private long albumId;
	private long artistId;
	private String path;
	private String title;
	private String album;
	private String artist;
	private Uri uri;
	private long duration;
	private int trackNo;
	
	public static final String[] COLUMNS = {
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ARTIST_ID,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.TRACK,
	};
	
	public Track (Cursor cursor){
        id              = cursor.getLong( cursor.getColumnIndex( MediaStore.Audio.Media._ID ));
        path            = cursor.getString( cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        title           = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.TITLE ));
        album           = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM ));
        artist          = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ARTIST ));
        albumId         = cursor.getLong( cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM_ID ));
        artistId        = cursor.getLong( cursor.getColumnIndex( MediaStore.Audio.Media.ARTIST_ID ));
        duration        = cursor.getLong( cursor.getColumnIndex( MediaStore.Audio.Media.DURATION ));
        trackNo         = cursor.getInt( cursor.getColumnIndex( MediaStore.Audio.Media.TRACK ));
        uri             = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

	}
	
	public String getPath(){
		return path;
	}
	public String getTitle(){
		return title;
	}
	public String getArtist(){
		return artist;		
	}
	public long getDuration(){
		return duration;
	}
}

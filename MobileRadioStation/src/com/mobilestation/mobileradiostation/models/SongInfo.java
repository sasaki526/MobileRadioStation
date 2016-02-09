package com.mobilestation.mobileradiostation.models;

import android.net.Uri;

/**
 * Created by miyamo on 2016/02/06.
 */
public class SongInfo {
    private Uri mUri;
    private String mTitle;
    private long mDuration;
    private long mElapsedTime;

    public SongInfo(Uri uri, String title, long duration, long elapsedTime) {
        mUri = uri;
        mTitle = title;
        mDuration = duration;
        mElapsedTime = elapsedTime;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }
    public void setTitle(String title) {
        mTitle = title;
    }
    public void setDuration(long duration) {
        mDuration = duration;
    }
    public void setElapsedTime(long elapsedTime) {
        mElapsedTime = elapsedTime;
    }

    public Uri getUri() {
        return mUri;
    }
    public String getTitle() {
        return mTitle;
    }
    public long getDuration() {
        return mDuration;
    }
    public long getElapsedTime() {
        return mElapsedTime;
    }

}

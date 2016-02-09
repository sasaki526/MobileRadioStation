package com.mobilestation.mobileradiostation.myplayer;

import android.media.MediaMetadataRetriever;

/**
 * Created by miyamo on 2014/12/28.
 */
public abstract class BasePlayer {
    protected long mDuration = 0;
    protected long mElapsedTime = 0;

    protected boolean mLoop = false;

    protected float mLeftVolume;
    protected float mRightVolume;

    protected String mPath;

    public interface PlayStatusListener {
        public void onReachEnd();
    }

    abstract public String getPath();

    abstract public boolean isPlaying();

    abstract public void play();

    abstract public void stop();

    abstract public void pause();

    abstract public long getElapsedTime();

    abstract public void setLoop(boolean loop);

    abstract public void setLRVolume(float l, float r);

    abstract public long getDuration();

    abstract public void setListener(PlayStatusListener listener);

    protected long getDuration(String path) {
        long duration = 0;
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(path);

        duration = Long.parseLong(
                metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

        return duration;
    }
}

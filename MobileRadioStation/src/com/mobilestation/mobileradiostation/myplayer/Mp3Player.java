package com.mobilestation.mobileradiostation.myplayer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaMetadataRetriever;

import java.io.IOException;

public class Mp3Player extends BasePlayer implements Mp3DecodeStatusListener {
    private Mp3Decoder mDecoder;
    private AudioTrack mAudioTrack;

    public static final int PCM_FREQ = 44100; // Hz

    private final String TAG = this.getClass().getSimpleName();

    public Mp3Player(String path, int bufSize){
        mDecoder = new Mp3Decoder(path, bufSize);

        mDuration = getDuration(path);

        mAudioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                PCM_FREQ,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufSize*2,
                AudioTrack.MODE_STREAM);
        mAudioTrack.setPlaybackRate(PCM_FREQ);
    }

    public boolean isPlaying(){
        return mDecoder.isDecoding();
    }

    public void play(){
        try {
            mAudioTrack.play();
            mDecoder.setListener(this);
            mDecoder.startDecode();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        mAudioTrack.stop();
        mDecoder.stopDecode();
        mDecoder.removeListener();
    }

    public void pause(){
        mAudioTrack.pause();
        mDecoder.pauseDecode();
        mDecoder.removeListener();
    }

    public long getElapsedTime(){
        return (mDuration - mDecoder.getElapsedTime());
    }

    public void setLoop(boolean loop){
        mLoop = loop;
    }

    @Override
    public void setLRVolume(float l, float r) {
        mLeftVolume = l;
        mRightVolume = r;
        mAudioTrack.setStereoVolume(mLeftVolume, mRightVolume);
    }

    @Override
    public long getDuration() {
        return mDuration;
    }

    @Override
    public void onDecoded(byte[] buffer) {
        mAudioTrack.write(buffer, 0, buffer.length);
    }

    @Override
    public void onReachEnd() {
        stop();
        if(mLoop){
            play();
        }
    }

}

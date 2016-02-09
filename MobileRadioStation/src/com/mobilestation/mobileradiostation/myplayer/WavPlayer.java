package com.mobilestation.mobileradiostation.myplayer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by miyamo on 2014/12/28.
 */
public class WavPlayer extends BasePlayer{
    private AudioTrack mAudioTrack;
    private String mPath;
    private FileInputStream mInputStream;
    private DataInputStream mSoundStream;

    private PlayStatusListener mListener;

    private long mWrittenDataSize = 0;
    private boolean mRunning = false;

    public WavPlayer(String path) {
        mPath = path;

        mDuration = getDuration(mPath);

        int bufSize = AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                44100,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufSize,
                AudioTrack.MODE_STREAM);

        initDataStream();



        mAudioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack track) {
            }

            @Override
            public void onPeriodicNotification(AudioTrack track) {
            }
        });
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public boolean isPlaying() {
        return mRunning;
    }

    @Override
    public void play() {
        mRunning = true;

        (new Thread(new Runnable() {
            @Override
            public void run() {
                do{
                int i = 0;
                int workbuffer = 512;
                byte[] pieceOfMusic = new byte[workbuffer];
//                long dataWritten = 0;
                try {
//                    DataInputStream soundStream = null;
//                    InputStream in = new FileInputStream(mPath);
//                    soundStream = new DataInputStream(in);
                    mAudioTrack.play();
                    while(mRunning && (i = mSoundStream.read(pieceOfMusic, 0, workbuffer)) > -1){
                        mWrittenDataSize += mAudioTrack.write(pieceOfMusic, 0, i);
                        mAudioTrack.setStereoVolume(mLeftVolume, mRightVolume);
                        mElapsedTime = (long)(mWrittenDataSize * 1000 / (44100.0 * 2 * 2));
                    }

                    mAudioTrack.stop();
                    mWrittenDataSize = 0;
                    mSoundStream.close();
                    mInputStream.close();
                    if (mListener != null) {
                        mListener.onReachEnd();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }while(mRunning && mLoop);

        }
        })).start();
    }

    @Override
    public void stop() {
        mRunning = false;
        mAudioTrack.stop();
        initDataStream();
    }

    @Override
    public void pause() {
        mRunning = false;
        mAudioTrack.pause();
    }

    @Override
    public void setListener(PlayStatusListener listener) {
        mListener = listener;
    }

    private void initDataStream() {
        try {
            mInputStream = new FileInputStream(mPath);
            mSoundStream = new DataInputStream(mInputStream);
            mWrittenDataSize = 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
//    public long getElapsedTime() {
//        return 0;
//    }
    public long getElapsedTime() {
        return mElapsedTime;
    }

    @Override
    public void setLoop(boolean loop) {
        mLoop = true;
    }

    @Override
    public void setLRVolume(float l, float r) {
        mLeftVolume = l;
        mRightVolume = r;
    }

    @Override
    public long getDuration() {
        return mDuration;
    }
}

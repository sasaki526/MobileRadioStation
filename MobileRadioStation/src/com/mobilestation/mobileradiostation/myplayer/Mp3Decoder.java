package com.mobilestation.mobileradiostation.myplayer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import android.util.Log;

public class Mp3Decoder {
    private int mBufSize;
    private File mFile;
    private InputStream mInputStream;
    private ByteArrayOutputStream mOutStream;

    private Mp3DecodeStatusListener mListener;

    private boolean mRunning = false;

    private float mLastFrameDuration = 0;
    private float mElapsedTime = 0;

    private final String TAG = this.getClass().getSimpleName();

    Mp3Decoder(String path, int bufSize) {
        mBufSize = bufSize;
        mFile = new File(path);

        try {
            mInputStream = new BufferedInputStream(	new FileInputStream(mFile), 8*mBufSize);
            mOutStream = new ByteArrayOutputStream(mBufSize);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isDecoding(){
        return mRunning;
    }

    public void startDecode() throws IOException {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                Decoder decoder = new Decoder();
                mOutStream.reset();
                Bitstream bitstream = new Bitstream(mInputStream);

                mRunning = true;
                while (mRunning) {
                    try {
                        if (mOutStream.size() > mBufSize) {
                            if (mListener != null) {
                                mListener.onDecoded(mOutStream.toByteArray());
                                mOutStream.reset();
                            }
                        }

                        Header frameHeader;
                        frameHeader = bitstream.readFrame();
                        if (frameHeader == null) { // Reach the end of the music
                            mListener.onDecoded(mOutStream.toByteArray());
                            mRunning = false;
                            initInputStream();
                            mListener.onReachEnd();
                        } else {
                            // >>> Debug
                            //Log.d(TAG, "time: "+frameHeader.ms_per_frame());
                            // <<< Debug
                            SampleBuffer output = (SampleBuffer) decoder
                                    .decodeFrame(frameHeader, bitstream);

                            mElapsedTime += frameHeader.ms_per_frame();

                            if (output.getSampleFrequency() != Mp3Player.PCM_FREQ
                                    || output.getChannelCount() != 2) {
                            }

                            short[] pcm = output.getBuffer();
                            for (short s : pcm) {
                                mOutStream.write(s & 0xff);
                                mOutStream.write((s >> 8) & 0xff);
                            }
                        }
                        bitstream.closeFrame();

                    } catch (BitstreamException e) {
                        Log.w(TAG, "Bitsream error", e);
                    } catch (DecoderException e) {
                        Log.w(TAG, "Decoder error", e);
                    } finally {
                    }
                }
            }
        })).start();
    }

    protected void stopDecode(){
        mRunning = false;
        initInputStream();
    }

    protected void pauseDecode(){
        mRunning = false;
    }

    protected void setListener(Mp3DecodeStatusListener listener){
        mListener = listener;
    }

    protected void removeListener(){
        mListener = null;
    }

    protected long getElapsedTime(){
        return (long)mElapsedTime;
    }

    private void initInputStream(){
        try {
            mInputStream = new BufferedInputStream(	new FileInputStream(mFile), 8*mBufSize);
            mElapsedTime = 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}

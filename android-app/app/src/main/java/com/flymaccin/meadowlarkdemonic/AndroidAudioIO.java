package com.flymaccin.meadowlarkdemonic;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

/** Android device I/O boundary. Project state and time remain owned by DemonicProject/Transport. */
public final class AndroidAudioIO {
    private final DemonicProject project;
    private AudioTrack output;
    private AudioRecord input;
    private boolean outputRunning,inputRunning;

    public AndroidAudioIO(DemonicProject project) { this.project = project; }

    public void openOutput() {
        int rate = project.transport.sampleRate();
        int min = AudioTrack.getMinBufferSize(rate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        output = new AudioTrack.Builder()
            .setAudioFormat(new AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(rate).setChannelMask(AudioFormat.CHANNEL_OUT_STEREO).build())
            .setBufferSizeInBytes(Math.max(min, rate / 5 * 4))
            .setTransferMode(AudioTrack.MODE_STREAM).build();
    }

    public synchronized boolean outputOpen(){return output!=null;}
    public synchronized boolean outputRunning(){return outputRunning;}
    public synchronized void startOutput() { if (outputRunning) return; if (output == null) openOutput(); if(output.getState()!=AudioTrack.STATE_INITIALIZED)throw new IllegalStateException("Output initialization failed"); output.play(); outputRunning=true; }
    public synchronized int write(short[] pcm, int offset, int length) { if (!outputRunning||output == null) throw new IllegalStateException("Output not running"); int n=output.write(pcm, offset, length); if(n<0)throw new IllegalStateException("AudioTrack write failed: "+n); return n; }
    public synchronized void stopOutput() { if (output != null) { try{if(outputRunning)output.stop();}finally{output.release();output=null;outputRunning=false;} } else outputRunning=false; }

    public void openInput() {
        int rate = project.transport.sampleRate();
        int min = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        input = new AudioRecord.Builder()
            .setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            .setAudioFormat(new AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(rate).setChannelMask(AudioFormat.CHANNEL_IN_MONO).build())
            .setBufferSizeInBytes(Math.max(min, rate / 5 * 2)).build();
    }

    public synchronized boolean inputOpen(){return input!=null;}
    public synchronized boolean inputRunning(){return inputRunning;}
    public synchronized void startInput() { if(inputRunning)return; if (input == null) openInput(); if(input.getState()!=AudioRecord.STATE_INITIALIZED)throw new IllegalStateException("Input initialization failed"); input.startRecording(); inputRunning=true; }
    public synchronized int read(short[] pcm, int offset, int length) { if (!inputRunning||input == null) throw new IllegalStateException("Input not running"); int n=input.read(pcm, offset, length); if(n<0)throw new IllegalStateException("AudioRecord read failed: "+n); return n; }
    public synchronized void stopInput() { if (input != null) { try{if(inputRunning)input.stop();}finally{input.release();input=null;inputRunning=false;} } else inputRunning=false; }
    public synchronized void release(){stopInput();stopOutput();}

}
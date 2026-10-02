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

    public AndroidAudioIO(DemonicProject project) { this.project = project; }

    public void openOutput() {
        int rate = project.transport.sampleRate();
        int min = AudioTrack.getMinBufferSize(rate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        output = new AudioTrack.Builder()
            .setAudioFormat(new AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(rate).setChannelMask(AudioFormat.CHANNEL_OUT_STEREO).build())
            .setBufferSizeInBytes(Math.max(min, rate / 5 * 4))
            .setTransferMode(AudioTrack.MODE_STREAM).build();
    }

    public void startOutput() { if (output == null) openOutput(); output.play(); }
    public int write(short[] pcm, int offset, int length) { if (output == null) throw new IllegalStateException("Output not open"); return output.write(pcm, offset, length); }
    public void stopOutput() { if (output != null) { output.stop(); output.release(); output = null; } }

    public void openInput() {
        int rate = project.transport.sampleRate();
        int min = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        input = new AudioRecord.Builder()
            .setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            .setAudioFormat(new AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(rate).setChannelMask(AudioFormat.CHANNEL_IN_MONO).build())
            .setBufferSizeInBytes(Math.max(min, rate / 5 * 2)).build();
    }

    public void startInput() { if (input == null) openInput(); input.startRecording(); }
    public int read(short[] pcm, int offset, int length) { if (input == null) throw new IllegalStateException("Input not open"); return input.read(pcm, offset, length); }
    public void stopInput() { if (input != null) { input.stop(); input.release(); input = null; } }
}
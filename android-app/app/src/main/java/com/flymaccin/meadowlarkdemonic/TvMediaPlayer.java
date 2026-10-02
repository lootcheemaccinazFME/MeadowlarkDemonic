package com.flymaccin.meadowlarkdemonic;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.SurfaceHolder;
import java.io.IOException;

/** Android media rendering boundary for the canonical DemonicTv session. */
public final class TvMediaPlayer implements MediaPlayer.OnCompletionListener {
    private final Context context;
    private final DemonicProject project;
    private MediaPlayer player;
    private SurfaceHolder surface;

    public TvMediaPlayer(Context context, DemonicProject project) {
        if (context == null || project == null) throw new IllegalArgumentException();
        this.context=context.getApplicationContext();
        this.project=project;
    }

    public void attachSurface(SurfaceHolder holder) {
        surface=holder;
        if(player!=null) player.setDisplay(holder);
    }

    public void loadCurrent() throws IOException {
        Asset asset=project.tv.current();
        if(asset==null) throw new IllegalStateException("No TV asset");
        releasePlayer();
        player=new MediaPlayer();
        player.setOnCompletionListener(this);
        if(surface!=null) player.setDisplay(surface);
        player.setDataSource(context, Uri.parse(asset.uri));
        player.prepare();
        long ms=framesToMillis(project.tv.positionFrame());
        if(ms>0) player.seekTo((int)Math.min(Integer.MAX_VALUE,ms));
    }

    public void play() throws IOException {
        if(player==null) loadCurrent();
        player.start();
        project.tv.play();
    }

    public void pause() {
        if(player!=null && player.isPlaying()) player.pause();
        syncPosition();
        project.tv.pause();
    }

    public void stop() {
        if(player!=null) player.stop();
        project.tv.stop();
        releasePlayer();
    }

    public void seekFrame(long frame) {
        project.tv.seek(frame);
        if(player!=null) player.seekTo((int)Math.min(Integer.MAX_VALUE,framesToMillis(frame)));
    }

    public void fastForwardSeconds(int seconds) { syncPosition(); project.tv.fastForwardSeconds(seconds); seekFrame(project.tv.positionFrame()); }

    public void rewindSeconds(int seconds) { syncPosition(); project.tv.rewindSeconds(seconds); seekFrame(project.tv.positionFrame()); }

    public void syncPosition() {
        if(player!=null) project.tv.seek(millisToFrames(player.getCurrentPosition()));
    }

    @Override public void onCompletion(MediaPlayer mp) {
        syncPosition();
        Asset next=project.tv.next();
        if(next==null){project.tv.stop();return;}
        try{loadCurrent();play();}catch(IOException e){project.tv.stop();}
    }

    public void release() {
        syncPosition();
        releasePlayer();
    }

    private void releasePlayer(){if(player!=null){player.release();player=null;}}
    private long framesToMillis(long frame){return frame*1000L/Math.max(1,project.transport.sampleRate());}
    private long millisToFrames(long ms){return ms*Math.max(1,project.transport.sampleRate())/1000L;}
}

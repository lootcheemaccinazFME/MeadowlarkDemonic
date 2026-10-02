package com.flymaccin.meadowlarkdemonic;

import java.util.*;

/** One Demonic TV playback session exposed through in-app, fullscreen and PiP surfaces. */
public final class DemonicTv {
    public enum Surface { IN_APP, FULLSCREEN, PICTURE_IN_PICTURE }
    public enum State { STOPPED, PLAYING, PAUSED }

    private final DemonicProject project;
    private final ArrayList<String> queue = new ArrayList<>();
    private int queueIndex = -1;
    private long positionFrame = 0;
    private Surface surface = Surface.IN_APP;
    private State state = State.STOPPED;
    private boolean poweredOn = true;
    private float volume = 1.0f;

    public DemonicTv(DemonicProject project) { this.project = project; }

    public Surface surface() { return surface; }
    public State state() { return state; }
    public long positionFrame() { return positionFrame; }
    public List<String> queue() { return Collections.unmodifiableList(queue); }
    public boolean poweredOn(){return poweredOn;}
    public float volume(){return volume;}

    public Asset current() {
        return queueIndex >= 0 && queueIndex < queue.size() ? project.asset(queue.get(queueIndex)) : null;
    }

    public void setSurface(Surface value) {
        if (value == null) throw new IllegalArgumentException("surface");
        surface = value;
    }

    public void setQueue(List<String> assetIds, int startIndex) {
        queue.clear();
        if (assetIds != null) for (String id : assetIds) {
            Asset a = requirePlayable(id);
            queue.add(a.id);
        }
        if (queue.isEmpty()) { queueIndex=-1; positionFrame=0; state=State.STOPPED; return; }
        if (startIndex < 0 || startIndex >= queue.size()) throw new IllegalArgumentException("startIndex");
        queueIndex=startIndex; positionFrame=0; state=State.PAUSED;
    }

    public void play(String assetId) {
        Asset a=requirePlayable(assetId);
        int i=queue.indexOf(a.id);
        if(i<0){queue.add(a.id);i=queue.size()-1;}
        queueIndex=i; state=State.PLAYING;
    }

    public void play(){if(!poweredOn)throw new IllegalStateException("TV power off");if(current()==null)throw new IllegalStateException("No TV asset");state=State.PLAYING;}
    public void pause(){if(state==State.PLAYING)state=State.PAUSED;}
    public void stop(){state=State.STOPPED;positionFrame=0;}
    public void seek(long frame){if(frame<0)throw new IllegalArgumentException("frame");positionFrame=frame;}
    public void fastForward(long frames){if(frames<0)throw new IllegalArgumentException("frames");positionFrame+=frames;}
    public void rewind(long frames){if(frames<0)throw new IllegalArgumentException("frames");positionFrame=Math.max(0,positionFrame-frames);}
    public void fastForwardSeconds(int seconds){fastForward((long)Math.max(0,seconds)*project.transport.sampleRate());}
    public void rewindSeconds(int seconds){rewind((long)Math.max(0,seconds)*project.transport.sampleRate());}
    public void advance(long frames){if(state==State.PLAYING)positionFrame=Math.max(0,positionFrame+frames);}

    public Asset next(){if(queueIndex+1>=queue.size())return null;queueIndex++;positionFrame=0;return current();}
    public Asset previous(){if(queueIndex<=0)return null;queueIndex--;positionFrame=0;return current();}
    public Asset channelUp(){return next();}
    public Asset channelDown(){return previous();}
    public float volumeUp(){volume=Math.min(1f,volume+0.05f);return volume;}
    public float volumeDown(){volume=Math.max(0f,volume-0.05f);return volume;}
    public boolean togglePower(){poweredOn=!poweredOn;if(!poweredOn)state=State.PAUSED;return poweredOn;}

    /** Lifecycle snapshot used when Android recreates a viewing surface. */
    public Session snapshot(){return new Session(current()==null?null:current().id,positionFrame,state,surface,queue,queueIndex);}
    public void restore(Session s){if(s==null)return;setQueue(s.queue,s.queue.isEmpty()?0:Math.max(0,Math.min(s.queueIndex,s.queue.size()-1)));positionFrame=Math.max(0,s.positionFrame);state=s.state;surface=s.surface;}

    public static final class Session{
        public final String assetId; public final long positionFrame; public final State state; public final Surface surface; public final List<String> queue; public final int queueIndex;
        Session(String assetId,long positionFrame,State state,Surface surface,List<String> queue,int queueIndex){this.assetId=assetId;this.positionFrame=positionFrame;this.state=state;this.surface=surface;this.queue=new ArrayList<>(queue);this.queueIndex=queueIndex;}
    }

    private Asset requirePlayable(String id){
        Asset a=project.asset(id);
        if(a==null)throw new IllegalArgumentException("Unknown asset "+id);
        if(a.kind!=Asset.Kind.VIDEO&&a.kind!=Asset.Kind.AUDIO)throw new IllegalArgumentException("TV requires audio/video asset");
        return a;
    }
}

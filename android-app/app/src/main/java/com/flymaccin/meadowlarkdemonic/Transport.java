package com.flymaccin.meadowlarkdemonic;

import org.json.JSONObject;

/** Single clock/playback authority for the entire project. */
public final class Transport {
 public enum State{STOPPED,PLAYING,RECORDING}
 private State state=State.STOPPED;
 private long frame=0;
 private int sampleRate=48000;
 private double bpm=120.0;

 public synchronized State state(){return state;}
 public synchronized long frame(){return frame;}
 public synchronized int sampleRate(){return sampleRate;}
 public synchronized double bpm(){return bpm;}
 public synchronized void play(){state=State.PLAYING;}
 public synchronized void record(){state=State.RECORDING;}
 public synchronized void stop(){state=State.STOPPED;}
 public synchronized void seek(long frame){this.frame=Math.max(0,frame);}
 public synchronized void advance(long frames){if(state!=State.STOPPED)frame=Math.max(0,frame+frames);}
 public synchronized void setBpm(double bpm){if(bpm<20||bpm>400)throw new IllegalArgumentException("BPM");this.bpm=bpm;}
 public synchronized JSONObject toJson(){try{return new JSONObject().put("state",state.name()).put("frame",frame).put("sampleRate",sampleRate).put("bpm",bpm);}catch(Exception e){throw new IllegalStateException(e);}}
 public static Transport fromJson(JSONObject o){Transport t=new Transport();if(o==null)return t;t.frame=o.optLong("frame",0);t.sampleRate=o.optInt("sampleRate",48000);t.bpm=o.optDouble("bpm",120);try{t.state=State.valueOf(o.optString("state","STOPPED"));}catch(Exception ignored){}return t;}
}
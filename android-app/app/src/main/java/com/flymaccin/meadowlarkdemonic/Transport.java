package com.flymaccin.meadowlarkdemonic;

import org.json.JSONObject;

/** Single clock/playback authority for the entire project. */
public final class Transport {
 public enum State{STOPPED,PLAYING,RECORDING}
 private State state=State.STOPPED;
 private long frame=0;
 private int sampleRate=48000;
 private double bpm=120.0;
 private boolean loopEnabled=false,punchEnabled=false;
 private long loopStart=0,loopEnd=0,punchIn=0,punchOut=0;

 public synchronized State state(){return state;}
 public synchronized long frame(){return frame;}
 public synchronized int sampleRate(){return sampleRate;}
 public synchronized double bpm(){return bpm;}
 public synchronized boolean loopEnabled(){return loopEnabled;}
 public synchronized long loopStart(){return loopStart;}
 public synchronized long loopEnd(){return loopEnd;}
 public synchronized boolean punchEnabled(){return punchEnabled;}
 public synchronized long punchIn(){return punchIn;}
 public synchronized long punchOut(){return punchOut;}
 public synchronized void setLoop(long start,long end,boolean enabled){if(start<0||end<=start)throw new IllegalArgumentException("loop range");loopStart=start;loopEnd=end;loopEnabled=enabled;}
 public synchronized void setPunch(long in,long out,boolean enabled){if(in<0||out<=in)throw new IllegalArgumentException("punch range");punchIn=in;punchOut=out;punchEnabled=enabled;}
 public synchronized boolean inPunchRange(){return !punchEnabled||(frame>=punchIn&&frame<punchOut);}
 public synchronized void play(){state=State.PLAYING;}
 public synchronized void record(){state=State.RECORDING;}
 public synchronized void stop(){state=State.STOPPED;}
 public synchronized void seek(long frame){this.frame=Math.max(0,frame);}
 public synchronized void advance(long frames){if(state!=State.STOPPED){frame=Math.max(0,frame+frames);if(loopEnabled&&loopEnd>loopStart&&frame>=loopEnd){long span=loopEnd-loopStart;frame=loopStart+((frame-loopStart)%span);}}}
 public synchronized void setBpm(double bpm){if(bpm<20||bpm>400)throw new IllegalArgumentException("BPM");this.bpm=bpm;}
 public synchronized JSONObject toJson(){try{return new JSONObject().put("state",state.name()).put("frame",frame).put("sampleRate",sampleRate).put("bpm",bpm).put("loopEnabled",loopEnabled).put("loopStart",loopStart).put("loopEnd",loopEnd).put("punchEnabled",punchEnabled).put("punchIn",punchIn).put("punchOut",punchOut);}catch(Exception e){throw new IllegalStateException(e);}}
 public static Transport fromJson(JSONObject o){Transport t=new Transport();if(o==null)return t;t.frame=o.optLong("frame",0);t.sampleRate=o.optInt("sampleRate",48000);t.bpm=o.optDouble("bpm",120);t.loopEnabled=o.optBoolean("loopEnabled",false);t.loopStart=o.optLong("loopStart",0);t.loopEnd=o.optLong("loopEnd",0);t.punchEnabled=o.optBoolean("punchEnabled",false);t.punchIn=o.optLong("punchIn",0);t.punchOut=o.optLong("punchOut",0);try{t.state=State.valueOf(o.optString("state","STOPPED"));}catch(Exception ignored){}return t;}
}
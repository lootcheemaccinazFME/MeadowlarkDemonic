package com.flymaccin.meadowlarkdemonic;
import org.json.JSONObject;
public final class MidiEvent{
 public final long frame; public final int status,data1,data2;
 public MidiEvent(long frame,int status,int data1,int data2){if(frame<0)throw new IllegalArgumentException("frame");this.frame=frame;this.status=status&255;this.data1=data1&127;this.data2=data2&127;}
 public JSONObject toJson(){try{return new JSONObject().put("frame",frame).put("status",status).put("data1",data1).put("data2",data2);}catch(Exception e){throw new IllegalStateException(e);}}
 public static MidiEvent fromJson(JSONObject o){return new MidiEvent(o.optLong("frame",0),o.optInt("status",144),o.optInt("data1",60),o.optInt("data2",100));}
}
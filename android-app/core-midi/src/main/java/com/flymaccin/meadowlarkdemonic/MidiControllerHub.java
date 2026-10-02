package com.flymaccin.meadowlarkdemonic;
import java.util.*;import org.json.*;
public final class MidiControllerHub{
 public static final class Mapping{public final int channel,control;public final String target;Mapping(int c,int k,String t){channel=c;control=k;target=t;}}
 private final ArrayList<Mapping> mappings=new ArrayList<>(); private double deadZone=.05,sensitivity=1;
 public void map(int channel,int control,String target){mappings.add(new Mapping(channel,control,target));} public List<Mapping> mappings(){return Collections.unmodifiableList(mappings);}
 public void calibrate(double d,double s){deadZone=Math.max(0,Math.min(.5,d));sensitivity=Math.max(.1,Math.min(4,s));} public double deadZone(){return deadZone;} public double sensitivity(){return sensitivity;}
 public JSONObject toJson(){try{JSONArray a=new JSONArray();for(Mapping m:mappings)a.put(new JSONObject().put("channel",m.channel).put("control",m.control).put("target",m.target));return new JSONObject().put("deadZone",deadZone).put("sensitivity",sensitivity).put("mappings",a);}catch(Exception e){throw new IllegalStateException(e);}}
 public void loadJson(JSONObject o){if(o==null)return;calibrate(o.optDouble("deadZone",deadZone),o.optDouble("sensitivity",sensitivity));mappings.clear();JSONArray a=o.optJSONArray("mappings");if(a!=null)for(int i=0;i<a.length();i++){JSONObject j=a.optJSONObject(i);if(j!=null)map(j.optInt("channel"),j.optInt("control"),j.optString("target"));}}
}

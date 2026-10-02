package com.flymaccin.meadowlarkdemonic;
import java.util.*;import org.json.*;
public final class SequencerTracker{
 public static final class Step{public boolean on;public int note=36,velocity=100;public double probability=1.0,micro=0;}
 private final ArrayList<Step> steps=new ArrayList<>(); private double swing=.55;
 public SequencerTracker(){for(int i=0;i<16;i++)steps.add(new Step());}
 public List<Step> steps(){return Collections.unmodifiableList(steps);} public double swing(){return swing;} public void swing(double v){swing=Math.max(.5,Math.min(.75,v));}
 public long frameForStep(int i,long framesPerBeat){long base=i*framesPerBeat/4;return base+(i%2==1?(long)((swing-.5)*framesPerBeat/2):0);}
 public JSONObject toJson(){try{JSONArray a=new JSONArray();for(Step s:steps)a.put(new JSONObject().put("on",s.on).put("note",s.note).put("velocity",s.velocity).put("probability",s.probability).put("micro",s.micro));return new JSONObject().put("swing",swing).put("steps",a);}catch(Exception e){throw new IllegalStateException(e);}}
 public void loadJson(JSONObject o){if(o==null)return;swing(o.optDouble("swing",swing));JSONArray a=o.optJSONArray("steps");if(a!=null)for(int i=0;i<Math.min(a.length(),steps.size());i++){JSONObject j=a.optJSONObject(i);if(j!=null){Step s=steps.get(i);s.on=j.optBoolean("on",s.on);s.note=j.optInt("note",s.note);s.velocity=j.optInt("velocity",s.velocity);s.probability=j.optDouble("probability",s.probability);s.micro=j.optDouble("micro",s.micro);}}}
}

package com.flymaccin.meadowlarkdemonic;
import org.json.*;
public final class HyphyEngine{
 private double swing=.58,micro=.0; private int roll=2; private boolean bassSlides=true;
 public double swing(){return swing;} public void swing(double v){swing=Math.max(.5,Math.min(.75,v));}
 public double microtiming(){return micro;} public void microtiming(double v){micro=Math.max(-.25,Math.min(.25,v));}
 public int roll(){return roll;} public void roll(int n){roll=Math.max(1,Math.min(16,n));}
 public boolean bassSlides(){return bassSlides;} public void bassSlides(boolean v){bassSlides=v;}
 public JSONObject toJson(){try{return new JSONObject().put("swing",swing).put("micro",micro).put("roll",roll).put("bassSlides",bassSlides);}catch(Exception e){throw new IllegalStateException(e);}}
 public void loadJson(JSONObject o){if(o==null)return;swing(o.optDouble("swing",swing));microtiming(o.optDouble("micro",micro));roll(o.optInt("roll",roll));bassSlides(o.optBoolean("bassSlides",bassSlides));}
}

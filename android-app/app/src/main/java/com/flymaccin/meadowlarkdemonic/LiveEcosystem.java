package com.flymaccin.meadowlarkdemonic;
import org.json.*;
public final class LiveEcosystem{
 public enum Mode{OFF,OPEN_MIC,STAGE,JAM,BEAT_BATTLE,BACKSTAGE}
 private Mode mode=Mode.OFF; private boolean captureMultitrack=false;
 public Mode mode(){return mode;} public void mode(Mode m){mode=m==null?Mode.OFF:m;} public boolean captureMultitrack(){return captureMultitrack;} public void captureMultitrack(boolean v){captureMultitrack=v;}
 public JSONObject toJson(){try{return new JSONObject().put("mode",mode.name()).put("captureMultitrack",captureMultitrack);}catch(Exception e){throw new IllegalStateException(e);}}
 public void loadJson(JSONObject o){if(o==null)return;try{mode(Mode.valueOf(o.optString("mode","OFF")));}catch(Exception ignored){mode(Mode.OFF);}captureMultitrack(o.optBoolean("captureMultitrack",false));}
}

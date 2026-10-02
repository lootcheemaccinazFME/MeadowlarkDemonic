package com.flymaccin.meadowlarkdemonic;
public final class LiveEcosystem{
 public enum Mode{OFF,OPEN_MIC,STAGE,JAM,BEAT_BATTLE,BACKSTAGE}
 private Mode mode=Mode.OFF; private boolean captureMultitrack=false;
 public Mode mode(){return mode;} public void mode(Mode m){mode=m==null?Mode.OFF:m;} public boolean captureMultitrack(){return captureMultitrack;} public void captureMultitrack(boolean v){captureMultitrack=v;}
}

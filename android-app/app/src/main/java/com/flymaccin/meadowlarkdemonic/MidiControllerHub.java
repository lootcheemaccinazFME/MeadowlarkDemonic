package com.flymaccin.meadowlarkdemonic;
import java.util.*;
public final class MidiControllerHub{
 public static final class Mapping{public final int channel,control;public final String target;Mapping(int c,int k,String t){channel=c;control=k;target=t;}}
 private final ArrayList<Mapping> mappings=new ArrayList<>(); private double deadZone=.05,sensitivity=1;
 public void map(int channel,int control,String target){mappings.add(new Mapping(channel,control,target));}
 public List<Mapping> mappings(){return Collections.unmodifiableList(mappings);} public void calibrate(double d,double s){deadZone=Math.max(0,Math.min(.5,d));sensitivity=Math.max(.1,Math.min(4,s));}
 public double deadZone(){return deadZone;} public double sensitivity(){return sensitivity;}
}

package com.flymaccin.meadowlarkdemonic;
import java.util.*;
public final class SequencerTracker{
 public static final class Step{public boolean on;public int note=36,velocity=100;public double probability=1.0,micro=0;}
 private final ArrayList<Step> steps=new ArrayList<>(); private double swing=.55;
 public SequencerTracker(){for(int i=0;i<16;i++)steps.add(new Step());}
 public List<Step> steps(){return Collections.unmodifiableList(steps);} public double swing(){return swing;}
 public void swing(double v){swing=Math.max(.5,Math.min(.75,v));}
 public long frameForStep(int i,long framesPerBeat){long base=i*framesPerBeat/4;return base+(i%2==1?(long)((swing-.5)*framesPerBeat/2):0);}
}

package com.flymaccin.meadowlarkdemonic;
/** Mixer facade. AudioGraph nodes remain the sole persistent DSP authority. */
public final class Mixer{
 private final DemonicProject project;
 public Mixer(DemonicProject p){project=p;}
 private AudioGraph.Node channel(String trackId){Track t=project.track(trackId);if(t==null)throw new IllegalArgumentException("Unknown track "+trackId);AudioGraph.Node n=project.audioGraph.node(t.graphNodeId);if(n==null)throw new IllegalArgumentException("Track has no audio channel");return n;}
 public void gain(String trackId,double linear){channel(trackId).set("gain",Math.max(0,linear));}
 public void pan(String trackId,double pan){channel(trackId).set("pan",Math.max(-1,Math.min(1,pan)));}
 public void mute(String trackId,boolean on){channel(trackId).set("mute",on?1:0);}
 public void solo(String trackId,boolean on){channel(trackId).set("solo",on?1:0);}
 public void masterGain(double linear){project.audioGraph.node(AudioGraph.MASTER).set("gain",Math.max(0,linear));}
}
package com.flymaccin.meadowlarkdemonic;

/** Timing helper for click/count-in; canonical Transport remains the only clock. */
public final class Metronome {
 private final DemonicProject project;
 private boolean enabled; private int countInBars=1; private int beatsPerBar=4;
 public Metronome(DemonicProject p){project=p;}
 public boolean enabled(){return enabled;} public void enabled(boolean on){enabled=on;}
 public int countInBars(){return countInBars;} public void countInBars(int bars){countInBars=Math.max(0,bars);}
 public int beatsPerBar(){return beatsPerBar;} public void beatsPerBar(int beats){beatsPerBar=Math.max(1,beats);}
 public long framesPerBeat(){return Math.max(1,Math.round(project.transport.sampleRate()*60.0/project.transport.bpm()));}
 public long countInFrames(){return framesPerBeat()*beatsPerBar*countInBars;}
 public boolean clickAt(long frame){if(!enabled)return false;long beat=framesPerBeat();return frame>=0&&frame%beat==0;}
 public boolean accentAt(long frame){if(!clickAt(frame))return false;return (frame/framesPerBeat())%beatsPerBar==0;}
}

package com.flymaccin.meadowlarkdemonic;

/** Musical grid derived only from canonical Transport tempo/sample rate. */
public final class Grid {
 private final DemonicProject project;
 public Grid(DemonicProject p){project=p;}
 public long framesPerBeat(){return Math.max(1,Math.round(project.transport.sampleRate()*60.0/project.transport.bpm()));}
 public long framesForDivision(int divisionsPerBeat){if(divisionsPerBeat<1)throw new IllegalArgumentException("division");return Math.max(1,framesPerBeat()/divisionsPerBeat);}
 public long snap(long frame,int divisionsPerBeat){long q=framesForDivision(divisionsPerBeat);return Math.max(0,Math.round((double)frame/q)*q);}
 public void quantizeClip(String clipId,int divisionsPerBeat){Clip c=null;for(Clip x:project.clips())if(x.id.equals(clipId)){c=x;break;}if(c==null)throw new IllegalArgumentException("Unknown clip");project.arrangement.move(c.id,snap(c.startFrame,divisionsPerBeat));}
}

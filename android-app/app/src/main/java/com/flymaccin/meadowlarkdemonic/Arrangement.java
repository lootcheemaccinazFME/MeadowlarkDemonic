package com.flymaccin.meadowlarkdemonic;
/** Arrangement controller over canonical Clips. Mutations flow through the one UndoHistory. */
public final class Arrangement{
 private final DemonicProject project;
 public Arrangement(DemonicProject p){project=p;}
 private Clip clip(String id){for(Clip c:project.clips())if(c.id.equals(id))return c;throw new IllegalArgumentException("Unknown clip "+id);}
 public void move(String clipId,long startFrame){final Clip c=clip(clipId);final long before=c.startFrame;final long after=Math.max(0,startFrame);project.history.execute(new UndoHistory.Command(){public void apply(){c.startFrame=after;}public void revert(){c.startFrame=before;}public String label(){return "Move clip";}});}
 public void trim(String clipId,long startFrame,long lengthFrames,long sourceOffsetFrames){if(startFrame<0||lengthFrames<1||sourceOffsetFrames<0)throw new IllegalArgumentException("Invalid trim");final Clip c=clip(clipId);final long bs=c.startFrame,bl=c.lengthFrames,bo=c.sourceOffsetFrames;project.history.execute(new UndoHistory.Command(){public void apply(){c.startFrame=startFrame;c.lengthFrames=lengthFrames;c.sourceOffsetFrames=sourceOffsetFrames;}public void revert(){c.startFrame=bs;c.lengthFrames=bl;c.sourceOffsetFrames=bo;}public String label(){return "Trim clip";}});}
}
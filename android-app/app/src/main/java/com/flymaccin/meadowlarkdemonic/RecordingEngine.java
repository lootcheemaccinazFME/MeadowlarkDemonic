package com.flymaccin.meadowlarkdemonic;
/** Recording facade. Captures become canonical Assets and Clips on the shared transport timeline. */
public final class RecordingEngine{
 private final DemonicProject project; private boolean monitoring; private String armedTrackId;
 public RecordingEngine(DemonicProject p){project=p;}
 public void arm(String trackId){Track t=project.track(trackId);if(t==null||t.kind!=Track.Kind.AUDIO)throw new IllegalArgumentException("Audio track required");armedTrackId=trackId;}
 public void disarm(){armedTrackId=null;monitoring=false;}
 public String armedTrackId(){return armedTrackId;}
 public boolean armed(){return armedTrackId!=null;}
 public void monitoring(boolean enabled){if(enabled&&!armed())throw new IllegalStateException("Arm an audio track first");monitoring=enabled;}
 public boolean monitoring(){return monitoring;}
 public long recordStartFrame(){return Math.max(0,project.transport.frame()-project.metronome.countInFrames());}
 public Clip commitAudio(String trackId,String uri,String name,long startFrame,long lengthFrames){
  Track t=project.track(trackId);if(t==null||t.kind!=Track.Kind.AUDIO)throw new IllegalArgumentException("Audio track required");
  Asset a=new Asset(Asset.Kind.AUDIO,uri,name);
  project.addAssetUndoable(a);return project.addClipUndoable(new Clip(trackId,a.id,startFrame,lengthFrames));
 }
 public Clip commitMidi(String trackId,Asset midiAsset,long startFrame,long lengthFrames){
  Track t=project.track(trackId);if(t==null||t.kind!=Track.Kind.MIDI||midiAsset.kind!=Asset.Kind.MIDI)throw new IllegalArgumentException("MIDI track/asset required");
  project.addAssetUndoable(midiAsset);return project.addClipUndoable(new Clip(trackId,midiAsset.id,startFrame,lengthFrames));
 }
}
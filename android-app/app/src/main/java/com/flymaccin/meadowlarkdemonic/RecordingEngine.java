package com.flymaccin.meadowlarkdemonic;
/** Recording facade. Captures become canonical Assets and Clips on the shared transport timeline. */
public final class RecordingEngine{
 private final DemonicProject project;
 public RecordingEngine(DemonicProject p){project=p;}
 public Clip commitAudio(String trackId,String uri,String name,long startFrame,long lengthFrames){
  Track t=project.track(trackId);if(t==null||t.kind!=Track.Kind.AUDIO)throw new IllegalArgumentException("Audio track required");
  Asset a=project.addAsset(new Asset(Asset.Kind.AUDIO,uri,name));
  return project.addClip(new Clip(trackId,a.id,startFrame,lengthFrames));
 }
 public Clip commitMidi(String trackId,Asset midiAsset,long startFrame,long lengthFrames){
  Track t=project.track(trackId);if(t==null||t.kind!=Track.Kind.MIDI||midiAsset.kind!=Asset.Kind.MIDI)throw new IllegalArgumentException("MIDI track/asset required");
  project.addAsset(midiAsset);return project.addClip(new Clip(trackId,midiAsset.id,startFrame,lengthFrames));
 }
}
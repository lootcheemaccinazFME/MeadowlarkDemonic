package com.flymaccin.meadowlarkdemonic;
/** AI/Maestro adapter. Generated material becomes canonical Demonic project objects. */
public final class MaestroBridge{
 private final DemonicProject project;
 public MaestroBridge(DemonicProject p){project=p;}
 public Clip ingestAudio(String uri,String name,long start,long length){
  Asset a=project.addAsset(new Asset(Asset.Kind.AUDIO,uri,name));
  Track t=project.addTrack(new Track(Track.Kind.AUDIO,name));
  return project.addClip(new Clip(t.id,a.id,start,length));
 }
 public Clip ingestMidi(Asset midi,String name,long start,long length){
  if(midi.kind!=Asset.Kind.MIDI)throw new IllegalArgumentException("MIDI asset required");
  project.addAsset(midi);Track t=project.addTrack(new Track(Track.Kind.MIDI,name));
  return project.addClip(new Clip(t.id,midi.id,start,length));
 }
 public Clip ingestMedia(Asset.Kind kind,String uri,String name,long start,long length){
  if(kind!=Asset.Kind.IMAGE&&kind!=Asset.Kind.VIDEO)throw new IllegalArgumentException("Image/video required");
  Asset a=project.addAsset(new Asset(kind,uri,name));
  Track t=project.addTrack(new Track(kind==Asset.Kind.IMAGE?Track.Kind.IMAGE:Track.Kind.VIDEO,name));
  return project.addClip(new Clip(t.id,a.id,start,length));
 }
}
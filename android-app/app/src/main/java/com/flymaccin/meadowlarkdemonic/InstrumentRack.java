package com.flymaccin.meadowlarkdemonic;
/** Instruments compile into AudioGraph and never own project or transport state. */
public final class InstrumentRack{
 private final DemonicProject project;
 public InstrumentRack(DemonicProject p){project=p;}
 public String attach(String trackId,String type){
  Track t=project.track(trackId);if(t==null||t.kind!=Track.Kind.MIDI)throw new IllegalArgumentException("MIDI track required");
  String id="instrument:"+trackId;
  AudioGraph.Node existing=project.audioGraph.node(id);
  if(existing==null){project.audioGraph.addNode(id,"INSTRUMENT_"+type.toUpperCase());project.audioGraph.connect(id,t.graphNodeId);}
  return id;
 }
}
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
 public AudioGraph.Node addInsert(String trackId,String effectType){Track t=project.track(trackId);if(t==null)throw new IllegalArgumentException("Unknown track "+trackId);String id="fx:"+trackId+":"+java.util.UUID.randomUUID();AudioGraph.Node fx=project.audioGraph.addNode(id,"FX_"+effectType.toUpperCase());project.audioGraph.disconnect(t.graphNodeId,AudioGraph.MASTER);project.audioGraph.connect(t.graphNodeId,id);project.audioGraph.connect(id,AudioGraph.MASTER);return fx;}
 public void removeInsert(String trackId,String effectNodeId){Track t=project.track(trackId);AudioGraph.Node fx=project.audioGraph.node(effectNodeId);if(t==null||fx==null)throw new IllegalArgumentException("Unknown insert");project.audioGraph.removeNode(effectNodeId);project.audioGraph.connect(t.graphNodeId,AudioGraph.MASTER);}
 public void effectParameter(String effectNodeId,String parameter,double value){AudioGraph.Node fx=project.audioGraph.node(effectNodeId);if(fx==null||!fx.type.startsWith("FX_"))throw new IllegalArgumentException("Unknown effect");fx.set(parameter,value);}
 public AudioGraph.Node addBus(String name){String id="bus:"+java.util.UUID.randomUUID();AudioGraph.Node bus=project.audioGraph.addNode(id,"BUS");bus.set("gain",1.0);project.audioGraph.connect(id,AudioGraph.MASTER);return bus;}
 public void send(String trackId,String busNodeId,double level){Track t=project.track(trackId);AudioGraph.Node bus=project.audioGraph.node(busNodeId);if(t==null||bus==null||!"BUS".equals(bus.type))throw new IllegalArgumentException("Unknown send route");String id="send:"+trackId+":"+busNodeId;AudioGraph.Node s=project.audioGraph.node(id);if(s==null){s=project.audioGraph.addNode(id,"SEND");project.audioGraph.connect(t.graphNodeId,id);project.audioGraph.connect(id,busNodeId);}s.set("gain",Math.max(0,level));}
 public void busGain(String busNodeId,double gain){AudioGraph.Node bus=project.audioGraph.node(busNodeId);if(bus==null||!"BUS".equals(bus.type))throw new IllegalArgumentException("Unknown bus");bus.set("gain",Math.max(0,gain));}
}
package com.flymaccin.meadowlarkdemonic;

import java.util.*;

/** Transplants add-on room state into the real canonical Demonic runtime. Owns no project state. */
public final class RoomRuntimeTransplant {
 private final DemonicProject project;
 public RoomRuntimeTransplant(DemonicProject p){if(p==null)throw new IllegalArgumentException("project");project=p;}

 public Report transplant(){
  ArrayList<String> graphNodes=new ArrayList<>();
  graphNodes.addAll(AddOnSourceGraphCompiler.compile(project));
  graphNodes.addAll(AddOnGraphCompiler.compileMastering(project.audioGraph,project.addOns.mastering,null));
  VaultIngestion.syncProjectAssets(project);
  new UnifiedDemonicDaw(project).verifySingleSpine();
  return new Report(graphNodes,project.assets().size(),project.tracks().size(),project.clips().size());
 }

 /** Runtime smoke path using only deterministic engines that are actually present in this APK. */
 public void runtimeTest(){
  Report r=transplant();
  if(project.audioGraph.node(AudioGraph.MASTER)==null)throw new IllegalStateException("Master runtime missing");
  short[] pcm={100,-100,500,-500,1200,-1200,0,0};
  MeterAnalyzer.analyze(pcm,project.addOns.meters);
  if(Float.isNaN(project.addOns.meters.latest.peakDb))throw new IllegalStateException("Meter runtime failed");
  ProductionCapabilities caps=new ProductionCapabilities();
  ProductionAdapters.Registry adapters=new ProductionAdapters.Registry();
  ProductionProcessing processing=new ProductionProcessing(adapters,caps);
  DemonicAddOnSuite.RepairPlan plan=new DemonicAddOnSuite.RepairPlan();plan.denoise=true;plan.declick=true;
  if(processing.repair(pcm,project.transport.sampleRate(),plan).length!=pcm.length)throw new IllegalStateException("Repair runtime failed");
  if(PreviewTimePitch.process(pcm,1.0,0).length==0)throw new IllegalStateException("Time/pitch preview runtime failed");
  // Android filesystem/export/recovery verification belongs to instrumentation/device certification, not JVM unit tests.\n  new UnifiedDemonicDaw(project).verifySingleSpine();
 }

 public static final class Report{
  public final List<String> graphNodes;public final int assets,tracks,clips;
  Report(List<String> n,int a,int t,int c){graphNodes=Collections.unmodifiableList(new ArrayList<>(n));assets=a;tracks=t;clips=c;}
 }
}

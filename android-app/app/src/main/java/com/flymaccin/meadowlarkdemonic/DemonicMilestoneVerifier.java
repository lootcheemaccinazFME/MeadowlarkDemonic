package com.flymaccin.meadowlarkdemonic;

/** Small dependency-free milestone verifier callable from instrumentation or debug UI. */
public final class DemonicMilestoneVerifier {
 private DemonicMilestoneVerifier(){}
 public static void verify(DemonicProject p){
  if(p==null||p.addOns==null)throw new IllegalStateException("Workspace missing");
  String json=p.toJson().toString();DemonicProject copy=DemonicProject.fromJson(json);
  if(copy.addOns==null)throw new IllegalStateException("Workspace round trip failed");
  DemonicAddOnSuite.Snapshot s=VersionVaultController.checkpoint(p,null,"Verifier");
  if(s==null||s.projectJson.length()==0)throw new IllegalStateException("Version checkpoint failed");
  AddOnSourceGraphCompiler.compile(p);
  VaultIngestion.syncProjectAssets(p);
  if(p.audioGraph.node(AudioGraph.MASTER)==null)throw new IllegalStateException("Master graph missing");
  MeterAnalyzer.analyze(new short[]{100,-100,200,-200},p.addOns.meters);
  if(Float.isNaN(p.addOns.meters.latest.peakDb))throw new IllegalStateException("Meter failed");
 }
}

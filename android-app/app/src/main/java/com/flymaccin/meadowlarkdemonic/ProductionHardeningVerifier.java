package com.flymaccin.meadowlarkdemonic;

/** Dependency-free hardening smoke checks for deterministic fallback DSP and capability truthfulness. */
public final class ProductionHardeningVerifier {
 private ProductionHardeningVerifier(){}
 public static void verify(){
  ProductionCapabilities caps=new ProductionCapabilities();ProductionAdapters.Registry reg=new ProductionAdapters.Registry();reg.publish(caps);
  if(caps.ready(ProductionCapabilities.Capability.STEM_SEPARATION)||caps.ready(ProductionCapabilities.Capability.EXTERNAL_PLUGIN_HOST))throw new IllegalStateException("Unavailable provider reported ready");
  short[] src={0,0,120,120,30000,-30000,140,140,0,0};short[] stretched=PreviewTimePitch.process(src,1.25,0);if(stretched.length==0)throw new IllegalStateException("Preview time pitch failed");
  short[] cleaned=src.clone();DeclickDsp.apply(cleaned,1f);NoiseReductionDsp.apply(cleaned,.5f);
  ProductionProcessing p=new ProductionProcessing(reg,caps);DemonicAddOnSuite.RepairPlan plan=new DemonicAddOnSuite.RepairPlan();plan.denoise=true;plan.declick=true;short[] repaired=p.repair(src,48000,plan);if(repaired.length!=src.length)throw new IllegalStateException("Repair changed frame count");
 }
}

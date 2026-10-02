package com.flymaccin.meadowlarkdemonic;

/** Standards meter facade. Never labels the legacy block estimate as standards-compliant LUFS. */
public final class StandardsMeter {
 private StandardsMeter(){}
 public static ProductionAdapters.Result measure(short[] pcm,int sampleRate,ProductionAdapters.Registry adapters,ProductionCapabilities caps){
  if(adapters==null||adapters.loudness==null)throw new IllegalStateException("Standards loudness adapter unavailable");
  ProductionAdapters.Result r=adapters.loudness.measure(pcm,sampleRate);
  if(caps!=null){caps.register(ProductionCapabilities.Capability.EBU_R128_LUFS,ProductionCapabilities.State.READY,adapters.loudness.provider());caps.register(ProductionCapabilities.Capability.TRUE_PEAK,ProductionCapabilities.State.READY,adapters.loudness.provider());}
  return r;
 }
}

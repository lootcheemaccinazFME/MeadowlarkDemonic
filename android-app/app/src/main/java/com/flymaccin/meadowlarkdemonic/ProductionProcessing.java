package com.flymaccin.meadowlarkdemonic;

import java.util.*;

/** Single production processing gateway: prefers registered HQ providers and falls back only to explicitly preview-grade DSP. */
public final class ProductionProcessing {
 private final ProductionAdapters.Registry adapters; private final ProductionCapabilities capabilities;
 public ProductionProcessing(ProductionAdapters.Registry a,ProductionCapabilities c){adapters=a==null?new ProductionAdapters.Registry():a;capabilities=c==null?new ProductionCapabilities():c;adapters.publish(capabilities);}
 public short[] timePitch(short[] pcm,int sampleRate,double ratio,double semitones){if(adapters.timePitch!=null)return adapters.timePitch.process(pcm,sampleRate,ratio,semitones);return PreviewTimePitch.process(pcm,ratio,semitones);}
 public short[] repair(short[] pcm,int sampleRate,DemonicAddOnSuite.RepairPlan plan){short[] out=pcm==null?new short[0]:pcm.clone();if(plan==null)return out;if(plan.denoise){if(adapters.repair!=null)out=adapters.repair.denoise(out,sampleRate,.65f);else NoiseReductionDsp.apply(out,.55f);}if(plan.declick&&adapters.repair!=null)out=adapters.repair.declick(out,sampleRate,.65f);RepairDsp.apply(out,plan,sampleRate);return out;}
 public Map<String,short[]> stems(short[] pcm,int sampleRate){if(adapters.stems==null)throw new IllegalStateException("Stem separation provider unavailable");return adapters.stems.separate(pcm,sampleRate);}
 public ProductionAdapters.Result loudness(short[] pcm,int sampleRate){return StandardsMeter.measure(pcm,sampleRate,adapters,capabilities);}
 public Object openPlugin(String pluginId){if(adapters.plugins==null||!adapters.plugins.supported())throw new IllegalStateException("External plugin host unavailable");return adapters.plugins.open(pluginId);}
 public ProductionCapabilities capabilities(){return capabilities;}
}

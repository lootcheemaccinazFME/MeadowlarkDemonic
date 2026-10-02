package com.flymaccin.meadowlarkdemonic;

import java.util.*;

/** Production DSP/plugin seams. Concrete native, ML or platform backends must implement these contracts. */
public final class ProductionAdapters {
 private ProductionAdapters(){}
 public interface TimePitch { short[] process(short[] stereo,int sampleRate,double stretchRatio,double semitones); String provider(); }
 public interface Repair { short[] denoise(short[] stereo,int sampleRate,float strength); short[] declick(short[] stereo,int sampleRate,float strength); String provider(); }
 public interface StemSeparator { Map<String,short[]> separate(short[] stereo,int sampleRate); String provider(); }
 public interface Loudness { Result measure(short[] stereo,int sampleRate); String provider(); }
 public interface PluginHost { boolean supported(); Object open(String pluginId); String provider(); }
 public static final class Result { public final float integratedLufs,truePeakDbtp; public Result(float l,float t){integratedLufs=l;truePeakDbtp=t;} }

 public static final class Registry {
  public TimePitch timePitch; public Repair repair; public StemSeparator stems; public Loudness loudness; public PluginHost plugins;
  public void publish(ProductionCapabilities c){
   c.register(ProductionCapabilities.Capability.HQ_TIME_STRETCH,timePitch==null?ProductionCapabilities.State.UNAVAILABLE:ProductionCapabilities.State.READY,timePitch==null?null:timePitch.provider());
   c.register(ProductionCapabilities.Capability.HQ_PITCH_SHIFT,timePitch==null?ProductionCapabilities.State.UNAVAILABLE:ProductionCapabilities.State.READY,timePitch==null?null:timePitch.provider());
   c.register(ProductionCapabilities.Capability.DENOISE,repair==null?ProductionCapabilities.State.UNAVAILABLE:ProductionCapabilities.State.READY,repair==null?null:repair.provider());
   c.register(ProductionCapabilities.Capability.DECLICK,repair==null?ProductionCapabilities.State.UNAVAILABLE:ProductionCapabilities.State.READY,repair==null?null:repair.provider());
   c.register(ProductionCapabilities.Capability.STEM_SEPARATION,stems==null?ProductionCapabilities.State.UNAVAILABLE:ProductionCapabilities.State.READY,stems==null?null:stems.provider());
   c.register(ProductionCapabilities.Capability.EBU_R128_LUFS,loudness==null?ProductionCapabilities.State.UNAVAILABLE:ProductionCapabilities.State.READY,loudness==null?null:loudness.provider());
   c.register(ProductionCapabilities.Capability.TRUE_PEAK,loudness==null?ProductionCapabilities.State.UNAVAILABLE:ProductionCapabilities.State.READY,loudness==null?null:loudness.provider());
   boolean ph=plugins!=null&&plugins.supported();c.register(ProductionCapabilities.Capability.EXTERNAL_PLUGIN_HOST,ph?ProductionCapabilities.State.READY:ProductionCapabilities.State.UNAVAILABLE,plugins==null?null:plugins.provider());
  }
 }
}

package com.flymaccin.meadowlarkdemonic;

import java.util.*;

/** Truthful capability registry: features are READY only when a concrete adapter registers itself. */
public final class ProductionCapabilities {
 public enum Capability { STEM_SEPARATION, DENOISE, DECLICK, DEHUM, HQ_TIME_STRETCH, HQ_PITCH_SHIFT, EBU_R128_LUFS, TRUE_PEAK, EXTERNAL_PLUGIN_HOST, DEVICE_AUDIO_VALIDATION }
 public enum State { UNAVAILABLE, EXPERIMENTAL, READY }
 private final EnumMap<Capability,State> states=new EnumMap<>(Capability.class);
 private final EnumMap<Capability,String> providers=new EnumMap<>(Capability.class);
 public ProductionCapabilities(){for(Capability c:Capability.values())states.put(c,State.UNAVAILABLE);}
 public void register(Capability c,State s,String provider){states.put(c,s==null?State.UNAVAILABLE:s);if(provider!=null)providers.put(c,provider);}
 public State state(Capability c){return states.get(c);}
 public String provider(Capability c){return providers.get(c);}
 public boolean ready(Capability c){return state(c)==State.READY;}
 public Map<Capability,State> snapshot(){return Collections.unmodifiableMap(new EnumMap<>(states));}
}

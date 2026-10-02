package com.flymaccin.meadowlarkdemonic;

import java.util.*;

/** Evidence-based certification gate for the final production lanes. */
public final class ProductionCertification {
 public enum Lane { NATIVE_TIME_PITCH, ML_REPAIR, ML_STEMS, STANDARDS_METERING, ANDROID_PLUGIN_HOST, MOTOROLA_DEVICE, SAMSUNG_DEVICE }
 public static final class Evidence {
  public final Lane lane; public final String provider,version; public final boolean referenceTestsPassed,runtimeTestsPassed; public final String notes;
  public Evidence(Lane l,String p,String v,boolean refs,boolean runtime,String n){lane=l;provider=p;version=v;referenceTestsPassed=refs;runtimeTestsPassed=runtime;notes=n;}
  public boolean certified(){return provider!=null&&!provider.trim().isEmpty()&&referenceTestsPassed&&runtimeTestsPassed;}
 }
 private final EnumMap<Lane,Evidence> evidence=new EnumMap<>(Lane.class);
 public void record(Evidence e){if(e!=null)evidence.put(e.lane,e);}
 public Evidence evidence(Lane l){return evidence.get(l);}
 public boolean certified(Lane l){Evidence e=evidence.get(l);return e!=null&&e.certified();}
 public boolean productionReady(){for(Lane l:Lane.values())if(!certified(l))return false;return true;}
 public List<Lane> missing(){ArrayList<Lane> out=new ArrayList<>();for(Lane l:Lane.values())if(!certified(l))out.add(l);return Collections.unmodifiableList(out);}
}

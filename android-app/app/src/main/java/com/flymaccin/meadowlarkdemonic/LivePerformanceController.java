package com.flymaccin.meadowlarkdemonic;

import java.util.*;

/** Quantized Live/Looper facade that reuses project transport and canonical assets. */
public final class LivePerformanceController {
 private final DemonicProject project;
 public LivePerformanceController(DemonicProject project){if(project==null)throw new IllegalArgumentException();this.project=project;}
 public List<DemonicAddOnSuite.LiveClip> launchScene(int scene){project.addOns.live.launchScene(scene);ArrayList<DemonicAddOnSuite.LiveClip> out=new ArrayList<>();for(DemonicAddOnSuite.LiveClip c:project.addOns.live.clips)if(c.scene==scene&&project.asset(c.assetId)!=null){out.add(c);DemonicAddOnSuite.VaultAsset v=project.addOns.vault.get(c.assetId);if(v!=null)v.useCount++;}return out;}
 public long nextQuantizedFrame(long quantumFrames){long q=Math.max(1,quantumFrames),f=project.transport.frame();return ((f+q-1)/q)*q;}
 public void beginOverdub(){project.addOns.looper.overdubbing=true;}
 public void commitLoopLayer(String assetId){if(project.asset(assetId)==null)throw new IllegalArgumentException("Unknown asset "+assetId);project.addOns.looper.addLayer(assetId);project.addOns.looper.overdubbing=false;DemonicAddOnSuite.VaultAsset v=project.addOns.vault.get(assetId);if(v!=null)v.useCount++;}
 public String undoLoopLayer(){return project.addOns.looper.undoLast();}
}

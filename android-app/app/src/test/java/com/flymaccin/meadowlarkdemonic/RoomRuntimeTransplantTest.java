package com.flymaccin.meadowlarkdemonic;

import org.junit.Test;
import static org.junit.Assert.*;

public final class RoomRuntimeTransplantTest {
 @Test public void roomsShareOneRuntimeSpine(){
  DemonicProject p=new DemonicProject("Runtime transplant test");
  Asset a=p.addAsset(new Asset(Asset.Kind.AUDIO,"test://tone","Tone"));
  p.addOns.sampler.pads[0].assetId=a.id;
  p.addOns.stems.stems.add(new DemonicAddOnSuite.Stem("Tone",a.id));
  DemonicAddOnSuite.RackModule gain=new DemonicAddOnSuite.RackModule("master_gain","GAIN");
  gain.parameters.put("gain",.8f);p.addOns.mastering.chain.add(gain);
  RoomRuntimeTransplant tx=new RoomRuntimeTransplant(p);
  RoomRuntimeTransplant.Report report=tx.transplant();
  assertSame(p,p.production==null?null:new UnifiedDemonicDaw(p).project());
  assertNotNull(p.audioGraph.node("addon_sampler_pad_0"));
  assertTrue(report.graphNodes.size()>0);
  tx.runtimeTest();
 }
}

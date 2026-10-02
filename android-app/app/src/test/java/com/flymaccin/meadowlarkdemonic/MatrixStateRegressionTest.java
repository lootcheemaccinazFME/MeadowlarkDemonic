package com.flymaccin.meadowlarkdemonic;
import org.junit.Test;
import static org.junit.Assert.*;
public final class MatrixStateRegressionTest {
 @Test public void matrixServicesMutateAndClampDeterministically(){
  DemonicProject p=new DemonicProject("matrix");
  p.sequencer.steps().get(0).on=true;p.sequencer.swing(.63);
  p.midiControllers.map(0,1,"MASTER:gain");p.midiControllers.calibrate(.08,1.2);
  p.hyphy.swing(.66);p.hyphy.microtiming(.1);p.hyphy.roll(4);p.hyphy.bassSlides(false);
  p.collaboration.branch("session-a");p.collaboration.grant("editor",CollaborationStudio.Role.EDITOR);
  p.live.mode(LiveEcosystem.Mode.JAM);p.live.captureMultitrack(true);
  p.delivery.credit("Artist","Producer");p.delivery.remixParent("parent-1");
  assertTrue(p.sequencer.steps().get(0).on);assertEquals(.63,p.sequencer.swing(),.0001);
  assertEquals(1,p.midiControllers.mappings().size());assertEquals(.08,p.midiControllers.deadZone(),.0001);
  assertEquals(.66,p.hyphy.swing(),.0001);assertEquals(4,p.hyphy.roll());assertFalse(p.hyphy.bassSlides());
  assertEquals("session-a",p.collaboration.branch());assertEquals(CollaborationStudio.Role.EDITOR,p.collaboration.collaborators().get("editor"));
  assertEquals(LiveEcosystem.Mode.JAM,p.live.mode());assertTrue(p.live.captureMultitrack());
  assertEquals(1,p.delivery.credits().size());assertEquals("parent-1",p.delivery.remixParent());
 }
}
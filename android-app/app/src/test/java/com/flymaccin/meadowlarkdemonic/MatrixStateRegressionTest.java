package com.flymaccin.meadowlarkdemonic;
import org.junit.Test;
import static org.junit.Assert.*;
public final class MatrixStateRegressionTest {
 @Test public void schemaV4RoundTripPreservesMatrixState(){
  DemonicProject p=new DemonicProject("matrix");
  p.sequencer.steps().get(0).on=true;p.sequencer.swing(.63);
  p.midiControllers.map(0,1,"MASTER:gain");p.midiControllers.calibrate(.08,1.2);
  p.hyphy.swing(.66);p.hyphy.microtiming(.1);p.hyphy.roll(4);p.hyphy.bassSlides(false);
  p.collaboration.branch("session-a");p.collaboration.grant("editor",CollaborationStudio.Role.EDITOR);
  p.live.mode(LiveEcosystem.Mode.JAM);p.live.captureMultitrack(true);
  p.delivery.credit("Artist","Producer");p.delivery.remixParent("parent-1");
  DemonicProject q=DemonicProject.fromJson(p.toJson().toString());
  assertTrue(q.sequencer.steps().get(0).on);assertEquals(.63,q.sequencer.swing(),.0001);
  assertEquals(1,q.midiControllers.mappings().size());assertEquals(.08,q.midiControllers.deadZone(),.0001);
  assertEquals(.66,q.hyphy.swing(),.0001);assertEquals(4,q.hyphy.roll());assertFalse(q.hyphy.bassSlides());
  assertEquals("session-a",q.collaboration.branch());assertEquals(CollaborationStudio.Role.EDITOR,q.collaboration.collaborators().get("editor"));
  assertEquals(LiveEcosystem.Mode.JAM,q.live.mode());assertTrue(q.live.captureMultitrack());
  assertEquals(1,q.delivery.credits().size());assertEquals("parent-1",q.delivery.remixParent());
 }
}
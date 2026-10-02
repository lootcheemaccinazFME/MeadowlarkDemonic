package com.flymaccin.meadowlarkdemonic;
import org.junit.Test;
import static org.junit.Assert.*;
public final class PerformanceHardeningTest {
 @Test public void deterministicAudioRenderStaysBounded(){
  DemonicProject p=new DemonicProject("perf");
  AudioRuntime runtime=new AudioRuntime(p,(asset,start,frames,out)->{java.util.Arrays.fill(out,(short)0);return (int)frames;});
  long start=System.nanoTime();long checksum=0;
  for(int i=0;i<256;i++){short[] block=runtime.render(i*256L,256);assertEquals(512,block.length);checksum+=block[0];}
  long elapsedMs=(System.nanoTime()-start)/1_000_000L;
  assertEquals(0,checksum);
  assertTrue("256 silent render blocks exceeded CI hardening ceiling: "+elapsedMs+"ms",elapsedMs<5000);
 }
}
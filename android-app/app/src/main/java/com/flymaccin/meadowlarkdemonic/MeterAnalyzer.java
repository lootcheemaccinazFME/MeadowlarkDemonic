package com.flymaccin.meadowlarkdemonic;

/** Lightweight block meter fed from rendered stereo PCM. LUFS is an approximation until a standards adapter is installed. */
public final class MeterAnalyzer {
 private MeterAnalyzer(){}
 public static void analyze(short[] pcm,DemonicAddOnSuite.SpectrumMeterSuite suite){
  if(pcm==null||suite==null||pcm.length<2)return;
  double peak=0,sum=0,sumL=0,sumR=0,sumLL=0,sumRR=0,sumLR=0;int frames=pcm.length/2;
  for(int i=0;i<frames;i++){double l=pcm[i*2]/32768.0,r=pcm[i*2+1]/32768.0;peak=Math.max(peak,Math.max(Math.abs(l),Math.abs(r)));sum+=(l*l+r*r)*.5;sumL+=l;sumR+=r;sumLL+=l*l;sumRR+=r*r;sumLR+=l*r;}
  double rms=Math.sqrt(sum/Math.max(1,frames));double denom=Math.sqrt(sumLL*sumRR);
  DemonicAddOnSuite.MeterFrame m=suite.latest;
  m.peakDb=db(peak);m.rmsDb=db(rms);m.lufs=(float)(m.rmsDb-0.7);m.phaseCorrelation=(float)(denom==0?0:Math.max(-1,Math.min(1,sumLR/denom)));
  double side=0,mid=0;for(int i=0;i<frames;i++){double l=pcm[i*2],r=pcm[i*2+1];mid+=Math.abs(l+r);side+=Math.abs(l-r);}m.stereoWidth=(float)(mid==0?0:Math.min(2,side/mid));
  int bins=m.spectrum.length;for(int b=0;b<bins;b++){int start=b*frames/bins,end=Math.max(start+1,(b+1)*frames/bins);double e=0;for(int i=start;i<end&&i<frames;i++){double l=pcm[i*2]/32768.0,r=pcm[i*2+1]/32768.0;e+=(l*l+r*r)*.5;}m.spectrum[b]=(float)Math.sqrt(e/Math.max(1,end-start));}
 }
 private static float db(double v){return (float)(20.0*Math.log10(Math.max(1e-9,v)));}
}

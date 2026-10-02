package com.flymaccin.meadowlarkdemonic;

/** Deterministic baseline repair DSP. Advanced denoise/de-click adapters remain capability-gated. */
public final class RepairDsp {
 private RepairDsp(){}
 public static void apply(short[] pcm,DemonicAddOnSuite.RepairPlan p,int sampleRate){
  if(pcm==null||p==null||pcm.length<2)return;
  if(p.dehum)notchHum(pcm,Math.max(8000,sampleRate),Math.max(20,p.humHz));
  if(p.trimSilence)gateSilence(pcm,0.0015);
  if(p.normalize)normalize(pcm,p.normalizePeakDb);
 }
 static void normalize(short[] pcm,float targetDb){double peak=0;for(short s:pcm)peak=Math.max(peak,Math.abs((double)s));if(peak<1)return;double target=32767.0*Math.pow(10,targetDb/20.0),g=target/peak;for(int i=0;i<pcm.length;i++)pcm[i]=clip(pcm[i]*g);}
 static void gateSilence(short[] pcm,double threshold){double t=32768.0*Math.max(0,threshold);for(int i=0;i<pcm.length;i++)if(Math.abs(pcm[i])<t)pcm[i]=0;}
 static void notchHum(short[] pcm,int sampleRate,double hz){double r=.985,w=2*Math.PI*hz/sampleRate,a=-2*Math.cos(w),b=-2*r*Math.cos(w),c=r*r;for(int ch=0;ch<2;ch++){double x1=0,x2=0,y1=0,y2=0;for(int i=ch;i<pcm.length;i+=2){double x=pcm[i],y=x+a*x1+x2-b*y1-c*y2;pcm[i]=clip(y);x2=x1;x1=x;y2=y1;y1=y;}}}
 private static short clip(double v){return(short)Math.max(Short.MIN_VALUE,Math.min(Short.MAX_VALUE,Math.round(v)));}
}

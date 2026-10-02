package com.flymaccin.meadowlarkdemonic;

/** Lightweight adaptive noise-floor reducer for preview/offline cleanup. ML/spectral denoise remains adapter-gated. */
public final class NoiseReductionDsp {
 private NoiseReductionDsp(){}
 public static void apply(short[] pcm,float strength){
  if(pcm==null||pcm.length<4)return;double s=Math.max(0,Math.min(1,strength)),floor=256;
  for(int ch=0;ch<2;ch++){double env=0;for(int i=ch;i<pcm.length;i+=2){double x=pcm[i],a=Math.abs(x);env=.995*env+.005*a;if(env<1800)floor=.999*floor+.001*a;double threshold=Math.max(96,floor*(1.5+2*s));double g=a>=threshold?1.0:Math.max(.08,1.0-s*(1.0-a/threshold));pcm[i]=clip(x*g);}}}
 private static short clip(double v){return(short)Math.max(Short.MIN_VALUE,Math.min(Short.MAX_VALUE,Math.round(v)));}
}

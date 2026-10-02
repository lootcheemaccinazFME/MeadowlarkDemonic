package com.flymaccin.meadowlarkdemonic;

/** Deterministic preview-quality time/pitch processor. Production HQ remains adapter-gated. */
public final class PreviewTimePitch {
 private PreviewTimePitch(){}
 public static short[] process(short[] stereo,double stretchRatio,double semitones){
  if(stereo==null||stereo.length<2)return new short[0];
  double pitch=Math.pow(2.0,semitones/12.0),step=pitch/Math.max(.01,stretchRatio);
  int inFrames=stereo.length/2,outFrames=Math.max(1,(int)Math.round(inFrames/step));short[] out=new short[outFrames*2];
  for(int i=0;i<outFrames;i++){double src=i*step;int a=Math.min(inFrames-1,(int)src),b=Math.min(inFrames-1,a+1);double f=src-a;for(int ch=0;ch<2;ch++)out[i*2+ch]=clip(stereo[a*2+ch]*(1-f)+stereo[b*2+ch]*f);}
  return out;
 }
 private static short clip(double v){return(short)Math.max(Short.MIN_VALUE,Math.min(Short.MAX_VALUE,Math.round(v)));}
}

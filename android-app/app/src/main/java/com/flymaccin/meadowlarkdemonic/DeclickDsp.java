package com.flymaccin.meadowlarkdemonic;

/** Conservative transient click repair for isolated sample spikes. */
public final class DeclickDsp {
 private DeclickDsp(){}
 public static int apply(short[] pcm,float sensitivity){
  if(pcm==null||pcm.length<6)return 0;double threshold=12000.0*Math.max(.25,Math.min(2,sensitivity));int fixed=0;
  for(int ch=0;ch<2;ch++)for(int i=ch+2;i<pcm.length-2;i+=2){double prev=pcm[i-2],cur=pcm[i],next=pcm[i+2],expected=(prev+next)*.5;if(Math.abs(cur-expected)>threshold&&Math.abs(prev-next)<threshold*.5){pcm[i]=(short)Math.round(expected);fixed++;}}
  return fixed;
 }
}

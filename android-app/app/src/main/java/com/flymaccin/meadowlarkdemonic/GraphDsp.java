package com.flymaccin.meadowlarkdemonic;

/** Lightweight DSP kernels driven only by canonical AudioGraph node types/parameters. */
public final class GraphDsp {
    private GraphDsp(){}

    public static void process(AudioGraph.Node node,short[] stereo,int frames){
        if(node==null||stereo==null)return;
        String type=node.type;
        if(type.startsWith("FX_GAIN")){
            gain(stereo,frames,node.get("gain",1.0));
        }else if(type.startsWith("FX_CLIP")||type.startsWith("FX_SATUR")){
            saturate(stereo,frames,node.get("drive",1.0));
        }else if(type.startsWith("FX_PAN")){
            pan(stereo,frames,node.get("pan",0.0));
        }else if(type.startsWith("FX_LOWPASS")){
            lowpass(stereo,frames,node.get("amount",0.25));
        }else if(type.startsWith("FX_HIGHPASS")){
            highpass(stereo,frames,node.get("amount",0.25));
        }else if(type.startsWith("FX_DELAY")){
            delay(stereo,frames,(int)Math.round(node.get("frames",1200)),node.get("feedback",0.25),node.get("mix",0.2));
        }
    }

    public static void gain(short[] pcm,int frames,double gain){
        for(int i=0;i<frames*2&&i<pcm.length;i++)pcm[i]=clip(pcm[i]*gain);
    }

    public static void pan(short[] pcm,int frames,double pan){
        pan=Math.max(-1,Math.min(1,pan));
        double l=pan>0?1-pan:1,r=pan<0?1+pan:1;
        for(int i=0;i<frames&&i*2+1<pcm.length;i++){pcm[i*2]=clip(pcm[i*2]*l);pcm[i*2+1]=clip(pcm[i*2+1]*r);}
    }

    public static void saturate(short[] pcm,int frames,double drive){
        drive=Math.max(0.01,drive);
        double norm=Math.tanh(drive);
        for(int i=0;i<frames*2&&i<pcm.length;i++)pcm[i]=clip(Math.tanh((pcm[i]/32768.0)*drive)/norm*32767.0);
    }

    public static void lowpass(short[] pcm,int frames,double amount){
        amount=Math.max(0.001,Math.min(1,amount));double l=0,r=0;
        for(int i=0;i<frames&&i*2+1<pcm.length;i++){l+=amount*(pcm[i*2]-l);r+=amount*(pcm[i*2+1]-r);pcm[i*2]=clip(l);pcm[i*2+1]=clip(r);}
    }

    public static void highpass(short[] pcm,int frames,double amount){
        amount=Math.max(0.001,Math.min(1,amount));double lpL=0,lpR=0;
        for(int i=0;i<frames&&i*2+1<pcm.length;i++){double inL=pcm[i*2],inR=pcm[i*2+1];lpL+=amount*(inL-lpL);lpR+=amount*(inR-lpR);pcm[i*2]=clip(inL-lpL);pcm[i*2+1]=clip(inR-lpR);}
    }

    public static void delay(short[] pcm,int frames,int delayFrames,double feedback,double mix){
        delayFrames=Math.max(1,delayFrames);feedback=Math.max(0,Math.min(.95,feedback));mix=Math.max(0,Math.min(1,mix));
        for(int i=delayFrames;i<frames&&i*2+1<pcm.length;i++){int d=(i-delayFrames)*2,p=i*2;double dl=pcm[d]*feedback,dr=pcm[d+1]*feedback;pcm[p]=clip(pcm[p]*(1-mix)+dl*mix);pcm[p+1]=clip(pcm[p+1]*(1-mix)+dr*mix);}
    }

    private static short clip(double v){return (short)Math.max(Short.MIN_VALUE,Math.min(Short.MAX_VALUE,Math.round(v)));}
}

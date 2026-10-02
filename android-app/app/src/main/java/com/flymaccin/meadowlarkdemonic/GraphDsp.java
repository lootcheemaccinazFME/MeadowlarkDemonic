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

    private static short clip(double v){return (short)Math.max(Short.MIN_VALUE,Math.min(Short.MAX_VALUE,Math.round(v)));}
}

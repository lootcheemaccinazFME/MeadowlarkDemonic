package com.flymaccin.meadowlarkdemonic;

import java.util.*;

/** Pull-based canonical audio runtime. ClipEngine supplies timeline slices; AudioGraph supplies mix state. */
public final class AudioRuntime {
    public interface Decoder {
        /** Decode stereo PCM16 for one canonical asset range. Missing frames remain silent. */
        int decode(Asset asset,long sourceFrame,long frames,short[] stereoOut);
    }

    private final DemonicProject project;
    private final Decoder decoder;

    public AudioRuntime(DemonicProject project,Decoder decoder){
        if(project==null||decoder==null)throw new IllegalArgumentException();
        this.project=project;this.decoder=decoder;
    }

    public short[] render(long startFrame,int frames){
        if(frames<1)return new short[0];
        int n=frames*2;int[] mix=new int[n];
        boolean anySolo=false;
        for(Track t:project.tracks()){AudioGraph.Node ch=project.audioGraph.node(t.graphNodeId);if(ch!=null&&ch.get("solo",0.0)>=0.5){anySolo=true;break;}}
        for(ClipEngine.PlaybackSlice slice:project.clipEngine.slices(startFrame,frames)){
            if(slice.asset.kind!=Asset.Kind.AUDIO)continue;
            int count=(int)Math.min(Integer.MAX_VALUE,slice.frames);
            short[] pcm=new short[count*2];
            decoder.decode(slice.asset,slice.sourceFrame,count,pcm);
            AudioGraph.Node node=project.audioGraph.node(slice.track.graphNodeId);
            if(anySolo&&(node==null||node.get("solo",0.0)<0.5))continue;
            double gain=node==null?1.0:project.automation.valueAt(Automation.target(node.id,"gain"),startFrame,node.get("gain",1.0));
            double pan=node==null?0.0:Math.max(-1.0,Math.min(1.0,project.automation.valueAt(Automation.target(node.id,"pan"),startFrame,node.get("pan",0.0))));
            boolean mute=node!=null&&project.automation.valueAt(Automation.target(node.id,"mute"),startFrame,node.get("mute",0.0))>=0.5;
            if(mute)continue;
            for(AudioGraph.Node routed:project.audioGraph.downstream(slice.track.graphNodeId)){
                if(routed.type.startsWith("FX_"))GraphDsp.process(routed,pcm,count);
            }
            double left=gain*(pan>0?1.0-pan:1.0),right=gain*(pan<0?1.0+pan:1.0);
            int offset=(int)(slice.timelineFrame-startFrame);
            for(int i=0;i<count&&offset+i<frames;i++){
                int dst=(offset+i)*2,src=i*2;
                mix[dst]+=Math.round((float)(pcm[src]*left));
                mix[dst+1]+=Math.round((float)(pcm[src+1]*right));
            }
        }
        AudioGraph.Node master=project.audioGraph.node(AudioGraph.MASTER);
        double masterGain=master==null?1.0:project.automation.valueAt(Automation.target(master.id,"gain"),startFrame,master.get("gain",1.0));
        short[] out=new short[n];
        for(int i=0;i<n;i++){long v=Math.round(mix[i]*masterGain);out[i]=(short)Math.max(Short.MIN_VALUE,Math.min(Short.MAX_VALUE,v));}
        return out;
    }

    public int pump(AndroidAudioIO io,int frames){
        long at=project.transport.frame();
        short[] pcm=render(at,frames);
        int written=io.write(pcm,0,pcm.length);
        if(written>0)project.transport.advance(written/2);
        return written;
    }
}

package com.flymaccin.meadowlarkdemonic;

import java.util.*;

/** Compiles sampler, vocal and stem add-on state into the canonical AudioGraph. */
public final class AddOnSourceGraphCompiler {
    private AddOnSourceGraphCompiler(){}

    public static List<String> compile(DemonicProject project){
        if(project==null)throw new IllegalArgumentException();
        ArrayList<String> ids=new ArrayList<>();
        AudioGraph graph=project.audioGraph;
        DemonicAddOnSuite.Workspace w=project.addOns;

        for(DemonicAddOnSuite.Pad pad:w.sampler.pads){
            if(pad.assetId==null||pad.assetId.length()==0)continue;
            String id="addon_sampler_pad_"+pad.index;
            reset(graph,id,"SAMPLER_PAD");
            AudioGraph.Node n=graph.node(id);
            n.set("gain",pad.gain); n.set("velocity",pad.velocity); n.set("pitchSemitones",pad.pitchSemitones);
            n.set("sliceStartFrames",pad.sliceStartFrames); n.set("sliceEndFrames",pad.sliceEndFrames);
            n.set("chokeGroup",pad.chokeGroup);
            graph.connect(id,AudioGraph.MASTER); ids.add(id);
        }

        int takeIndex=0;
        for(DemonicAddOnSuite.VocalTake take:w.vocals.takes){
            if(take.assetId==null||take.assetId.length()==0)continue;
            String id="addon_vocal_take_"+takeIndex++;
            reset(graph,id,"VOCAL_TAKE");
            AudioGraph.Node n=graph.node(id);
            n.set("punchInFrame",take.punchInFrame); n.set("punchOutFrame",take.punchOutFrame);
            n.set("selected",take.selected?1:0);
            List<String> chain=AddOnGraphCompiler.compileRack(graph,w.vocals.vocalChain,id+"_fx",id,AudioGraph.MASTER);
            ids.add(id); ids.addAll(chain);
        }

        boolean anySolo=false; for(DemonicAddOnSuite.Stem s:w.stems.stems)anySolo|=s.solo;
        int stemIndex=0;
        for(DemonicAddOnSuite.Stem stem:w.stems.stems){
            if(stem.assetId==null||stem.assetId.length()==0)continue;
            String id="addon_stem_"+stemIndex++;
            reset(graph,id,"STEM_SOURCE");
            AudioGraph.Node n=graph.node(id);
            n.set("gain",stem.gain); n.set("pan",stem.pan);
            n.set("muted",stem.muted?1:0); n.set("solo",stem.solo?1:0);
            n.set("audible",(!stem.muted&&(!anySolo||stem.solo))?1:0);
            graph.connect(id,AudioGraph.MASTER); ids.add(id);
        }
        return ids;
    }

    private static void reset(AudioGraph graph,String id,String type){
        if(graph.node(id)!=null)graph.removeNode(id);
        graph.addNode(id,type);
    }
}

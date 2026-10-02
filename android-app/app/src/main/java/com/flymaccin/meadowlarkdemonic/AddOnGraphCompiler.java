package com.flymaccin.meadowlarkdemonic;

import java.util.*;

/** Compiles Demonic add-on rack state into the one canonical AudioGraph. */
public final class AddOnGraphCompiler {
    private AddOnGraphCompiler(){}

    public static List<String> compileRack(AudioGraph graph,DemonicAddOnSuite.DemonicRack rack,String prefix,String input,String output){
        if(graph==null||rack==null)throw new IllegalArgumentException();
        ArrayList<String> ids=new ArrayList<>();
        String previous=input;
        int index=0;
        for(DemonicAddOnSuite.RackModule module:rack.modules()){
            if(module==null||module.bypassed)continue;
            String id=prefix+"_"+index+"_"+safe(module.id);
            if(graph.node(id)!=null)graph.removeNode(id);
            AudioGraph.Node node=graph.addNode(id,canonicalType(module.type));
            for(Map.Entry<String,Float> p:module.parameters.entrySet())node.set(p.getKey(),p.getValue());
            if(previous!=null){graph.disconnect(previous,output);graph.connect(previous,id);}
            previous=id; ids.add(id); index++;
        }
        if(previous!=null&&output!=null)graph.connect(previous,output);
        return ids;
    }

    public static List<String> compileMastering(AudioGraph graph,DemonicAddOnSuite.MasteringRack mastering,String input){
        AudioGraph.Node master=graph.node(AudioGraph.MASTER);
        if(master!=null){master.set("targetLufs",mastering.targetLufs);master.set("ceilingDb",mastering.ceilingDb);}
        return compileRack(graph,mastering.chain,"addon_master",input,AudioGraph.MASTER);
    }

    private static String canonicalType(String type){
        String t=type==null?"GAIN":type.trim().toUpperCase(Locale.US);
        if(t.startsWith("FX_"))return t;
        if(t.contains("SATUR")||t.contains("CLIP"))return "FX_SATURATE";
        if(t.contains("PAN"))return "FX_PAN";
        if(t.contains("LOWPASS")||t.equals("LPF"))return "FX_LOWPASS";
        if(t.contains("HIGHPASS")||t.equals("HPF"))return "FX_HIGHPASS";
        if(t.contains("DELAY")||t.contains("ECHO"))return "FX_DELAY";
        return "FX_GAIN";
    }

    private static String safe(String value){
        String s=value==null?"module":value.replaceAll("[^A-Za-z0-9_]+","_");
        return s.length()==0?"module":s;
    }
}

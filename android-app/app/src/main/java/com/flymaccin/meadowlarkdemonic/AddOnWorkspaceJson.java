package com.flymaccin.meadowlarkdemonic;

import org.json.*;
import java.util.*;

/** Backward-compatible JSON codec for project-scoped Demonic add-on state. */
public final class AddOnWorkspaceJson {
 private AddOnWorkspaceJson(){}

 public static JSONObject toJson(DemonicAddOnSuite.Workspace w){
  try{
   JSONObject o=new JSONObject();
   JSONArray pads=new JSONArray();
   for(DemonicAddOnSuite.Pad p:w.sampler.pads)if(p.assetId!=null){
    JSONArray layers=new JSONArray();for(String s:p.layers)layers.put(s);
    pads.put(new JSONObject().put("index",p.index).put("assetId",p.assetId).put("velocity",p.velocity).put("gain",p.gain).put("pitch",p.pitchSemitones).put("choke",p.chokeGroup).put("sliceStart",p.sliceStartFrames).put("sliceEnd",p.sliceEndFrames).put("layers",layers));
   }
   JSONArray takes=new JSONArray();for(DemonicAddOnSuite.VocalTake t:w.vocals.takes)takes.put(new JSONObject().put("id",t.id).put("assetId",t.assetId).put("in",t.punchInFrame).put("out",t.punchOutFrame).put("selected",t.selected));
   JSONArray stems=new JSONArray();for(DemonicAddOnSuite.Stem s:w.stems.stems)stems.put(new JSONObject().put("name",s.name).put("assetId",s.assetId).put("gain",s.gain).put("pan",s.pan).put("muted",s.muted).put("solo",s.solo));
   o.put("samplerPads",pads).put("vocalTakes",takes).put("stems",stems);
   o.put("mastering",new JSONObject().put("targetLufs",w.mastering.targetLufs).put("ceilingDb",w.mastering.ceilingDb).put("referenceAssetId",w.mastering.referenceAssetId));
   o.put("timePitch",new JSONObject().put("sourceBpm",w.timePitch.sourceBpm).put("targetBpm",w.timePitch.targetBpm).put("semitones",w.timePitch.semitones));
   return o;
  }catch(Exception e){throw new IllegalStateException("Add-on serialization failed",e);}
 }

 public static void load(JSONObject o,DemonicAddOnSuite.Workspace w){
  if(o==null||w==null)return;
  JSONArray pads=o.optJSONArray("samplerPads");if(pads!=null)for(int i=0;i<pads.length();i++){JSONObject x=pads.optJSONObject(i);if(x==null)continue;int n=x.optInt("index",-1);if(n<0||n>=w.sampler.pads.length)continue;DemonicAddOnSuite.Pad p=w.sampler.pads[n];p.assetId=x.optString("assetId",null);p.velocity=(float)x.optDouble("velocity",1);p.gain=(float)x.optDouble("gain",1);p.pitchSemitones=(float)x.optDouble("pitch",0);p.chokeGroup=x.optInt("choke",0);p.sliceStartFrames=x.optLong("sliceStart",0);p.sliceEndFrames=x.optLong("sliceEnd",-1);JSONArray a=x.optJSONArray("layers");if(a!=null)for(int j=0;j<a.length();j++)p.layers.add(a.optString(j));}
  JSONArray takes=o.optJSONArray("vocalTakes");if(takes!=null)for(int i=0;i<takes.length();i++){JSONObject x=takes.optJSONObject(i);if(x==null)continue;DemonicAddOnSuite.VocalTake t=new DemonicAddOnSuite.VocalTake(x.optString("id",UUID.randomUUID().toString()),x.optString("assetId",""));t.punchInFrame=x.optLong("in",0);t.punchOutFrame=x.optLong("out",0);t.selected=x.optBoolean("selected",false);w.vocals.takes.add(t);}
  JSONArray stems=o.optJSONArray("stems");if(stems!=null)for(int i=0;i<stems.length();i++){JSONObject x=stems.optJSONObject(i);if(x==null)continue;DemonicAddOnSuite.Stem s=new DemonicAddOnSuite.Stem(x.optString("name","Stem"),x.optString("assetId",""));s.gain=(float)x.optDouble("gain",1);s.pan=(float)x.optDouble("pan",0);s.muted=x.optBoolean("muted",false);s.solo=x.optBoolean("solo",false);w.stems.stems.add(s);}
  JSONObject m=o.optJSONObject("mastering");if(m!=null){w.mastering.targetLufs=(float)m.optDouble("targetLufs",-14);w.mastering.ceilingDb=(float)m.optDouble("ceilingDb",-1);w.mastering.referenceAssetId=m.optString("referenceAssetId",null);}
  JSONObject t=o.optJSONObject("timePitch");if(t!=null){w.timePitch.sourceBpm=t.optDouble("sourceBpm",120);w.timePitch.targetBpm=t.optDouble("targetBpm",120);w.timePitch.semitones=t.optDouble("semitones",0);}
 }
}

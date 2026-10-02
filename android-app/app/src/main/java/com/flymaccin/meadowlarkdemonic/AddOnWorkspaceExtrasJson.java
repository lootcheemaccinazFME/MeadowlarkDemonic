package com.flymaccin.meadowlarkdemonic;

import org.json.*;
import java.util.*;

/** Extended JSON helpers for the Demonic workspace features that must survive relaunch. */
public final class AddOnWorkspaceExtrasJson {
 private AddOnWorkspaceExtrasJson(){}

 public static JSONObject toJson(DemonicAddOnSuite.Workspace w){
  try{
   JSONObject o=new JSONObject();
   o.put("universalRack",rack(w.universalRack)).put("vocalRack",rack(w.vocals.vocalChain)).put("masterRack",rack(w.mastering.chain));
   JSONArray curves=new JSONArray();for(DemonicAddOnSuite.AutomationCurve c:w.automation){JSONArray pts=new JSONArray();for(DemonicAddOnSuite.AutomationPoint p:c.points)pts.put(new JSONArray().put(p.beat).put(p.value));curves.put(new JSONObject().put("target",c.target).put("shape",c.shape.name()).put("points",pts));}o.put("curves",curves);
   JSONArray markers=new JSONArray();for(DemonicAddOnSuite.Marker m:w.markers.markers)markers.put(new JSONObject().put("beat",m.beat).put("type",m.type).put("label",m.label));o.put("markers",markers);
   JSONArray notes=new JSONArray();for(DemonicAddOnSuite.TimelineNote n:w.lyricsNotes.notes)notes.put(new JSONObject().put("beat",n.beat).put("section",n.section).put("text",n.text).put("takeId",n.takeId));o.put("notes",notes);
   JSONArray live=new JSONArray();for(DemonicAddOnSuite.LiveClip c:w.live.clips)live.put(new JSONObject().put("id",c.id).put("assetId",c.assetId).put("looping",c.looping).put("scene",c.scene));o.put("live",live).put("activeScene",w.live.activeScene);
   JSONArray layers=new JSONArray();for(String s:w.looper.layers)layers.put(s);o.put("looper",new JSONObject().put("overdubbing",w.looper.overdubbing).put("layers",layers));
   JSONArray vault=new JSONArray();for(DemonicAddOnSuite.VaultAsset a:w.vault.all()){JSONArray tags=new JSONArray();for(String t:a.tags)tags.put(t);vault.put(new JSONObject().put("id",a.id).put("type",a.type).put("uri",a.uri).put("bpm",a.bpm).put("key",a.key).put("source",a.source).put("projectId",a.projectId).put("tags",tags).put("useCount",a.useCount).put("favorite",a.favorite));}o.put("vault",vault);
   JSONArray warp=new JSONArray();for(DemonicAddOnSuite.WarpMarker m:w.timePitch.markers)warp.put(new JSONObject().put("sourceFrame",m.sourceFrame).put("beat",m.beat));o.put("warp",warp);
   o.put("repair",new JSONObject().put("denoise",w.repair.plan.denoise).put("declick",w.repair.plan.declick).put("dehum",w.repair.plan.dehum).put("trimSilence",w.repair.plan.trimSilence).put("normalize",w.repair.plan.normalize).put("humHz",w.repair.plan.humHz).put("normalizePeakDb",w.repair.plan.normalizePeakDb));
   JSONArray sync=new JSONArray();for(DemonicAddOnSuite.SyncMarker m:w.videoSync.markers)sync.put(new JSONObject().put("audioFrame",m.audioFrame).put("videoMs",m.videoMs).put("label",m.label));o.put("videoSync",sync);
   JSONArray scenes=new JSONArray();for(DemonicAddOnSuite.DirectorScene s:w.director.scenes)scenes.put(new JSONObject().put("id",s.id).put("shotOrder",s.shotOrder).put("mediaInstructions",s.mediaInstructions).put("musicCue",s.musicCue).put("renderPlan",s.renderPlan));o.put("scenes",scenes);
   return o;
  }catch(Exception e){throw new IllegalStateException("Extended add-on serialization failed",e);}
 }

 public static void load(JSONObject o,DemonicAddOnSuite.Workspace w){
  if(o==null)return;
  loadRack(o.optJSONArray("universalRack"),w.universalRack);loadRack(o.optJSONArray("vocalRack"),w.vocals.vocalChain);loadRack(o.optJSONArray("masterRack"),w.mastering.chain);
  JSONArray curves=o.optJSONArray("curves");if(curves!=null)for(int i=0;i<curves.length();i++){JSONObject x=curves.optJSONObject(i);if(x==null)continue;DemonicAddOnSuite.AutomationCurve c=new DemonicAddOnSuite.AutomationCurve();c.target=x.optString("target",null);try{c.shape=DemonicAddOnSuite.CurveShape.valueOf(x.optString("shape","LINEAR"));}catch(Exception ignored){}JSONArray pts=x.optJSONArray("points");if(pts!=null)for(int j=0;j<pts.length();j++){JSONArray p=pts.optJSONArray(j);if(p!=null)c.points.add(new DemonicAddOnSuite.AutomationPoint(p.optDouble(0), (float)p.optDouble(1)));}w.automation.add(c);}
  JSONArray markers=o.optJSONArray("markers");if(markers!=null)for(int i=0;i<markers.length();i++){JSONObject x=markers.optJSONObject(i);if(x!=null)w.markers.markers.add(new DemonicAddOnSuite.Marker(x.optDouble("beat"),x.optString("type","MARKER"),x.optString("label","")));}
  JSONArray notes=o.optJSONArray("notes");if(notes!=null)for(int i=0;i<notes.length();i++){JSONObject x=notes.optJSONObject(i);if(x!=null){DemonicAddOnSuite.TimelineNote n=new DemonicAddOnSuite.TimelineNote(x.optDouble("beat"),x.optString("section",""),x.optString("text",""));n.takeId=x.optString("takeId",null);w.lyricsNotes.notes.add(n);}}
  JSONArray live=o.optJSONArray("live");if(live!=null)for(int i=0;i<live.length();i++){JSONObject x=live.optJSONObject(i);if(x!=null){DemonicAddOnSuite.LiveClip c=new DemonicAddOnSuite.LiveClip(x.optString("id",UUID.randomUUID().toString()),x.optString("assetId",""));c.looping=x.optBoolean("looping",true);c.scene=x.optInt("scene",0);w.live.clips.add(c);}}w.live.activeScene=o.optInt("activeScene",-1);
  JSONObject loop=o.optJSONObject("looper");if(loop!=null){w.looper.overdubbing=loop.optBoolean("overdubbing",false);JSONArray a=loop.optJSONArray("layers");if(a!=null)for(int i=0;i<a.length();i++)w.looper.layers.add(a.optString(i));}
  JSONArray vault=o.optJSONArray("vault");if(vault!=null)for(int i=0;i<vault.length();i++){JSONObject x=vault.optJSONObject(i);if(x==null)continue;DemonicAddOnSuite.VaultAsset a=new DemonicAddOnSuite.VaultAsset(x.optString("id"),x.optString("type"),x.optString("uri"));if(!x.isNull("bpm"))a.bpm=x.optDouble("bpm");a.key=x.optString("key",null);a.source=x.optString("source",null);a.projectId=x.optString("projectId",null);a.useCount=x.optInt("useCount");a.favorite=x.optBoolean("favorite");JSONArray tags=x.optJSONArray("tags");if(tags!=null)for(int j=0;j<tags.length();j++)a.tags.add(tags.optString(j));w.vault.put(a);}
  JSONArray warp=o.optJSONArray("warp");if(warp!=null)for(int i=0;i<warp.length();i++){JSONObject x=warp.optJSONObject(i);if(x!=null)w.timePitch.markers.add(new DemonicAddOnSuite.WarpMarker(x.optLong("sourceFrame"),x.optDouble("beat")));}
  JSONObject r=o.optJSONObject("repair");if(r!=null){DemonicAddOnSuite.RepairPlan p=w.repair.plan;p.denoise=r.optBoolean("denoise");p.declick=r.optBoolean("declick");p.dehum=r.optBoolean("dehum");p.trimSilence=r.optBoolean("trimSilence");p.normalize=r.optBoolean("normalize");p.humHz=(float)r.optDouble("humHz",60);p.normalizePeakDb=(float)r.optDouble("normalizePeakDb",-1);}
  JSONArray sync=o.optJSONArray("videoSync");if(sync!=null)for(int i=0;i<sync.length();i++){JSONObject x=sync.optJSONObject(i);if(x!=null)w.videoSync.markers.add(new DemonicAddOnSuite.SyncMarker(x.optLong("audioFrame"),x.optLong("videoMs"),x.optString("label","")));}
  JSONArray scenes=o.optJSONArray("scenes");if(scenes!=null)for(int i=0;i<scenes.length();i++){JSONObject x=scenes.optJSONObject(i);if(x!=null){DemonicAddOnSuite.DirectorScene s=new DemonicAddOnSuite.DirectorScene(x.optString("id",UUID.randomUUID().toString()));s.shotOrder=x.optString("shotOrder","");s.mediaInstructions=x.optString("mediaInstructions","");s.musicCue=x.optString("musicCue","");s.renderPlan=x.optString("renderPlan","");w.director.scenes.add(s);}}
 }

 private static JSONArray rack(DemonicAddOnSuite.DemonicRack r)throws JSONException{JSONArray a=new JSONArray();for(DemonicAddOnSuite.RackModule m:r.modules()){JSONObject p=new JSONObject();for(Map.Entry<String,Float> e:m.parameters.entrySet())p.put(e.getKey(),e.getValue());a.put(new JSONObject().put("id",m.id).put("type",m.type).put("bypassed",m.bypassed).put("parameters",p));}return a;}
 private static void loadRack(JSONArray a,DemonicAddOnSuite.DemonicRack r){if(a==null)return;for(int i=0;i<a.length();i++){JSONObject x=a.optJSONObject(i);if(x==null)continue;DemonicAddOnSuite.RackModule m=new DemonicAddOnSuite.RackModule(x.optString("id",UUID.randomUUID().toString()),x.optString("type","GAIN"));m.bypassed=x.optBoolean("bypassed");JSONObject p=x.optJSONObject("parameters");if(p!=null){Iterator<String> it=p.keys();while(it.hasNext()){String k=it.next();m.parameters.put(k,(float)p.optDouble(k));}}r.add(m);}}
}

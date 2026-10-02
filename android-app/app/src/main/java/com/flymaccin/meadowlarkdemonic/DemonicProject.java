package com.flymaccin.meadowlarkdemonic;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.UUID;

/** Single canonical project authority. All subsystems reference IDs owned here. */
public final class DemonicProject {
 public static final int SCHEMA_VERSION=1;
 public final String id;
 public String name;
 public final Transport transport;
 public final AudioGraph audioGraph;
 public final JSONArray assets=new JSONArray();
 public final JSONArray tracks=new JSONArray();
 public final JSONArray clips=new JSONArray();
 public final UndoHistory history=new UndoHistory();

 public DemonicProject(String name){
  this(UUID.randomUUID().toString(),name,new Transport(),new AudioGraph());
 }
 private DemonicProject(String id,String name,Transport transport,AudioGraph graph){
  this.id=id; this.name=name; this.transport=transport; this.audioGraph=graph;
 }
 public JSONObject toJson(){
  try{
   return new JSONObject().put("schemaVersion",SCHEMA_VERSION).put("id",id).put("name",name)
    .put("transport",transport.toJson()).put("audioGraph",audioGraph.toJson())
    .put("assets",assets).put("tracks",tracks).put("clips",clips);
  }catch(Exception e){throw new IllegalStateException("Project serialization failed",e);}
 }
 public static DemonicProject fromJson(String raw){
  try{
   JSONObject o=new JSONObject(raw);
   DemonicProject p=new DemonicProject(o.getString("id"),o.optString("name","Demonic Project"),
    Transport.fromJson(o.optJSONObject("transport")),AudioGraph.fromJson(o.optJSONObject("audioGraph")));
   copy(o.optJSONArray("assets"),p.assets); copy(o.optJSONArray("tracks"),p.tracks); copy(o.optJSONArray("clips"),p.clips);
   return p;
  }catch(Exception e){throw new IllegalArgumentException("Invalid Demonic project",e);}
 }
 private static void copy(JSONArray src,JSONArray dst)throws Exception{
  if(src!=null)for(int i=0;i<src.length();i++)dst.put(src.get(i));
 }
}
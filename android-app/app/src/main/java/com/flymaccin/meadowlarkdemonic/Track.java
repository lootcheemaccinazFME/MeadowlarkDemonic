package com.flymaccin.meadowlarkdemonic;
import org.json.JSONObject;
import java.util.UUID;
public final class Track{
 public enum Kind{AUDIO,MIDI,VIDEO,IMAGE}
 public final String id;public final Kind kind;public String name;public final String graphNodeId;
 public Track(Kind kind,String name){this(UUID.randomUUID().toString(),kind,name,null);}
 private Track(String id,Kind kind,String name,String node){this.id=id;this.kind=kind;this.name=name;this.graphNodeId=node==null?"track:"+id:node;}
 public JSONObject toJson(){try{return new JSONObject().put("id",id).put("kind",kind.name()).put("name",name).put("graphNodeId",graphNodeId);}catch(Exception e){throw new IllegalStateException(e);}}
 public static Track fromJson(JSONObject o){return new Track(o.getString("id"),Kind.valueOf(o.optString("kind","AUDIO")),o.optString("name","Track"),o.optString("graphNodeId",null));}
}
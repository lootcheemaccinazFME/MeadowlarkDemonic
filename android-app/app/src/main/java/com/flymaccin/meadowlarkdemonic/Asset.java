package com.flymaccin.meadowlarkdemonic;
import org.json.JSONObject;
import java.util.UUID;
public final class Asset{
 public enum Kind{AUDIO,MIDI,IMAGE,VIDEO}
 public final String id;public final Kind kind;public final String uri;public final String name;
 public Asset(Kind kind,String uri,String name){this(UUID.randomUUID().toString(),kind,uri,name);}
 private Asset(String id,Kind kind,String uri,String name){this.id=id;this.kind=kind;this.uri=uri;this.name=name;}
 public JSONObject toJson(){try{return new JSONObject().put("id",id).put("kind",kind.name()).put("uri",uri).put("name",name);}catch(Exception e){throw new IllegalStateException(e);}}
 public static Asset fromJson(JSONObject o){return new Asset(o.optString("id",UUID.randomUUID().toString()),Kind.valueOf(o.optString("kind","AUDIO")),o.optString("uri",""),o.optString("name","Asset"));}
}
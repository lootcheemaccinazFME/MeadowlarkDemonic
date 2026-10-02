package com.flymaccin.meadowlarkdemonic;
import org.json.JSONObject;
import java.util.UUID;
public final class Clip{
 public final String id,trackId,assetId;public long startFrame,lengthFrames,sourceOffsetFrames;
 public Clip(String trackId,String assetId,long start,long length){this(UUID.randomUUID().toString(),trackId,assetId,start,length,0);}
 private Clip(String id,String trackId,String assetId,long start,long length,long offset){if(start<0||length<1||offset<0)throw new IllegalArgumentException("Invalid clip range");this.id=id;this.trackId=trackId;this.assetId=assetId;startFrame=start;lengthFrames=length;sourceOffsetFrames=offset;}
 public long endFrame(){return startFrame+lengthFrames;}
 public JSONObject toJson(){try{return new JSONObject().put("id",id).put("trackId",trackId).put("assetId",assetId).put("startFrame",startFrame).put("lengthFrames",lengthFrames).put("sourceOffsetFrames",sourceOffsetFrames);}catch(Exception e){throw new IllegalStateException(e);}}
 public static Clip fromJson(JSONObject o){return new Clip(o.getString("id"),o.getString("trackId"),o.getString("assetId"),o.optLong("startFrame",0),o.optLong("lengthFrames",1),o.optLong("sourceOffsetFrames",0));}
}
package com.flymaccin.meadowlarkdemonic;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

public final class DemonicProject {
 public static final int SCHEMA_VERSION=2;
 public final String id; public String name;
 public final Transport transport; public final AudioGraph audioGraph; public final UndoHistory history=new UndoHistory();
 private final ArrayList<Asset> assets=new ArrayList<>();
 private final ArrayList<Track> tracks=new ArrayList<>();
 private final ArrayList<Clip> clips=new ArrayList<>();
 public final Timeline timeline; public final ClipEngine clipEngine;

 public DemonicProject(String name){this(UUID.randomUUID().toString(),name,new Transport(),new AudioGraph());}
 private DemonicProject(String id,String name,Transport transport,AudioGraph graph){this.id=id;this.name=name;this.transport=transport;this.audioGraph=graph;this.timeline=new Timeline(this);this.clipEngine=new ClipEngine(this);}
 public List<Asset> assets(){return Collections.unmodifiableList(assets);}
 public List<Track> tracks(){return Collections.unmodifiableList(tracks);}
 public List<Clip> clips(){return Collections.unmodifiableList(clips);}
 public Asset addAsset(Asset a){assets.add(a);return a;}
 public Track addTrack(Track t){tracks.add(t);if(t.kind==Track.Kind.AUDIO||t.kind==Track.Kind.MIDI){audioGraph.addNode(t.graphNodeId,"TRACK_"+t.kind.name());audioGraph.connect(t.graphNodeId,AudioGraph.MASTER);}return t;}
 public Clip addClip(Clip c){requireTrack(c.trackId);requireAsset(c.assetId);clips.add(c);return c;}
 public Track track(String id){for(Track t:tracks)if(t.id.equals(id))return t;return null;}\n public Asset asset(String id){for(Asset a:assets)if(a.id.equals(id))return a;return null;}\n private void requireTrack(String id){for(Track t:tracks)if(t.id.equals(id))return;throw new IllegalArgumentException("Unknown track "+id);}
 private void requireAsset(String id){for(Asset a:assets)if(a.id.equals(id))return;throw new IllegalArgumentException("Unknown asset "+id);}

 public JSONObject toJson(){try{JSONArray aa=new JSONArray(),tt=new JSONArray(),cc=new JSONArray();for(Asset a:assets)aa.put(a.toJson());for(Track t:tracks)tt.put(t.toJson());for(Clip c:clips)cc.put(c.toJson());return new JSONObject().put("schemaVersion",SCHEMA_VERSION).put("id",id).put("name",name).put("transport",transport.toJson()).put("audioGraph",audioGraph.toJson()).put("assets",aa).put("tracks",tt).put("clips",cc);}catch(Exception e){throw new IllegalStateException("Project serialization failed",e);}}
 public static DemonicProject fromJson(String raw){try{JSONObject o=new JSONObject(raw);DemonicProject p=new DemonicProject(o.getString("id"),o.optString("name","Demonic Project"),Transport.fromJson(o.optJSONObject("transport")),AudioGraph.fromJson(o.optJSONObject("audioGraph")));JSONArray a=o.optJSONArray("assets");if(a!=null)for(int i=0;i<a.length();i++)p.assets.add(Asset.fromJson(a.getJSONObject(i)));JSONArray t=o.optJSONArray("tracks");if(t!=null)for(int i=0;i<t.length();i++)p.tracks.add(Track.fromJson(t.getJSONObject(i)));JSONArray c=o.optJSONArray("clips");if(c!=null)for(int i=0;i<c.length();i++)p.clips.add(Clip.fromJson(c.getJSONObject(i)));return p;}catch(Exception e){throw new IllegalArgumentException("Invalid Demonic project",e);}}
}
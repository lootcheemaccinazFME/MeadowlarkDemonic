package com.flymaccin.meadowlarkdemonic;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

public final class DemonicProject {
 public static final int SCHEMA_VERSION=4;
 public final String id; public String name;
 public final Transport transport; public final AudioGraph audioGraph; public final UndoHistory history=new UndoHistory();
 private final ArrayList<Asset> assets=new ArrayList<>();
 private final ArrayList<Track> tracks=new ArrayList<>();
 private final ArrayList<Clip> clips=new ArrayList<>();
 public final DemonicAddOnSuite.Workspace addOns; public final Timeline timeline; public final Grid grid; public final Metronome metronome; public final ClipEngine clipEngine; public final Mixer mixer; public final InstrumentRack instruments; public final RecordingEngine recording; public final Arrangement arrangement; public final Automation automation; public final MaestroBridge maestro; public final AiAssetIngestion ai; public final Director director; public final ProductionSession production; public final MediaEditor mediaEditor; public final DemonicBrowser browser; public final DemonicDownloader downloader; public final DemonicTv tv; public final SequencerTracker sequencer; public final MidiControllerHub midiControllers; public final HyphyEngine hyphy; public final CollaborationStudio collaboration; public final LiveEcosystem live; public final PublishingDelivery delivery;

 public DemonicProject(String name){this(UUID.randomUUID().toString(),name,new Transport(),new AudioGraph());}
 private DemonicProject(String id,String name,Transport transport,AudioGraph graph){this.id=id;this.name=name;this.transport=transport;this.audioGraph=graph;this.addOns=new DemonicAddOnSuite.Workspace();this.timeline=new Timeline(this);this.grid=new Grid(this);this.metronome=new Metronome(this);this.clipEngine=new ClipEngine(this);this.mixer=new Mixer(this);this.instruments=new InstrumentRack(this);this.recording=new RecordingEngine(this);this.arrangement=new Arrangement(this);this.automation=new Automation(this);this.maestro=new MaestroBridge(this);this.ai=new AiAssetIngestion(this);this.director=new Director(this);this.production=new ProductionSession(this);this.mediaEditor=new MediaEditor(this);this.browser=new DemonicBrowser(this);this.downloader=new DemonicDownloader(this);this.tv=new DemonicTv(this);this.sequencer=new SequencerTracker();this.midiControllers=new MidiControllerHub();this.hyphy=new HyphyEngine();this.collaboration=new CollaborationStudio();this.live=new LiveEcosystem();this.delivery=new PublishingDelivery();}
 public List<Asset> assets(){return Collections.unmodifiableList(assets);}
 public List<Track> tracks(){return Collections.unmodifiableList(tracks);}
 public List<Clip> clips(){return Collections.unmodifiableList(clips);}
 public Asset addAsset(Asset a){assets.add(a);VaultIngestion.ingest(this,a,"project");return a;}
 public Track addTrack(Track t){tracks.add(t);if(t.kind==Track.Kind.AUDIO||t.kind==Track.Kind.MIDI){audioGraph.addNode(t.graphNodeId,"TRACK_"+t.kind.name());audioGraph.connect(t.graphNodeId,AudioGraph.MASTER);}return t;}
 public Clip addClip(Clip c){requireTrack(c.trackId);requireAsset(c.assetId);clips.add(c);return c;}
 void removeClipInternal(Clip c){clips.remove(c);}
 void removeAssetInternal(Asset a){assets.remove(a);}
 void restoreAssetInternal(Asset a){if(!assets.contains(a)){assets.add(a);VaultIngestion.ingest(this,a,"project");}}
 void removeTrackInternal(Track t){tracks.remove(t);if(t.kind==Track.Kind.AUDIO||t.kind==Track.Kind.MIDI)audioGraph.removeNode(t.graphNodeId);}
 void restoreTrackInternal(Track t){if(!tracks.contains(t)){tracks.add(t);if(t.kind==Track.Kind.AUDIO||t.kind==Track.Kind.MIDI){audioGraph.addNode(t.graphNodeId,"TRACK_"+t.kind.name());audioGraph.connect(t.graphNodeId,AudioGraph.MASTER);}}}
 public Asset addAssetUndoable(final Asset a){history.execute(new UndoHistory.Command(){public void apply(){restoreAssetInternal(a);}public void revert(){removeAssetInternal(a);}public String label(){return "Add asset";}});return a;}
 public Track addTrackUndoable(final Track t){history.execute(new UndoHistory.Command(){public void apply(){restoreTrackInternal(t);}public void revert(){removeTrackInternal(t);}public String label(){return "Add track";}});return t;}
 public Clip addClipUndoable(final Clip c){requireTrack(c.trackId);requireAsset(c.assetId);history.execute(new UndoHistory.Command(){public void apply(){restoreClipInternal(c);}public void revert(){removeClipInternal(c);}public String label(){return "Add clip";}});return c;}
 void restoreClipInternal(Clip c){requireTrack(c.trackId);requireAsset(c.assetId);if(!clips.contains(c))clips.add(c);}
 public Track track(String id){for(Track t:tracks)if(t.id.equals(id))return t;return null;}
 public Asset asset(String id){for(Asset a:assets)if(a.id.equals(id))return a;return null;}
 private void requireTrack(String id){for(Track t:tracks)if(t.id.equals(id))return;throw new IllegalArgumentException("Unknown track "+id);}
 private void requireAsset(String id){for(Asset a:assets)if(a.id.equals(id))return;throw new IllegalArgumentException("Unknown asset "+id);}

 public JSONObject toJson(){try{JSONArray aa=new JSONArray(),tt=new JSONArray(),cc=new JSONArray();for(Asset a:assets)aa.put(a.toJson());for(Track t:tracks)tt.put(t.toJson());for(Clip c:clips)cc.put(c.toJson());return new JSONObject().put("schemaVersion",SCHEMA_VERSION).put("id",id).put("name",name).put("transport",transport.toJson()).put("audioGraph",audioGraph.toJson()).put("assets",aa).put("tracks",tt).put("clips",cc).put("automation",automation.toJson()).put("mediaTransforms",mediaEditor.toJson()).put("addOns",AddOnWorkspaceJson.toJson(addOns)).put("tv",tv.toJson()).put("sequencer",sequencer.toJson()).put("midiControllers",midiControllers.toJson()).put("hyphy",hyphy.toJson()).put("collaboration",collaboration.toJson()).put("live",live.toJson()).put("delivery",delivery.toJson());}catch(Exception e){throw new IllegalStateException("Project serialization failed",e);}}
 public static DemonicProject fromJson(String raw){try{JSONObject o=new JSONObject(raw);DemonicProject p=new DemonicProject(o.getString("id"),o.optString("name","Demonic Project"),Transport.fromJson(o.optJSONObject("transport")),AudioGraph.fromJson(o.optJSONObject("audioGraph")));JSONArray a=o.optJSONArray("assets");if(a!=null)for(int i=0;i<a.length();i++)p.assets.add(Asset.fromJson(a.getJSONObject(i)));JSONArray t=o.optJSONArray("tracks");if(t!=null)for(int i=0;i<t.length();i++)p.tracks.add(Track.fromJson(t.getJSONObject(i)));JSONArray c=o.optJSONArray("clips");if(c!=null)for(int i=0;i<c.length();i++)p.clips.add(Clip.fromJson(c.getJSONObject(i)));p.automation.loadJson(o.optJSONArray("automation"));p.mediaEditor.loadJson(o.optJSONObject("mediaTransforms"));AddOnWorkspaceJson.load(o.optJSONObject("addOns"),p.addOns);p.tv.loadJson(o.optJSONObject("tv"));p.sequencer.loadJson(o.optJSONObject("sequencer"));p.midiControllers.loadJson(o.optJSONObject("midiControllers"));p.hyphy.loadJson(o.optJSONObject("hyphy"));p.collaboration.loadJson(o.optJSONObject("collaboration"));p.live.loadJson(o.optJSONObject("live"));p.delivery.loadJson(o.optJSONObject("delivery"));VaultIngestion.syncProjectAssets(p);return p;}catch(Exception e){throw new IllegalArgumentException("Invalid Demonic project",e);}}
}
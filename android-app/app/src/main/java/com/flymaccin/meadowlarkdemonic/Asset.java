package com.flymaccin.meadowlarkdemonic;
import org.json.*; import java.util.*;
public final class Asset{
 public enum Kind{AUDIO,MIDI,IMAGE,VIDEO}
 public final String id;public final Kind kind;public final String uri;public final String name;private final ArrayList<MidiEvent> midi=new ArrayList<>();
 public Asset(Kind kind,String uri,String name){this(UUID.randomUUID().toString(),kind,uri,name);}
 private Asset(String id,Kind kind,String uri,String name){this.id=id;this.kind=kind;this.uri=uri;this.name=name;}
 public Asset addMidiEvent(MidiEvent e){if(kind!=Kind.MIDI)throw new IllegalStateException("Not MIDI");midi.add(e);return this;}
 public List<MidiEvent> midiEvents(){return Collections.unmodifiableList(midi);}
 public JSONObject toJson(){try{JSONObject o=new JSONObject().put("id",id).put("kind",kind.name()).put("uri",uri).put("name",name);if(kind==Kind.MIDI){JSONArray a=new JSONArray();for(MidiEvent e:midi)a.put(e.toJson());o.put("midiEvents",a);}return o;}catch(Exception e){throw new IllegalStateException(e);}}
 public static Asset fromJson(JSONObject o){Asset x=new Asset(o.optString("id",UUID.randomUUID().toString()),Kind.valueOf(o.optString("kind","AUDIO")),o.optString("uri",""),o.optString("name","Asset"));JSONArray a=o.optJSONArray("midiEvents");if(a!=null)for(int i=0;i<a.length();i++)x.midi.add(MidiEvent.fromJson(a.optJSONObject(i)));return x;}
}
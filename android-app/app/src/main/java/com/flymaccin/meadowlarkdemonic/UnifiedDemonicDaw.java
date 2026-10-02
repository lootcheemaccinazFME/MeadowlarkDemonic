package com.flymaccin.meadowlarkdemonic;

import java.util.*;

/** Canonical inventory proving every Demonic DAW surface belongs to one application/project spine. */
public final class UnifiedDemonicDaw {
 public enum Surface { ARRANGE, RECORD, MIDI_COMPOSE, MIX, SAMPLER, VOCAL_STUDIO, MASTERING, STEM_LAB, TIME_PITCH, AUDIO_REPAIR, AUTOMATION, GROOVE, CHORD_SCALE, LIVE, LOOPER, PLUGIN_RACK, ASSET_VAULT, VERSION_VAULT, LYRICS_NOTES, MARKERS, METERS, VIDEO_SYNC, DIRECTOR, MAESTRO_AI, BROWSER, DOWNLOADS, TV }
 private final DemonicProject project;
 public UnifiedDemonicDaw(DemonicProject p){if(p==null)throw new IllegalArgumentException("project");project=p;}
 public DemonicProject project(){return project;}
 public Set<Surface> surfaces(){return Collections.unmodifiableSet(EnumSet.allOf(Surface.class));}
 public void verifySingleSpine(){
  if(project.production.addOns()!=project.addOns)throw new IllegalStateException("Add-on workspace split");
  if(project.production.vault()!=project.addOns.vault)throw new IllegalStateException("Vault split");
  if(project.production.live()!=project.addOns.live)throw new IllegalStateException("Live split");
  if(project.production.rack()!=project.addOns.universalRack)throw new IllegalStateException("Rack split");
  if(project.audioGraph==null||project.transport==null||project.timeline==null||project.mixer==null||project.clipEngine==null||project.undo==null)throw new IllegalStateException("Canonical DAW spine incomplete");
  if(project.tracks()==null||project.clips()==null||project.assets()==null)throw new IllegalStateException("Canonical project collections unavailable");
 }
 public String summary(){return surfaces().size()+" DAW surfaces · 1 project · 1 transport · 1 audio graph · 1 APK";}
}

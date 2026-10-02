package com.flymaccin.meadowlarkdemonic;

import android.app.*;
import android.os.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity{
 int gold=Color.rgb(214,179,90),panel=Color.rgb(24,24,31),white=Color.rgb(240,240,244),muted=Color.rgb(145,145,155),green=Color.rgb(80,200,130);
 DemonicProject project; String workspace="HOME";

 public void onCreate(Bundle b){
  super.onCreate(b);getWindow().setStatusBarColor(Color.BLACK);
  project=ProjectStore.loadOrCreate(this);
  showStudio();
 }
 TextView t(String s,int z,int c){TextView v=new TextView(this);v.setText(s);v.setTextSize(z);v.setTextColor(c);v.setPadding(18,12,18,12);return v;}
 Button button(String s){Button x=new Button(this);x.setText(s);x.setTextColor(white);x.setBackgroundColor(panel);return x;}
 void persist(){ProjectStore.save(this,project);}

 void showStudio(){
  ScrollView sc=new ScrollView(this);LinearLayout p=new LinearLayout(this);p.setOrientation(LinearLayout.VERTICAL);p.setPadding(24,18,24,18);p.setBackgroundColor(Color.rgb(9,9,12));
  p.addView(t("DEMONIC",30,gold));
  p.addView(t("ONE PROJECT · ONE TRANSPORT · ONE AUDIO GRAPH",12,green));
  TextView status=t(statusText(),13,white);p.addView(status);

  LinearLayout transport=new LinearLayout(this);transport.setOrientation(LinearLayout.HORIZONTAL);
  Button play=button("PLAY");play.setOnClickListener(v->{project.transport.play();persist();status.setText(statusText());});transport.addView(play);
  Button stop=button("STOP");stop.setOnClickListener(v->{project.transport.stop();persist();status.setText(statusText());});transport.addView(stop);
  Button rec=button("REC");rec.setOnClickListener(v->{project.transport.record();persist();status.setText(statusText());});transport.addView(rec);
  p.addView(transport);

  p.addView(t("UNIFIED WORKSPACES",14,gold));
  LinearLayout nav=new LinearLayout(this);nav.setOrientation(LinearLayout.VERTICAL);
  for(String s:new String[]{"HOME","ARRANGE","RECORD","COMPOSE / MIDI","MIX","MAESTRO / AI","DIRECTOR","IMAGE / VIDEO","BROWSER","DOWNLOADS","TV"}){
   Button x=button(s);x.setOnClickListener(v->{workspace=s;showStudio();});nav.addView(x);
  }
  p.addView(nav);
  p.addView(t("WORKSPACE · "+workspace,18,gold));
  p.addView(t(workspaceText(),13,white));

  p.addView(t("CORE AUTHORITY",14,gold));
  p.addView(t("Project: "+project.name+"\nTracks: "+project.tracks().size()+" · Clips: "+project.clips().size()+" · Assets: "+project.assets().size()+"\nGraph nodes: "+project.audioGraph.nodes().size()+" · Graph routes: "+project.audioGraph.edges().size()+"\nUndo: "+(project.history.canUndo()?"ready":"empty")+" · Redo: "+(project.history.canRedo()?"ready":"empty"),12,muted));
  p.addView(t("External engines may donate/import capabilities and assets. They do not own Project, Transport, Timeline, Audio Graph, Track, Clip, Asset, Mixer, or Undo state.",12,muted));
  sc.addView(p);setContentView(sc);
 }
 String workspaceText(){
  if("HOME".equals(workspace))return "Project "+project.name+" · "+project.tracks().size()+" tracks · "+project.clips().size()+" clips · "+project.assets().size()+" assets";
  if("ARRANGE".equals(workspace))return "Timeline clips "+project.clips().size()+" · Undo "+project.history.undoDepth()+" · Redo "+project.history.redoDepth();
  if("RECORD".equals(workspace))return "Recording shares Transport, AudioGraph and canonical project assets.";
  if("COMPOSE / MIDI".equals(workspace))return "MIDI tracks use the canonical Clip Engine, Piano Roll and Instrument Rack.";
  if("MIX".equals(workspace))return "Graph nodes "+project.audioGraph.nodes().size()+" · routes "+project.audioGraph.edges().size()+" · canonical Mixer/DSP.";
  if("MAESTRO / AI".equals(workspace)||"DIRECTOR".equals(workspace))return project.production.summary();
  if("IMAGE / VIDEO".equals(workspace))return "Media clips "+project.clips().size()+" · transforms share the project timeline.";
  if("BROWSER".equals(workspace))return "Browser discovery imports into canonical Assets.";
  if("DOWNLOADS".equals(workspace))return "Download jobs "+project.downloader.jobs().size()+" · completed media becomes canonical Assets.";
  if("TV".equals(workspace))return "TV "+project.tv.state()+" · surface "+project.tv.surface()+" · shared canonical media session.";
  return workspace;
 }
 String statusText(){return "Transport: "+project.transport.state()+" · Frame "+project.transport.frame()+" · "+project.transport.bpm()+" BPM · "+project.transport.sampleRate()+" Hz";}
}

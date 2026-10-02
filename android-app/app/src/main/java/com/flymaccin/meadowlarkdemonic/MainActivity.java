package com.flymaccin.meadowlarkdemonic;

import android.app.*;
import android.os.*;
import android.content.*;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.database.Cursor;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity{
 int gold=Color.rgb(235,187,67),panel=Color.rgb(10,18,26),white=Color.rgb(240,240,244),muted=Color.rgb(145,145,155),green=Color.rgb(34,221,108),red=Color.rgb(235,25,38),deepRed=Color.rgb(92,8,12),black=Color.rgb(4,7,10);
 DemonicProject project; String workspace="HOME";
 static final int PICK_TV_MEDIA=4401;

 public void onCreate(Bundle b){
  super.onCreate(b);getWindow().setStatusBarColor(Color.BLACK);
  project=ProjectStore.loadOrCreate(this);
  showStudio();
 }
 @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){
  super.onActivityResult(requestCode,resultCode,data);
  if(requestCode!=PICK_TV_MEDIA||resultCode!=RESULT_OK||data==null||data.getData()==null)return;
  Uri uri=data.getData();
  try{getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception ignored){}
  String mime=getContentResolver().getType(uri); Asset.Kind kind=mime!=null&&mime.startsWith("audio/")?Asset.Kind.AUDIO:Asset.Kind.VIDEO;
  String name=displayName(uri); Clip clip=project.director.placeMedia(kind,uri.toString(),name,project.transport.frame(),project.transport.sampleRate()*60L);
  project.tv.play(clip.assetId); persist(); workspace="TV"; showStudio();
 }
 void showLiveTvDialog(){final EditText input=new EditText(this);input.setHint("Official/public HLS or media stream URL");new AlertDialog.Builder(this).setTitle("Free Live TV").setMessage("Add a free, authorized public stream. Availability may vary by broadcaster and region.").setView(input).setPositiveButton("PLAY",(d,w)->{String u=input.getText().toString().trim();if(u.isEmpty())return;project.tv.addLiveChannel("Live Channel",u);project.tv.playLiveChannel(project.tv.liveChannels().size()-1);persist();workspace="TV";showStudio();}).setNegativeButton("CANCEL",null).show();}
 String displayName(Uri uri){String name="Imported TV Media";Cursor cur=null;try{cur=getContentResolver().query(uri,null,null,null,null);if(cur!=null&&cur.moveToFirst()){int i=cur.getColumnIndex(OpenableColumns.DISPLAY_NAME);if(i>=0)name=cur.getString(i);}}finally{if(cur!=null)cur.close();}return name;}
 void importTvFile(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("*/*");i.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"video/*","audio/*"});i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);startActivityForResult(i,PICK_TV_MEDIA);}
 TextView t(String s,int z,int c){TextView v=new TextView(this);v.setText(s);v.setTextSize(z);v.setTextColor(c);v.setPadding(18,12,18,12);return v;}
 Button button(String s){Button x=new Button(this);x.setText(s);x.setTextColor(white);x.setBackgroundColor(panel);return x;}
 Button activeButton(String s){Button x=button(s);x.setTextColor(Color.WHITE);x.setBackgroundColor(deepRed);return x;}
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
  LinearLayout timing=new LinearLayout(this);timing.setOrientation(LinearLayout.HORIZONTAL);
  Button bpmDown=button("BPM -");bpmDown.setOnClickListener(v->{project.transport.setBpm(Math.max(20,project.transport.bpm()-1));persist();showStudio();});timing.addView(bpmDown);
  Button bpmUp=button("BPM +");bpmUp.setOnClickListener(v->{project.transport.setBpm(Math.min(400,project.transport.bpm()+1));persist();showStudio();});timing.addView(bpmUp);
  Button metro=button(project.metronome.enabled()?"CLICK ON":"CLICK OFF");metro.setOnClickListener(v->{project.metronome.enabled(!project.metronome.enabled());persist();showStudio();});timing.addView(metro);p.addView(timing);
  LinearLayout loop=new LinearLayout(this);loop.setOrientation(LinearLayout.HORIZONTAL);
  Button loopBar=button(project.transport.loopEnabled()?"LOOP ON":"LOOP 4 BEATS");loopBar.setOnClickListener(v->{if(project.transport.loopEnabled())project.transport.setLoop(project.transport.loopStart(),project.transport.loopEnd(),false);else{long start=project.grid.snap(project.transport.frame(),1);project.transport.setLoop(start,start+project.grid.framesPerBeat()*4,true);}persist();showStudio();});loop.addView(loopBar);
  Button count=button("COUNT-IN "+project.metronome.countInBars()+" BAR");count.setOnClickListener(v->{project.metronome.countInBars((project.metronome.countInBars()+1)%5);persist();showStudio();});loop.addView(count);p.addView(loop);

  p.addView(t("UNIFIED WORKSPACES",14,gold));
  LinearLayout nav=new LinearLayout(this);nav.setOrientation(LinearLayout.VERTICAL);
  for(String s:new String[]{"HOME","ARRANGE","RECORD","COMPOSE / MIDI","MIX","MAESTRO / AI","DIRECTOR","IMAGE / VIDEO","BROWSER","DOWNLOADS","TV"}){
   Button x=s.equals(workspace)?activeButton(s):button(s);x.setOnClickListener(v->{workspace=s;showStudio();});nav.addView(x);
  }
  p.addView(nav);
  p.addView(t("WORKSPACE · "+workspace,18,gold));
  p.addView(t(workspaceText(),13,white));
  addWorkspaceControls(p,status);

  p.addView(t("CORE AUTHORITY",14,gold));
  p.addView(t("Project: "+project.name+"\\nTracks: "+project.tracks().size()+" · Clips: "+project.clips().size()+" · Assets: "+project.assets().size()+"\\nGraph nodes: "+project.audioGraph.nodes().size()+" · Graph routes: "+project.audioGraph.edges().size()+"\\nUndo: "+(project.history.canUndo()?"ready":"empty")+" · Redo: "+(project.history.canRedo()?"ready":"empty"),12,muted));
  p.addView(t("External engines may donate/import capabilities and assets. They do not own Project, Transport, Timeline, Audio Graph, Track, Clip, Asset, Mixer, or Undo state.",12,muted));
  sc.addView(p);setContentView(sc);
 }
 void addWorkspaceControls(LinearLayout p,TextView status){
  if("ARRANGE".equals(workspace)){
   LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.HORIZONTAL);
   Button undo=button("UNDO");undo.setOnClickListener(v->{project.history.undo();persist();showStudio();});r.addView(undo);
   Button redo=button("REDO");redo.setOnClickListener(v->{project.history.redo();persist();showStudio();});r.addView(redo);p.addView(r);
   if(!project.clips().isEmpty()){Button q=button("QUANTIZE FIRST CLIP");q.setOnClickListener(v->{project.grid.quantizeClip(project.clips().get(0).id,4);persist();showStudio();});p.addView(q);}
  }else if("RECORD".equals(workspace)){
   String firstAudio=null;for(Track tr:project.tracks())if(tr.kind==Track.Kind.AUDIO){firstAudio=tr.id;break;}
   final String audioId=firstAudio;
   if(audioId!=null){Button arm=button(project.recording.armed()?"DISARM":"ARM FIRST AUDIO");arm.setOnClickListener(v->{if(project.recording.armed())project.recording.disarm();else project.recording.arm(audioId);persist();showStudio();});p.addView(arm);
    Button mon=button(project.recording.monitoring()?"MONITOR OFF":"MONITOR ON");mon.setEnabled(project.recording.armed());mon.setOnClickListener(v->{project.recording.monitoring(!project.recording.monitoring());persist();showStudio();});p.addView(mon);}
  }else if("MIX".equals(workspace)){
   Button down=button("MASTER -");down.setOnClickListener(v->{AudioGraph.Node m=project.audioGraph.node(AudioGraph.MASTER);project.mixer.masterGain(Math.max(0,m.get("gain",1)-.1));persist();showStudio();});p.addView(down);
   Button up=button("MASTER +");up.setOnClickListener(v->{AudioGraph.Node m=project.audioGraph.node(AudioGraph.MASTER);project.mixer.masterGain(m.get("gain",1)+.1);persist();showStudio();});p.addView(up);
  }else if("COMPOSE / MIDI".equals(workspace)){
   p.addView(t("Grid: 1/4 beat · "+project.grid.framesForDivision(4)+" frames · Metronome "+(project.metronome.enabled()?"ON":"OFF"),12,green));
  }else if("TV".equals(workspace)){
   p.addView(t("🔥  DEMONIC TV",20,white));
   LinearLayout tabs=new LinearLayout(this);tabs.setOrientation(LinearLayout.HORIZONTAL);
   for(String label:new String[]{"LIVE TV","BROWSER / TV","IMPORT FILE","PLAYLISTS","FAVORITES","SETTINGS"}){Button b="LIVE TV".equals(label)?activeButton(label):button(label);if("IMPORT FILE".equals(label))b.setOnClickListener(v->importTvFile());else if("LIVE TV".equals(label)||"BROWSER / TV".equals(label))b.setOnClickListener(v->showLiveTvDialog());tabs.addView(b);}p.addView(tabs);
    p.addView(t("CHANNELS",13,gold));LinearLayout cats=new LinearLayout(this);cats.setOrientation(LinearLayout.VERTICAL);for(String category:new String[]{"All Channels","Local","News","Movies","Kids","Music","International"}){Button cat=category.equals(project.tv.selectedCategory())?activeButton(category):button(category);cat.setOnClickListener(v->{project.tv.selectedCategory(category);persist();showStudio();});cats.addView(cat);}p.addView(cats);
   Button live=activeButton("+  ADD / PLAY FREE LIVE CHANNEL");live.setOnClickListener(v->showLiveTvDialog());p.addView(live);
    if(!project.tv.liveChannels().isEmpty()){p.addView(t("LIVE CHANNELS · "+project.tv.selectedCategory(),13,gold));for(int i=0;i<project.tv.liveChannels().size();i++){final int channel=i;DemonicTv.LiveChannel ch=project.tv.liveChannels().get(i);if(!"All Channels".equals(project.tv.selectedCategory())&&!project.tv.selectedCategory().equals(ch.category))continue;Button cb=button(String.format("%02d  %s  · %s",i+1,ch.name,ch.category));cb.setOnClickListener(v->{project.tv.playLiveChannel(channel);persist();showStudio();});p.addView(cb);}}
   Asset current=project.tv.current();p.addView(t(current==null?"No TV media loaded":"NOW PLAYING · "+current.name,12,green));
   LinearLayout tvc=new LinearLayout(this);tvc.setOrientation(LinearLayout.HORIZONTAL);
   Button playTv=button("PLAY");playTv.setEnabled(current!=null);playTv.setOnClickListener(v->{project.tv.play();persist();showStudio();});tvc.addView(playTv);
   Button pauseTv=button("PAUSE");pauseTv.setOnClickListener(v->{project.tv.pause();persist();showStudio();});tvc.addView(pauseTv);
   Button stopTv=button("STOP");stopTv.setOnClickListener(v->{project.tv.stop();persist();showStudio();});tvc.addView(stopTv);
   Button fs=button("FULLSCREEN");fs.setOnClickListener(v->{project.tv.setSurface(DemonicTv.Surface.FULLSCREEN);persist();showStudio();});tvc.addView(fs);
   Button pip=button("PICTURE IN PICTURE");pip.setOnClickListener(v->{project.tv.setSurface(DemonicTv.Surface.PICTURE_IN_PICTURE);persist();showStudio();});tvc.addView(pip);p.addView(tvc);
   LinearLayout prefs=new LinearLayout(this);prefs.setOrientation(LinearLayout.HORIZONTAL);
   Button fav=button("FAVORITES "+project.tv.favoriteChannels().size());fav.setOnClickListener(v->{if(!project.tv.liveChannels().isEmpty()){project.tv.toggleFavorite(0);persist();showStudio();}});prefs.addView(fav);
   Button subs=button(project.tv.subtitlesEnabled()?"CC ON":"CC OFF");subs.setOnClickListener(v->{project.tv.subtitlesEnabled(!project.tv.subtitlesEnabled());persist();showStudio();});prefs.addView(subs);
   Button audio=button("AUDIO · "+project.tv.audioTrack());audio.setOnClickListener(v->{project.tv.audioTrack("Auto".equals(project.tv.audioTrack())?"Primary":"Auto");persist();showStudio();});prefs.addView(audio);p.addView(prefs);
   p.addView(t("＋ Add Channel   ▣ Import File   ☰ Manage Channels   ▤ EPG / Guide   ⛶ Fullscreen   ◫ Picture in Picture   ♫ Audio Track   CC Subtitles   ⚙ Settings",11,muted));
  }else if("IMAGE / VIDEO".equals(workspace)){
   Clip media=null;for(Clip cl:project.clips()){Asset a=project.asset(cl.assetId);if(a!=null&&(a.kind==Asset.Kind.IMAGE||a.kind==Asset.Kind.VIDEO)){media=cl;break;}}
   final Clip mc=media;
   if(mc!=null){MediaEditor.Transform tr=project.mediaEditor.transform(mc.id);p.addView(t("First media · X "+tr.x+" · Y "+tr.y+" · Scale "+tr.scaleX+" · Rotation "+tr.rotation+" · Opacity "+tr.opacity,12,green));
    Button left=button("NUDGE LEFT");left.setOnClickListener(v->{project.mediaEditor.move(mc.id,Math.max(0,mc.startFrame-project.grid.framesForDivision(4)));persist();showStudio();});p.addView(left);
    Button right=button("NUDGE RIGHT");right.setOnClickListener(v->{project.mediaEditor.move(mc.id,mc.startFrame+project.grid.framesForDivision(4));persist();showStudio();});p.addView(right);
    Button scale=button("SCALE +");scale.setOnClickListener(v->{MediaEditor.Transform x=project.mediaEditor.transform(mc.id);project.mediaEditor.setTransform(mc.id,x.x,x.y,x.scaleX+.1,x.scaleY+.1,x.rotation,x.opacity);persist();showStudio();});p.addView(scale);
    Button rotate=button("ROTATE +15");rotate.setOnClickListener(v->{MediaEditor.Transform x=project.mediaEditor.transform(mc.id);project.mediaEditor.setTransform(mc.id,x.x,x.y,x.scaleX,x.scaleY,x.rotation+15,x.opacity);persist();showStudio();});p.addView(rotate);
    Button fade=button("OPACITY -");fade.setOnClickListener(v->{MediaEditor.Transform x=project.mediaEditor.transform(mc.id);project.mediaEditor.setTransform(mc.id,x.x,x.y,x.scaleX,x.scaleY,x.rotation,Math.max(0,x.opacity-.1));persist();showStudio();});p.addView(fade);
   }
  }
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

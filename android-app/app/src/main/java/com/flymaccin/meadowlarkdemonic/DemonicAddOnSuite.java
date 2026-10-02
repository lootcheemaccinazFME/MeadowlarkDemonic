package com.flymaccin.meadowlarkdemonic;

import java.util.*;

/**
 * Domain layer for the Demonic production add-on suite.
 * All modules describe work in terms of the shared AudioGraph/project model.
 * Platform DSP/rendering adapters may consume these immutable-ish plans.
 */
public final class DemonicAddOnSuite {
    private DemonicAddOnSuite() {}

    public enum Module {
        SAMPLER_DRUM_MACHINE, VOCAL_STUDIO, MASTERING_RACK, STEM_LAB, TIME_PITCH_ENGINE,
        AUDIO_REPAIR_LAB, AUTOMATION_CURVES, GROOVE_ENGINE, CHORD_SCALE_ASSISTANT,
        PERFORMANCE_MODE, LOOPER, PLUGIN_RACK, ASSET_VAULT, VERSION_VAULT,
        COLLABORATION_PACKAGE, LYRICS_NOTES_WORKSPACE, MARKER_SYSTEM,
        SPECTRUM_METER_SUITE, VIDEO_AUDIO_SYNC_LAB, DIRECTOR_SCENE_BUILDER
    }

    public static final class RackModule {
        public final String id, type;
        public boolean bypassed;
        public final Map<String, Float> parameters = new LinkedHashMap<>();
        public RackModule(String id, String type) { this.id=id; this.type=type; }
    }

    /** Input -> Instrument/Sampler -> FX -> Sends -> Bus -> Master. */
    public static final class DemonicRack {
        private final List<RackModule> modules = new ArrayList<>();
        public void add(RackModule m) { modules.add(m); }
        public void move(int from, int to) { modules.add(to, modules.remove(from)); }
        public List<RackModule> modules() { return Collections.unmodifiableList(modules); }
    }

    public static final class Pad {
        public final int index;
        public String assetId;
        public float velocity=1f, gain=1f, pitchSemitones=0f;
        public int chokeGroup=0;
        public long sliceStartFrames=0, sliceEndFrames=-1;
        public final List<String> layers = new ArrayList<>();
        public Pad(int index) { this.index=index; }
    }

    public static final class SamplerDrumMachine {
        public final Pad[] pads = new Pad[16];
        public SamplerDrumMachine() { for(int i=0;i<pads.length;i++) pads[i]=new Pad(i); }
        public void choke(int group, int exceptPad) {
            for(Pad p:pads) if(p.index!=exceptPad && p.chokeGroup==group) p.gain=0f;
        }
    }

    public static final class VocalTake {
        public final String id, assetId;
        public long punchInFrame, punchOutFrame;
        public boolean selected;
        public VocalTake(String id,String assetId){this.id=id;this.assetId=assetId;}
    }
    public static final class VocalStudio {
        public final List<VocalTake> takes=new ArrayList<>();
        public final List<String> doubles=new ArrayList<>(), harmonies=new ArrayList<>();
        public final DemonicRack vocalChain=new DemonicRack();
        public void selectTake(String id){for(VocalTake t:takes)t.selected=t.id.equals(id);}
    }

    public static final class MasteringRack {
        public final DemonicRack chain=new DemonicRack();
        public float targetLufs=-14f, ceilingDb=-1f;
        public String referenceAssetId;
    }

    public static final class Stem {
        public final String name, assetId;
        public float gain=1f, pan=0f;
        public boolean muted, solo;
        public Stem(String name,String assetId){this.name=name;this.assetId=assetId;}
    }
    public static final class StemLab {
        public final List<Stem> stems=new ArrayList<>();
        public List<Stem> audible(){
            boolean anySolo=false; for(Stem s:stems) anySolo|=s.solo;
            List<Stem> out=new ArrayList<>();
            for(Stem s:stems) if(!s.muted && (!anySolo||s.solo)) out.add(s);
            return out;
        }
    }

    public static final class WarpMarker {
        public final long sourceFrame;
        public final double beat;
        public WarpMarker(long sourceFrame,double beat){this.sourceFrame=sourceFrame;this.beat=beat;}
    }
    public static final class TimePitchEngine {
        public double sourceBpm=120, targetBpm=120, semitones=0;
        public final List<WarpMarker> markers=new ArrayList<>();
        public double stretchRatio(){return sourceBpm<=0?1.0:sourceBpm/targetBpm;}
    }

    public static final class RepairPlan {
        public boolean denoise, declick, dehum, trimSilence, normalize;
        public float humHz=60f, normalizePeakDb=-1f;
    }
    public static final class AudioRepairLab { public final RepairPlan plan=new RepairPlan(); }

    public enum CurveShape { LINEAR, BEZIER, STEP, LFO, ENVELOPE }
    public static final class AutomationPoint {
        public final double beat; public final float value;
        public AutomationPoint(double beat,float value){this.beat=beat;this.value=value;}
    }
    public static final class AutomationCurve {
        public String target; public CurveShape shape=CurveShape.LINEAR;
        public final List<AutomationPoint> points=new ArrayList<>();
    }

    public static final class GrooveTemplate {
        public final String id; public double swing=.5, timingHumanize=0, velocityHumanize=0;
        public GrooveTemplate(String id){this.id=id;}
    }
    public static final class GrooveEngine {
        public final Map<String,GrooveTemplate> templates=new LinkedHashMap<>();
        public double apply(double beat, GrooveTemplate g, Random r){
            double human=(r.nextDouble()*2-1)*g.timingHumanize;
            double swing=((long)Math.floor(beat*2)&1)==1?(g.swing-.5)*.5:0;
            return beat+swing+human;
        }
    }

    public static final class ChordScaleAssistant {
        public int rootMidi=60;
        public final Set<Integer> pitchClasses=new LinkedHashSet<>(Arrays.asList(0,2,4,5,7,9,11));
        public int constrain(int midi){
            int best=midi, dist=128;
            for(int d=-12;d<=12;d++){int n=midi+d;int pc=((n-rootMidi)%12+12)%12;if(pitchClasses.contains(pc)&&Math.abs(d)<dist){best=n;dist=Math.abs(d);}}
            return best;
        }
    }

    public static final class LiveClip {
        public final String id, assetId; public boolean looping=true; public int scene;
        public LiveClip(String id,String assetId){this.id=id;this.assetId=assetId;}
    }
    public static final class PerformanceMode {
        public final List<LiveClip> clips=new ArrayList<>();
        public int activeScene=-1;
        public void launchScene(int scene){activeScene=scene;}
    }

    public static final class Looper {
        public final List<String> layers=new ArrayList<>();
        public boolean overdubbing;
        public void addLayer(String assetId){layers.add(assetId);}
        public String undoLast(){return layers.isEmpty()?null:layers.remove(layers.size()-1);}
    }

    public interface PluginModule {
        String id(); String name(); Map<String,Float> parameters();
        default boolean external(){return false;}
    }
    public static final class PluginRack {
        private final List<PluginModule> plugins=new ArrayList<>();
        public void add(PluginModule p){plugins.add(p);}
        public List<PluginModule> plugins(){return Collections.unmodifiableList(plugins);}
    }

    public static final class VaultAsset {
        public final String id, type, uri;
        public Double bpm; public String key, source, projectId;
        public final Set<String> tags=new LinkedHashSet<>();
        public int useCount; public boolean favorite;
        public VaultAsset(String id,String type,String uri){this.id=id;this.type=type;this.uri=uri;}
    }
    public static final class DemonicVault {
        private final Map<String,VaultAsset> assets=new LinkedHashMap<>();
        public void put(VaultAsset a){assets.put(a.id,a);}
        public VaultAsset get(String id){return assets.get(id);}
        public List<VaultAsset> search(String token){
            String q=token==null?"":token.toLowerCase(Locale.US); List<VaultAsset> out=new ArrayList<>();
            for(VaultAsset a:assets.values()) if(a.id.toLowerCase(Locale.US).contains(q)||a.type.toLowerCase(Locale.US).contains(q)||a.tags.toString().toLowerCase(Locale.US).contains(q)) out.add(a);
            return out;
        }
    }

    public static final class Snapshot {
        public final String id,parentId,label,projectJson;
        public final long createdAt;
        public Snapshot(String id,String parentId,String label,String projectJson){this.id=id;this.parentId=parentId;this.label=label;this.projectJson=projectJson;this.createdAt=System.currentTimeMillis();}
    }
    public static final class VersionVault {
        public final Map<String,Snapshot> snapshots=new LinkedHashMap<>();
        public Snapshot save(String parent,String label,String json){Snapshot s=new Snapshot(UUID.randomUUID().toString(),parent,label,json);snapshots.put(s.id,s);return s;}
    }

    public static final class CollaborationPackage {
        public String projectJson; public final List<String> stemAssetIds=new ArrayList<>(), notes=new ArrayList<>(), revisions=new ArrayList<>();
    }

    public static final class TimelineNote {
        public double beat; public String section,text,takeId;
        public TimelineNote(double beat,String section,String text){this.beat=beat;this.section=section;this.text=text;}
    }
    public static final class LyricsNotesWorkspace { public final List<TimelineNote> notes=new ArrayList<>(); }

    public static final class Marker {
        public final double beat; public final String type,label;
        public Marker(double beat,String type,String label){this.beat=beat;this.type=type;this.label=label;}
    }
    public static final class MarkerSystem { public final List<Marker> markers=new ArrayList<>(); }

    public static final class MeterFrame {
        public float peakDb, rmsDb, lufs, phaseCorrelation, stereoWidth;
        public final float[] spectrum;
        public MeterFrame(int bins){spectrum=new float[Math.max(1,bins)];}
    }
    public static final class SpectrumMeterSuite { public MeterFrame latest=new MeterFrame(64); }

    public static final class SyncMarker {
        public final long audioFrame, videoMs; public final String label;
        public SyncMarker(long audioFrame,long videoMs,String label){this.audioFrame=audioFrame;this.videoMs=videoMs;this.label=label;}
    }
    public static final class VideoAudioSyncLab { public final List<SyncMarker> markers=new ArrayList<>(); }

    public static final class DirectorScene {
        public final String id; public String shotOrder="", mediaInstructions="", musicCue="", renderPlan="";
        public DirectorScene(String id){this.id=id;}
    }
    public static final class DirectorSceneBuilder { public final List<DirectorScene> scenes=new ArrayList<>(); }

    /** One project-scoped aggregate so UI, persistence and AudioGraph adapters share state. */
    public static final class Workspace {
        public final SamplerDrumMachine sampler=new SamplerDrumMachine();
        public final VocalStudio vocals=new VocalStudio();
        public final MasteringRack mastering=new MasteringRack();
        public final StemLab stems=new StemLab();
        public final TimePitchEngine timePitch=new TimePitchEngine();
        public final AudioRepairLab repair=new AudioRepairLab();
        public final List<AutomationCurve> automation=new ArrayList<>();
        public final GrooveEngine groove=new GrooveEngine();
        public final ChordScaleAssistant harmony=new ChordScaleAssistant();
        public final PerformanceMode live=new PerformanceMode();
        public final Looper looper=new Looper();
        public final PluginRack plugins=new PluginRack();
        public final DemonicVault vault=new DemonicVault();
        public final VersionVault versions=new VersionVault();
        public final CollaborationPackage collaboration=new CollaborationPackage();
        public final LyricsNotesWorkspace lyricsNotes=new LyricsNotesWorkspace();
        public final MarkerSystem markers=new MarkerSystem();
        public final SpectrumMeterSuite meters=new SpectrumMeterSuite();
        public final VideoAudioSyncLab videoSync=new VideoAudioSyncLab();
        public final DirectorSceneBuilder director=new DirectorSceneBuilder();
        public final DemonicRack universalRack=new DemonicRack();
    }
}

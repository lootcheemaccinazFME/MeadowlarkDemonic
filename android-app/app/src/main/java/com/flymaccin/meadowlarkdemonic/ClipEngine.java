package com.flymaccin.meadowlarkdemonic;
import java.util.*;
/** Reads the canonical timeline at Transport frame positions. Owns no clock or project state. */
public final class ClipEngine{
 private final DemonicProject project;
 public ClipEngine(DemonicProject project){this.project=project;}
 public List<PlaybackSlice> slices(long frame,long frames){if(frames<1)return Collections.emptyList();ArrayList<PlaybackSlice> out=new ArrayList<>();long end=frame+frames;for(Clip c:project.clips()){long a=Math.max(frame,c.startFrame),b=Math.min(end,c.endFrame());if(a<b){Asset asset=project.asset(c.assetId);Track track=project.track(c.trackId);if(asset!=null&&track!=null)out.add(new PlaybackSlice(track,asset,c,a,b-a,c.sourceOffsetFrames+(a-c.startFrame)));}}return out;}
 public List<MidiEvent> midiEvents(long frame,long frames){ArrayList<MidiEvent> out=new ArrayList<>();for(PlaybackSlice s:slices(frame,frames)){if(s.asset.kind!=Asset.Kind.MIDI)continue;for(MidiEvent e:s.asset.midiEvents())if(e.frame>=s.sourceFrame&&e.frame<s.sourceFrame+s.frames)out.add(new MidiEvent(s.timelineFrame+(e.frame-s.sourceFrame),e.status,e.data1,e.data2));}Collections.sort(out,(a,b)->Long.compare(a.frame,b.frame));return out;}
 public static final class PlaybackSlice{public final Track track;public final Asset asset;public final Clip clip;public final long timelineFrame,frames,sourceFrame;PlaybackSlice(Track t,Asset a,Clip c,long tf,long n,long sf){track=t;asset=a;clip=c;timelineFrame=tf;frames=n;sourceFrame=sf;}}
}
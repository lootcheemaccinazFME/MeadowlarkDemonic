package com.flymaccin.meadowlarkdemonic;
import java.util.*;
/** Cross-media orchestrator over the one Demonic project and transport. */
public final class Director{
 private final DemonicProject project;
 public Director(DemonicProject p){project=p;}
 public long frame(){return project.transport.frame();}
 public void seek(long frame){project.transport.seek(frame);}
 public void play(){project.transport.play();}
 public void stop(){project.transport.stop();}
 public Clip placeAudio(String uri,String name,long start,long length){return project.ai.audio(uri,name,start,length);}
 public Clip placeMedia(Asset.Kind kind,String uri,String name,long start,long length){return project.ai.media(kind,uri,name,start,length);}
 public List<Clip> placeStems(List<AiAssetIngestion.Stem> stems,long start){return project.ai.stems(stems,start);}
 public void move(String clipId,long start){project.arrangement.move(clipId,start);}
 public void trim(String clipId,long start,long length,long sourceOffset){project.arrangement.trim(clipId,start,length,sourceOffset);}
}
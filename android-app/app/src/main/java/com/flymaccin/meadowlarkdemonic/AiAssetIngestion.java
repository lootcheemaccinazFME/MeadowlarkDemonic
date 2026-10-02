package com.flymaccin.meadowlarkdemonic;
import java.util.*;
/** Normalizes AI outputs into canonical Demonic assets, tracks and clips. */
public final class AiAssetIngestion{
 public static final class Stem{public final String uri,name;public final long lengthFrames;public Stem(String u,String n,long l){uri=u;name=n;lengthFrames=l;}}
 private final DemonicProject project;
 public AiAssetIngestion(DemonicProject p){project=p;}
 public Clip audio(String uri,String name,long start,long length){return project.maestro.ingestAudio(uri,name,start,length);}
 public Clip midi(Asset midi,String name,long start,long length){return project.maestro.ingestMidi(midi,name,start,length);}
 public Clip media(Asset.Kind kind,String uri,String name,long start,long length){return project.maestro.ingestMedia(kind,uri,name,start,length);}
 public List<Clip> stems(List<Stem> stems,long start){ArrayList<Clip> out=new ArrayList<>();for(Stem s:stems){if(s.lengthFrames<1)throw new IllegalArgumentException("Invalid stem length");out.add(audio(s.uri,s.name,start,s.lengthFrames));}return Collections.unmodifiableList(out);}
}
package com.flymaccin.meadowlarkdemonic;
import java.util.*;
public final class Timeline{
 private final DemonicProject project;
 Timeline(DemonicProject project){this.project=project;}
 public List<Clip> clipsOn(String trackId){ArrayList<Clip> out=new ArrayList<>();for(Clip c:project.clips())if(c.trackId.equals(trackId))out.add(c);Collections.sort(out,(a,b)->Long.compare(a.startFrame,b.startFrame));return out;}
 public List<Clip> activeAt(long frame){ArrayList<Clip> out=new ArrayList<>();for(Clip c:project.clips())if(frame>=c.startFrame&&frame<c.endFrame())out.add(c);return out;}
}
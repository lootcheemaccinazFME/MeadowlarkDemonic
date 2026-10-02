package com.flymaccin.meadowlarkdemonic;

import java.util.*;

/** Project-aware production facade for Maestro/AI and Director. Never owns project state. */
public final class ProductionSession {
    private final DemonicProject project;
    public ProductionSession(DemonicProject project){if(project==null)throw new IllegalArgumentException();this.project=project;}

    public Clip acceptAudio(String uri,String name,long lengthFrames){return project.director.placeAudio(uri,name,project.transport.frame(),lengthFrames);}
    public Clip acceptMedia(Asset.Kind kind,String uri,String name,long lengthFrames){return project.director.placeMedia(kind,uri,name,project.transport.frame(),lengthFrames);}
    public List<Clip> acceptStems(List<AiAssetIngestion.Stem> stems){return project.director.placeStems(stems,project.transport.frame());}
    public void move(String clipId,long frame){project.director.move(clipId,frame);}
    public void trim(String clipId,long start,long length,long sourceOffset){project.director.trim(clipId,start,length,sourceOffset);}
    public void undo(){project.history.undo();}
    public void redo(){project.history.redo();}
    public String summary(){return "AI/Director · frame "+project.transport.frame()+" · assets "+project.assets().size()+" · clips "+project.clips().size();}
}

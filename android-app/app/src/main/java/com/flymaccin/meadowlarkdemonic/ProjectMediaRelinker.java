package com.flymaccin.meadowlarkdemonic;
import android.content.Context;
import android.net.Uri;
import java.io.InputStream;
import java.util.*;

/** Detects missing external media and repairs canonical Asset URIs without replacing IDs or Clips. */
public final class ProjectMediaRelinker{
 private final Context context;private final DemonicProject project;
 public ProjectMediaRelinker(Context c,DemonicProject p){context=c.getApplicationContext();project=p;}
 public List<Asset> missing(){
  ArrayList<Asset> out=new ArrayList<>();
  for(Asset a:project.assets()){if(a.kind==Asset.Kind.MIDI||a.uri==null||a.uri.isEmpty())continue;if(!readable(a.uri))out.add(a);}
  return out;
 }
 public void relink(String assetId,String uri){
  Asset a=project.asset(assetId);if(a==null)throw new IllegalArgumentException("Unknown asset");if(!readable(uri))throw new IllegalArgumentException("Unreadable media");a.relink(uri);
 }
 private boolean readable(String uri){try(InputStream in=context.getContentResolver().openInputStream(Uri.parse(uri))){return in!=null;}catch(Exception e){return false;}}
}

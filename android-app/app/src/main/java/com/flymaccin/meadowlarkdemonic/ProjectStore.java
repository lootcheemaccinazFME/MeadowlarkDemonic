package com.flymaccin.meadowlarkdemonic;

import android.content.Context;

/** Persistence boundary for the one active Demonic project, backed by crash-safe file recovery. */
public final class ProjectStore {
 private static final String PREF="demonic_project",KEY="active";
 private ProjectStore(){}
 public static DemonicProject loadOrCreate(Context c){
  DemonicProject recovered=ProjectRecovery.load(c);
  if(recovered!=null)return recovered;
  String raw=c.getSharedPreferences(PREF,0).getString(KEY,null);
  if(raw!=null)try{DemonicProject p=DemonicProject.fromJson(raw);save(c,p);return p;}catch(Exception ignored){}
  DemonicProject p=new DemonicProject("Demonic Project");save(c,p);return p;
 }
 public static void save(Context c,DemonicProject p){
  try{ProjectRecovery.save(c,p);}catch(Exception ignored){}
  c.getSharedPreferences(PREF,0).edit().putString(KEY,p.toJson().toString()).apply();
 }
 public static boolean hasRecovery(Context c){return ProjectRecovery.hasRecovery(c);}
 public static DemonicProject recover(Context c){return ProjectRecovery.recover(c);}
}

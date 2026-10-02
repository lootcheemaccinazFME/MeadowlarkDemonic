package com.flymaccin.meadowlarkdemonic;

import android.content.Context;

/** Persistence boundary for the one active Demonic project. */
public final class ProjectStore {
 private static final String PREF="demonic_project",KEY="active";
 private ProjectStore(){}
 public static DemonicProject loadOrCreate(Context c){
  String raw=c.getSharedPreferences(PREF,0).getString(KEY,null);
  if(raw!=null)try{return DemonicProject.fromJson(raw);}catch(Exception ignored){}
  DemonicProject p=new DemonicProject("Demonic Project");save(c,p);return p;
 }
 public static void save(Context c,DemonicProject p){c.getSharedPreferences(PREF,0).edit().putString(KEY,p.toJson().toString()).apply();}
}
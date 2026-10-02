package com.flymaccin.meadowlarkdemonic;

/** Keeps the project Asset registry and Demonic Vault synchronized. */
public final class VaultIngestion {
 private VaultIngestion(){}
 public static DemonicAddOnSuite.VaultAsset ingest(DemonicProject p,Asset a,String source){
  if(p==null||a==null)throw new IllegalArgumentException();
  DemonicAddOnSuite.VaultAsset v=new DemonicAddOnSuite.VaultAsset(a.id,a.kind.name(),a.uri);
  v.source=source==null?"project":source;v.projectId=p.id;v.tags.add(a.kind.name().toLowerCase());v.tags.add("project");
  p.addOns.vault.put(v);return v;
 }
 public static void syncProjectAssets(DemonicProject p){
  for(Asset a:p.assets())if(p.addOns.vault.get(a.id)==null)ingest(p,a,"project");
 }
}

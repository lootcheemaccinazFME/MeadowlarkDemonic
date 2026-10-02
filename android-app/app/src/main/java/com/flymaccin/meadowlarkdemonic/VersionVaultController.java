package com.flymaccin.meadowlarkdemonic;

/** Project snapshot/restore facade backed by the Demonic Version Vault. */
public final class VersionVaultController {
 private VersionVaultController(){}
 public static DemonicAddOnSuite.Snapshot checkpoint(DemonicProject p,String parentId,String label){
  if(p==null)throw new IllegalArgumentException();
  return p.addOns.versions.save(parentId,label==null?"Checkpoint":label,p.toJson().toString());
 }
 public static DemonicProject restore(DemonicProject p,String snapshotId){
  if(p==null||snapshotId==null)throw new IllegalArgumentException();
  DemonicAddOnSuite.Snapshot s=p.addOns.versions.snapshots.get(snapshotId);
  if(s==null)throw new IllegalArgumentException("Unknown snapshot "+snapshotId);
  return DemonicProject.fromJson(s.projectJson);
 }
}

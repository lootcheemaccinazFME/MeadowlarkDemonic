package com.flymaccin.meadowlarkdemonic;
import java.util.*;
public final class CollaborationStudio{
 public enum Role{OWNER,EDITOR,PERFORMER,VIEWER}
 private final LinkedHashMap<String,Role> collaborators=new LinkedHashMap<>(); private String branch="main";
 public void grant(String id,Role role){if(id!=null&&!id.trim().isEmpty())collaborators.put(id,role);} public Map<String,Role> collaborators(){return Collections.unmodifiableMap(collaborators);}
 public String branch(){return branch;} public void branch(String name){if(name!=null&&!name.trim().isEmpty())branch=name.trim();}
}

package com.flymaccin.meadowlarkdemonic;
import java.util.*;
import org.json.*;
public final class CollaborationStudio{
 public enum Role{OWNER,EDITOR,PERFORMER,VIEWER}
 private final LinkedHashMap<String,Role> collaborators=new LinkedHashMap<>(); private String branch="main";
 public void grant(String id,Role role){if(id!=null&&!id.trim().isEmpty())collaborators.put(id,role);} public Map<String,Role> collaborators(){return Collections.unmodifiableMap(collaborators);}
 public String branch(){return branch;} public void branch(String name){if(name!=null&&!name.trim().isEmpty())branch=name.trim();}
 public JSONObject toJson(){try{JSONObject x=new JSONObject();for(Map.Entry<String,Role> e:collaborators.entrySet())x.put(e.getKey(),e.getValue().name());return new JSONObject().put("branch",branch).put("collaborators",x);}catch(Exception e){throw new IllegalStateException(e);}}
 public void loadJson(JSONObject o){if(o==null)return;branch(o.optString("branch",branch));collaborators.clear();JSONObject x=o.optJSONObject("collaborators");if(x!=null){Iterator<String> it=x.keys();while(it.hasNext()){String k=it.next();try{grant(k,Role.valueOf(x.optString(k,"VIEWER")));}catch(Exception ignored){}}}}
}

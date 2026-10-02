package com.flymaccin.meadowlarkdemonic;
import java.util.*;
import org.json.*;
public final class PublishingDelivery{
 public static final class Credit{public final String name,role;Credit(String n,String r){name=n;role=r;}}
 private final ArrayList<Credit> credits=new ArrayList<>(); private String remixParent="";
 public void credit(String name,String role){credits.add(new Credit(name,role));} public List<Credit> credits(){return Collections.unmodifiableList(credits);}
 public String remixParent(){return remixParent;} public void remixParent(String id){remixParent=id==null?"":id;}
 public JSONObject toJson(){try{JSONArray a=new JSONArray();for(Credit c:credits)a.put(new JSONObject().put("name",c.name).put("role",c.role));return new JSONObject().put("remixParent",remixParent).put("credits",a);}catch(Exception e){throw new IllegalStateException(e);}}
 public void loadJson(JSONObject o){if(o==null)return;remixParent(o.optString("remixParent",""));credits.clear();JSONArray a=o.optJSONArray("credits");if(a!=null)for(int i=0;i<a.length();i++){JSONObject j=a.optJSONObject(i);if(j!=null)credit(j.optString("name"),j.optString("role"));}}
}

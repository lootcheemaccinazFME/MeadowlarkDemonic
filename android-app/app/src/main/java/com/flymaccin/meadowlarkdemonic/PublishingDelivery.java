package com.flymaccin.meadowlarkdemonic;
import java.util.*;
public final class PublishingDelivery{
 public static final class Credit{public final String name,role;Credit(String n,String r){name=n;role=r;}}
 private final ArrayList<Credit> credits=new ArrayList<>(); private String remixParent="";
 public void credit(String name,String role){credits.add(new Credit(name,role));} public List<Credit> credits(){return Collections.unmodifiableList(credits);}
 public String remixParent(){return remixParent;} public void remixParent(String id){remixParent=id==null?"":id;}
}

package com.flymaccin.meadowlarkdemonic;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

/** Generic signal graph authority. Mixer, instruments and FX must compile into this graph. */
public final class AudioGraph {
 public static final String MASTER="master";
 private final LinkedHashMap<String,Node> nodes=new LinkedHashMap<>();
 private final ArrayList<Edge> edges=new ArrayList<>();
 public AudioGraph(){nodes.put(MASTER,new Node(MASTER,"MASTER"));}
 public synchronized Node addNode(String id,String type){if(nodes.containsKey(id))throw new IllegalArgumentException("Duplicate node "+id);Node n=new Node(id,type);nodes.put(id,n);return n;}
 public synchronized void connect(String from,String to){if(!nodes.containsKey(from)||!nodes.containsKey(to))throw new IllegalArgumentException("Unknown graph node");for(Edge e:edges)if(e.from.equals(from)&&e.to.equals(to))return;edges.add(new Edge(from,to));}
 public synchronized Node node(String id){return nodes.get(id);}
 public synchronized Collection<Node> nodes(){return Collections.unmodifiableCollection(nodes.values());}
 public synchronized List<Edge> edges(){return Collections.unmodifiableList(edges);}
 public synchronized JSONObject toJson(){try{JSONArray ns=new JSONArray(),es=new JSONArray();for(Node n:nodes.values())ns.put(n.toJson());for(Edge e:edges)es.put(e.toJson());return new JSONObject().put("nodes",ns).put("edges",es);}catch(Exception e){throw new IllegalStateException(e);}}
 public static AudioGraph fromJson(JSONObject o){AudioGraph g=new AudioGraph();if(o==null)return g;try{JSONArray ns=o.optJSONArray("nodes");if(ns!=null)for(int i=0;i<ns.length();i++){JSONObject n=ns.getJSONObject(i);String id=n.getString("id");if(!MASTER.equals(id)&&!g.nodes.containsKey(id)){Node nn=new Node(id,n.optString("type","GENERIC"));nn.load(n.optJSONObject("params"));g.nodes.put(id,nn);}else if(MASTER.equals(id))g.nodes.get(MASTER).load(n.optJSONObject("params"));}JSONArray es=o.optJSONArray("edges");if(es!=null)for(int i=0;i<es.length();i++){JSONObject e=es.getJSONObject(i);g.connect(e.getString("from"),e.getString("to"));}}catch(Exception e){throw new IllegalArgumentException("Invalid graph",e);}return g;}
 public static final class Node{public final String id,type;private final LinkedHashMap<String,Double> params=new LinkedHashMap<>();Node(String id,String type){this.id=id;this.type=type;}public synchronized void set(String key,double value){params.put(key,value);}public synchronized double get(String key,double fallback){Double v=params.get(key);return v==null?fallback:v;}JSONObject toJson(){try{JSONObject p=new JSONObject();for(Map.Entry<String,Double> e:params.entrySet())p.put(e.getKey(),e.getValue());return new JSONObject().put("id",id).put("type",type).put("params",p);}catch(Exception e){throw new IllegalStateException(e);}}void load(JSONObject p){if(p==null)return;Iterator<String> it=p.keys();while(it.hasNext()){String k=it.next();set(k,p.optDouble(k,0));}}}
 public static final class Edge{public final String from,to;Edge(String f,String t){from=f;to=t;}JSONObject toJson(){try{return new JSONObject().put("from",from).put("to",to);}catch(Exception e){throw new IllegalStateException(e);}}}
}
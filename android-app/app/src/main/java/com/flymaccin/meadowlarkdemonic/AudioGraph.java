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
 public synchronized Collection<Node> nodes(){return Collections.unmodifiableCollection(nodes.values());}
 public synchronized List<Edge> edges(){return Collections.unmodifiableList(edges);}
 public synchronized JSONObject toJson(){try{JSONArray ns=new JSONArray(),es=new JSONArray();for(Node n:nodes.values())ns.put(n.toJson());for(Edge e:edges)es.put(e.toJson());return new JSONObject().put("nodes",ns).put("edges",es);}catch(Exception e){throw new IllegalStateException(e);}}
 public static AudioGraph fromJson(JSONObject o){AudioGraph g=new AudioGraph();if(o==null)return g;try{JSONArray ns=o.optJSONArray("nodes");if(ns!=null)for(int i=0;i<ns.length();i++){JSONObject n=ns.getJSONObject(i);String id=n.getString("id");if(!MASTER.equals(id)&&!g.nodes.containsKey(id))g.nodes.put(id,new Node(id,n.optString("type","GENERIC")));}JSONArray es=o.optJSONArray("edges");if(es!=null)for(int i=0;i<es.length();i++){JSONObject e=es.getJSONObject(i);g.connect(e.getString("from"),e.getString("to"));}}catch(Exception e){throw new IllegalArgumentException("Invalid graph",e);}return g;}
 public static final class Node{public final String id,type;Node(String id,String type){this.id=id;this.type=type;}JSONObject toJson(){try{return new JSONObject().put("id",id).put("type",type);}catch(Exception e){throw new IllegalStateException(e);}}}
 public static final class Edge{public final String from,to;Edge(String f,String t){from=f;to=t;}JSONObject toJson(){try{return new JSONObject().put("from",from).put("to",to);}catch(Exception e){throw new IllegalStateException(e);}}}
}
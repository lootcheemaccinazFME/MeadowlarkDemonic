package com.flymaccin.meadowlarkdemonic;
import java.util.*;
/** Frame-based automation targeting canonical AudioGraph node parameters. */
public final class Automation{
 public static final class Point{public final long frame;public final double value;public Point(long f,double v){if(f<0)throw new IllegalArgumentException("frame");frame=f;value=v;}}
 private final DemonicProject project;
 private final LinkedHashMap<String,ArrayList<Point>> lanes=new LinkedHashMap<>();
 public Automation(DemonicProject p){project=p;}
 public static String target(String nodeId,String parameter){return nodeId+"/"+parameter;}
 public void addPoint(final String target,final long frame,final double value){final ArrayList<Point> lane=lanes.computeIfAbsent(target,k->new ArrayList<Point>());final Point p=new Point(frame,value);project.history.execute(new UndoHistory.Command(){public void apply(){lane.add(p);Collections.sort(lane,(a,b)->Long.compare(a.frame,b.frame));}public void revert(){lane.remove(p);}public String label(){return "Automation point";}});}
 public List<Point> points(String target){ArrayList<Point> p=lanes.get(target);return p==null?Collections.<Point>emptyList():Collections.unmodifiableList(p);}
 public double valueAt(String target,long frame,double fallback){List<Point> ps=points(target);if(ps.isEmpty())return fallback;Point prev=ps.get(0);if(frame<=prev.frame)return prev.value;for(int i=1;i<ps.size();i++){Point next=ps.get(i);if(frame<=next.frame){double t=(double)(frame-prev.frame)/(double)(next.frame-prev.frame);return prev.value+(next.value-prev.value)*t;}prev=next;}return prev.value;}
 public void applyAt(String target,long frame){int slash=target.lastIndexOf('/');if(slash<1)throw new IllegalArgumentException("target");AudioGraph.Node n=project.audioGraph.node(target.substring(0,slash));if(n==null)throw new IllegalArgumentException("Unknown node");String param=target.substring(slash+1);n.set(param,valueAt(target,frame,n.get(param,0)));}
}
package com.flymaccin.meadowlarkdemonic;
import java.util.*;
import org.json.*;
/** Image/video edit facade keyed to canonical media Clip IDs and shared timeline frames. */
public final class MediaEditor{
 public static final class Transform{
  public double x=0,y=0,scaleX=1,scaleY=1,rotation=0,opacity=1;
  Transform copy(){Transform t=new Transform();t.x=x;t.y=y;t.scaleX=scaleX;t.scaleY=scaleY;t.rotation=rotation;t.opacity=opacity;return t;}
 }
 private final DemonicProject project;
 private final LinkedHashMap<String,Transform> transforms=new LinkedHashMap<>();
 public MediaEditor(DemonicProject p){project=p;}
 private Clip mediaClip(String id){for(Clip c:project.clips())if(c.id.equals(id)){Asset a=project.asset(c.assetId);if(a!=null&&(a.kind==Asset.Kind.IMAGE||a.kind==Asset.Kind.VIDEO))return c;break;}throw new IllegalArgumentException("Media clip required");}
 public Transform transform(String clipId){mediaClip(clipId);Transform t=transforms.get(clipId);if(t==null){t=new Transform();transforms.put(clipId,t);}return t.copy();}
 public void setTransform(final String clipId,final double x,final double y,final double sx,final double sy,final double rotation,final double opacity){mediaClip(clipId);if(sx<=0||sy<=0)throw new IllegalArgumentException("scale");final Transform before=transform(clipId),after=new Transform();after.x=x;after.y=y;after.scaleX=sx;after.scaleY=sy;after.rotation=rotation;after.opacity=Math.max(0,Math.min(1,opacity));project.history.execute(new UndoHistory.Command(){public void apply(){transforms.put(clipId,after.copy());}public void revert(){transforms.put(clipId,before.copy());}public String label(){return "Media transform";}});}
 public void move(String clipId,long frame){mediaClip(clipId);project.arrangement.move(clipId,frame);}
 public void trim(String clipId,long frame,long length,long sourceOffset){mediaClip(clipId);project.arrangement.trim(clipId,frame,length,sourceOffset);}
 public JSONObject toJson(){JSONObject out=new JSONObject();try{for(Map.Entry<String,Transform> e:transforms.entrySet()){Transform t=e.getValue();out.put(e.getKey(),new JSONObject().put("x",t.x).put("y",t.y).put("scaleX",t.scaleX).put("scaleY",t.scaleY).put("rotation",t.rotation).put("opacity",t.opacity));}return out;}catch(Exception e){throw new IllegalStateException("Media transform serialization failed",e);}}
 public void loadJson(JSONObject in){transforms.clear();if(in==null)return;try{Iterator<String> keys=in.keys();while(keys.hasNext()){String id=keys.next();JSONObject o=in.optJSONObject(id);if(o==null)continue;Transform t=new Transform();t.x=o.optDouble("x",0);t.y=o.optDouble("y",0);t.scaleX=o.optDouble("scaleX",1);t.scaleY=o.optDouble("scaleY",1);t.rotation=o.optDouble("rotation",0);t.opacity=Math.max(0,Math.min(1,o.optDouble("opacity",1)));transforms.put(id,t);}}catch(Exception e){throw new IllegalArgumentException("Invalid media transforms",e);}}
}

package com.flymaccin.meadowlarkdemonic;

import java.util.*;
import org.json.*;

/** One Demonic TV playback session exposed through in-app, fullscreen and PiP surfaces. */
public final class DemonicTv {
    public enum Surface { IN_APP, FULLSCREEN, PICTURE_IN_PICTURE }
    public enum State { STOPPED, PLAYING, PAUSED }

    private final DemonicProject project;
    private final ArrayList<String> queue = new ArrayList<>();
    private final ArrayList<LiveChannel> liveChannels = new ArrayList<>();
    private int queueIndex = -1;
    private long positionFrame = 0;
    private Surface surface = Surface.IN_APP;
    private State state = State.STOPPED;
    private boolean poweredOn = true;
    private float volume = 1.0f;
    private boolean subtitlesEnabled=false;
    private String audioTrack="Auto";
    private String selectedCategory="All Channels";
    private final LinkedHashSet<String> favorites=new LinkedHashSet<>();
    private final LinkedHashMap<String,ArrayList<Integer>> playlists=new LinkedHashMap<>();

    public DemonicTv(DemonicProject project) { this.project = project; }

    public Surface surface() { return surface; }
    public State state() { return state; }
    public long positionFrame() { return positionFrame; }
    public List<String> queue() { return Collections.unmodifiableList(queue); }
    public boolean poweredOn(){return poweredOn;}
    public float volume(){return volume;}
    public List<LiveChannel> liveChannels(){return Collections.unmodifiableList(liveChannels);}
    public boolean subtitlesEnabled(){return subtitlesEnabled;} public void subtitlesEnabled(boolean v){subtitlesEnabled=v;}
    public String audioTrack(){return audioTrack;} public void audioTrack(String v){audioTrack=v==null||v.trim().isEmpty()?"Auto":v.trim();}
    public String selectedCategory(){return selectedCategory;} public void selectedCategory(String v){selectedCategory=v==null||v.trim().isEmpty()?"All Channels":v.trim();}
    public boolean isFavorite(int index){return index>=0&&index<liveChannels.size()&&favorites.contains(liveChannels.get(index).streamUrl);}
    public void toggleFavorite(int index){if(index<0||index>=liveChannels.size())throw new IllegalArgumentException("channel");String u=liveChannels.get(index).streamUrl;if(!favorites.remove(u))favorites.add(u);}
    public List<LiveChannel> favoriteChannels(){ArrayList<LiveChannel> out=new ArrayList<>();for(LiveChannel ch:liveChannels)if(favorites.contains(ch.streamUrl))out.add(ch);return Collections.unmodifiableList(out);}
    public Set<String> playlistNames(){return Collections.unmodifiableSet(playlists.keySet());}
    public void savePlaylist(String name,List<Integer> indexes){if(name==null||name.trim().isEmpty())throw new IllegalArgumentException("playlist");ArrayList<Integer> clean=new ArrayList<>();if(indexes!=null)for(Integer i:indexes)if(i!=null&&i>=0&&i<liveChannels.size()&&!clean.contains(i))clean.add(i);playlists.put(name.trim(),clean);}
    public List<LiveChannel> playlist(String name){ArrayList<LiveChannel> out=new ArrayList<>();ArrayList<Integer> ids=playlists.get(name);if(ids!=null)for(Integer i:ids)if(i>=0&&i<liveChannels.size())out.add(liveChannels.get(i));return Collections.unmodifiableList(out);}
    public void addLiveChannel(String name,String streamUrl){addLiveChannel(name,streamUrl,"Other","");}
    public void addLiveChannel(String name,String streamUrl,String category,String guideId){if(name==null||name.trim().isEmpty()||streamUrl==null||streamUrl.trim().isEmpty())throw new IllegalArgumentException("channel");liveChannels.add(new LiveChannel(name.trim(),streamUrl.trim(),category,guideId));}
    public void renameLiveChannel(int index,String name){if(index<0||index>=liveChannels.size())throw new IllegalArgumentException("channel");if(name==null||name.trim().isEmpty())throw new IllegalArgumentException("name");LiveChannel old=liveChannels.get(index);liveChannels.set(index,new LiveChannel(name.trim(),old.streamUrl,old.category,old.guideId));}
    public void removeLiveChannel(int index){if(index<0||index>=liveChannels.size())throw new IllegalArgumentException("channel");liveChannels.remove(index);}
    public void clearLiveChannels(){liveChannels.clear();}
    public Asset playLiveChannel(int index){if(index<0||index>=liveChannels.size())throw new IllegalArgumentException("channel");LiveChannel ch=liveChannels.get(index);Asset a=new Asset(Asset.Kind.VIDEO,ch.streamUrl,ch.name);project.addAssetUndoable(a);play(a.id);return a;}
    public static final class LiveChannel { public final String name,streamUrl,category,guideId; LiveChannel(String n,String u){this(n,u,"Other","");} LiveChannel(String n,String u,String c,String g){name=n;streamUrl=u;category=c==null||c.trim().isEmpty()?"Other":c.trim();guideId=g==null?"":g.trim();} }

    public Asset current() {
        return queueIndex >= 0 && queueIndex < queue.size() ? project.asset(queue.get(queueIndex)) : null;
    }

    public void setSurface(Surface value) {
        if (value == null) throw new IllegalArgumentException("surface");
        surface = value;
    }

    public void setQueue(List<String> assetIds, int startIndex) {
        queue.clear();
        if (assetIds != null) for (String id : assetIds) {
            Asset a = requirePlayable(id);
            queue.add(a.id);
        }
        if (queue.isEmpty()) { queueIndex=-1; positionFrame=0; state=State.STOPPED; return; }
        if (startIndex < 0 || startIndex >= queue.size()) throw new IllegalArgumentException("startIndex");
        queueIndex=startIndex; positionFrame=0; state=State.PAUSED;
    }

    public void play(String assetId) {
        Asset a=requirePlayable(assetId);
        int i=queue.indexOf(a.id);
        if(i<0){queue.add(a.id);i=queue.size()-1;}
        queueIndex=i; state=State.PLAYING;
    }

    public void play(){if(!poweredOn)throw new IllegalStateException("TV power off");if(current()==null)throw new IllegalStateException("No TV asset");state=State.PLAYING;}
    public void pause(){if(state==State.PLAYING)state=State.PAUSED;}
    public void stop(){state=State.STOPPED;positionFrame=0;}
    public void seek(long frame){if(frame<0)throw new IllegalArgumentException("frame");positionFrame=frame;}
    public void fastForward(long frames){if(frames<0)throw new IllegalArgumentException("frames");positionFrame+=frames;}
    public void rewind(long frames){if(frames<0)throw new IllegalArgumentException("frames");positionFrame=Math.max(0,positionFrame-frames);}
    public void fastForwardSeconds(int seconds){fastForward((long)Math.max(0,seconds)*project.transport.sampleRate());}
    public void rewindSeconds(int seconds){rewind((long)Math.max(0,seconds)*project.transport.sampleRate());}
    public void advance(long frames){if(state==State.PLAYING)positionFrame=Math.max(0,positionFrame+frames);}

    public Asset next(){if(queueIndex+1>=queue.size())return null;queueIndex++;positionFrame=0;return current();}
    public Asset previous(){if(queueIndex<=0)return null;queueIndex--;positionFrame=0;return current();}
    public Asset channelUp(){return next();}
    public Asset channelDown(){return previous();}
    public float volumeUp(){volume=Math.min(1f,volume+0.05f);return volume;}
    public float volumeDown(){volume=Math.max(0f,volume-0.05f);return volume;}
    public boolean togglePower(){poweredOn=!poweredOn;if(!poweredOn)state=State.PAUSED;return poweredOn;}

    /** Lifecycle snapshot used when Android recreates a viewing surface. */
    public Session snapshot(){return new Session(current()==null?null:current().id,positionFrame,state,surface,queue,queueIndex);}
    public void restore(Session s){if(s==null)return;setQueue(s.queue,s.queue.isEmpty()?0:Math.max(0,Math.min(s.queueIndex,s.queue.size()-1)));positionFrame=Math.max(0,s.positionFrame);state=s.state;surface=s.surface;}

    public static final class Session{
        public final String assetId; public final long positionFrame; public final State state; public final Surface surface; public final List<String> queue; public final int queueIndex;
        Session(String assetId,long positionFrame,State state,Surface surface,List<String> queue,int queueIndex){this.assetId=assetId;this.positionFrame=positionFrame;this.state=state;this.surface=surface;this.queue=new ArrayList<>(queue);this.queueIndex=queueIndex;}
    }

    public JSONObject toJson(){try{JSONObject o=new JSONObject().put("surface",surface.name()).put("state",state.name()).put("positionFrame",positionFrame).put("poweredOn",poweredOn).put("volume",volume).put("subtitles",subtitlesEnabled).put("audioTrack",audioTrack).put("category",selectedCategory);JSONArray ch=new JSONArray();for(LiveChannel x:liveChannels)ch.put(new JSONObject().put("name",x.name).put("url",x.streamUrl).put("category",x.category).put("guideId",x.guideId));o.put("channels",ch);JSONArray fav=new JSONArray();for(String u:favorites)fav.put(u);o.put("favorites",fav);JSONObject pls=new JSONObject();for(Map.Entry<String,ArrayList<Integer>> e:playlists.entrySet()){JSONArray a=new JSONArray();for(Integer i:e.getValue())a.put(i);pls.put(e.getKey(),a);}o.put("playlists",pls);return o;}catch(Exception e){throw new IllegalStateException("TV serialization failed",e);}}
    public void loadJson(JSONObject o){if(o==null)return;try{surface=Surface.valueOf(o.optString("surface",Surface.IN_APP.name()));state=State.valueOf(o.optString("state",State.STOPPED.name()));positionFrame=Math.max(0,o.optLong("positionFrame",0));poweredOn=o.optBoolean("poweredOn",true);volume=(float)Math.max(0,Math.min(1,o.optDouble("volume",1)));subtitlesEnabled=o.optBoolean("subtitles",false);audioTrack=o.optString("audioTrack","Auto");selectedCategory=o.optString("category","All Channels");liveChannels.clear();JSONArray ch=o.optJSONArray("channels");if(ch!=null)for(int i=0;i<ch.length();i++){JSONObject x=ch.getJSONObject(i);liveChannels.add(new LiveChannel(x.optString("name","Live Channel"),x.optString("url",""),x.optString("category","Other"),x.optString("guideId","")));}favorites.clear();JSONArray fav=o.optJSONArray("favorites");if(fav!=null)for(int i=0;i<fav.length();i++)favorites.add(fav.optString(i));playlists.clear();JSONObject pls=o.optJSONObject("playlists");if(pls!=null){Iterator<String> keys=pls.keys();while(keys.hasNext()){String k=keys.next();JSONArray a=pls.optJSONArray(k);ArrayList<Integer> ids=new ArrayList<>();if(a!=null)for(int i=0;i<a.length();i++)ids.add(a.optInt(i));playlists.put(k,ids);}}}catch(Exception ignored){}}

    private Asset requirePlayable(String id){
        Asset a=project.asset(id);
        if(a==null)throw new IllegalArgumentException("Unknown asset "+id);
        if(a.kind!=Asset.Kind.VIDEO&&a.kind!=Asset.Kind.AUDIO)throw new IllegalArgumentException("TV requires audio/video asset");
        return a;
    }
}

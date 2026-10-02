package com.flymaccin.meadowlarkdemonic;

import android.content.Context;
import android.media.*;
import android.net.Uri;
import java.nio.ByteBuffer;

/** Platform-codec fallback decoder for common Android-supported compressed audio assets. */
public final class AndroidCodecDecoder implements AudioRuntime.Decoder {
 private final Context context;
 public AndroidCodecDecoder(Context c){context=c.getApplicationContext();}
 @Override public int decode(Asset asset,long sourceFrame,long frames,short[] out){
  if(asset==null||asset.kind!=Asset.Kind.AUDIO||out==null||frames<=0)return 0;
  MediaExtractor ex=new MediaExtractor();MediaCodec codec=null;
  try{
   ex.setDataSource(context,Uri.parse(asset.uri),null);int track=-1;MediaFormat fmt=null;
   for(int i=0;i<ex.getTrackCount();i++){MediaFormat f=ex.getTrackFormat(i);String mime=f.getString(MediaFormat.KEY_MIME);if(mime!=null&&mime.startsWith("audio/")){track=i;fmt=f;break;}}
   if(track<0||fmt==null)return 0;ex.selectTrack(track);int rate=fmt.getInteger(MediaFormat.KEY_SAMPLE_RATE);int channels=fmt.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
   long us=Math.max(0,sourceFrame)*1000000L/Math.max(1,rate);ex.seekTo(us,MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
   String mime=fmt.getString(MediaFormat.KEY_MIME);codec=MediaCodec.createDecoderByType(mime);codec.configure(fmt,null,null,0);codec.start();
   boolean inputDone=false,outputDone=false;int written=0;MediaCodec.BufferInfo info=new MediaCodec.BufferInfo();
   while(!outputDone&&written<frames){
    if(!inputDone){int ix=codec.dequeueInputBuffer(10000);if(ix>=0){ByteBuffer b=codec.getInputBuffer(ix);int size=ex.readSampleData(b,0);if(size<0){codec.queueInputBuffer(ix,0,0,0,MediaCodec.BUFFER_FLAG_END_OF_STREAM);inputDone=true;}else{codec.queueInputBuffer(ix,0,size,ex.getSampleTime(),0);ex.advance();}}}
    int ox=codec.dequeueOutputBuffer(info,10000);if(ox>=0){ByteBuffer b=codec.getOutputBuffer(ox);if(b!=null&&info.size>0){b.position(info.offset);b.limit(info.offset+info.size);while(b.remaining()>=2&&written<frames){short l=b.getShort();short r=channels>1&&b.remaining()>=2?b.getShort():l;out[written*2]=l;out[written*2+1]=r;written++;}}codec.releaseOutputBuffer(ox,false);if((info.flags&MediaCodec.BUFFER_FLAG_END_OF_STREAM)!=0)outputDone=true;}
   }
   return written;
  }catch(Exception ignored){return 0;}finally{try{ex.release();}catch(Exception ignored){}if(codec!=null)try{codec.stop();codec.release();}catch(Exception ignored){}}
 }
}

package com.flymaccin.meadowlarkdemonic;

import android.content.Context;
import android.net.Uri;
import java.io.*;

/** Device microphone -> PCM16 mono WAV -> canonical project clip bridge. */
public final class AndroidTakeRecorder {
 private final Context context; private final DemonicProject project; private final AndroidAudioIO io;
 private volatile boolean running; private Thread worker; private File file; private long frames; private String trackId; private long startFrame; private volatile RuntimeException failure;

 public AndroidTakeRecorder(Context context,DemonicProject project){
  if(context==null||project==null)throw new IllegalArgumentException();
  this.context=context.getApplicationContext();this.project=project;this.io=new AndroidAudioIO(project);
 }
 public synchronized boolean running(){return running;}
 public synchronized void start(String armedTrackId)throws IOException{
  if(running)throw new IllegalStateException("Recording already active");
  Track t=project.track(armedTrackId);if(t==null||t.kind!=Track.Kind.AUDIO)throw new IllegalArgumentException("Arm an audio track first");
  File dir=new File(context.getFilesDir(),"takes");if(!dir.exists()&&!dir.mkdirs())throw new IOException("Cannot create takes directory");
  file=new File(dir,"take_"+System.currentTimeMillis()+".wav");writeEmptyHeader(file,project.transport.sampleRate());
  trackId=armedTrackId;startFrame=project.recording.recordStartFrame();frames=0;failure=null;io.startInput();running=true;
  worker=new Thread(()->capture(),"DemonicTakeRecorder");worker.start();project.transport.record();
 }
 private void capture(){
  short[] buffer=new short[2048];
  try(FileOutputStream out=new FileOutputStream(file,true);BufferedOutputStream bos=new BufferedOutputStream(out)){
   while(running){int n=io.read(buffer,0,buffer.length);for(int i=0;i<n;i++){short s=buffer[i];bos.write(s&255);bos.write((s>>>8)&255);}frames+=Math.max(0,n);}
   bos.flush();
  }catch(Exception e){failure=new IllegalStateException("Microphone capture failed",e);running=false;}
 }
 public synchronized Clip stopAndCommit()throws IOException{
  if(!running&&worker==null){if(failure!=null)throw failure;return null;}
  running=false;io.stopInput();Thread w=worker;worker=null;if(w!=null)try{w.join(3000);}catch(InterruptedException e){Thread.currentThread().interrupt();}
  if(failure!=null)throw failure;
  patchHeader(file,project.transport.sampleRate(),frames);project.transport.stop();
  if(frames<=0){file.delete();return null;}
  Clip clip=project.recording.commitAudio(trackId,Uri.fromFile(file).toString(),file.getName(),startFrame,frames);
  return clip;
 }
 public synchronized void release(){if(running)try{stopAndCommit();}catch(Exception ignored){}io.release();}
 private static void writeEmptyHeader(File f,int rate)throws IOException{try(RandomAccessFile r=new RandomAccessFile(f,"rw")){r.setLength(0);header(r,rate,0);}}
 private static void patchHeader(File f,int rate,long frames)throws IOException{try(RandomAccessFile r=new RandomAccessFile(f,"rw")){header(r,rate,frames*2L);}}
 private static void header(RandomAccessFile r,int rate,long data)throws IOException{r.seek(0);ascii(r,"RIFF");le32(r,36+data);ascii(r,"WAVEfmt ");le32(r,16);le16(r,1);le16(r,1);le32(r,rate);le32(r,rate*2L);le16(r,2);le16(r,16);ascii(r,"data");le32(r,data);}
 private static void ascii(RandomAccessFile r,String s)throws IOException{r.write(s.getBytes("US-ASCII"));}
 private static void le16(RandomAccessFile r,int v)throws IOException{r.write(v&255);r.write((v>>>8)&255);}
 private static void le32(RandomAccessFile r,long v)throws IOException{r.write((int)(v&255));r.write((int)((v>>>8)&255));r.write((int)((v>>>16)&255));r.write((int)((v>>>24)&255));}
}

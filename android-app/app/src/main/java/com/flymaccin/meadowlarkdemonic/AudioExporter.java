package com.flymaccin.meadowlarkdemonic;

import java.io.*;

/** Offline stereo PCM16 WAV bounce using the exact canonical AudioRuntime render path. */
public final class AudioExporter {
    private final DemonicProject project;
    private final AudioRuntime runtime;

    public AudioExporter(DemonicProject project,AudioRuntime.Decoder decoder){
        if(project==null||decoder==null)throw new IllegalArgumentException();
        this.project=project;this.runtime=new AudioRuntime(project,decoder);
    }

    public File exportWav(File output,long startFrame,long endFrame) throws IOException{
        if(output==null||startFrame<0||endFrame<=startFrame)throw new IllegalArgumentException("range");
        File parent=output.getParentFile();if(parent!=null&&!parent.exists()&&!parent.mkdirs())throw new IOException("Cannot create export directory");
        int sampleRate=project.transport.sampleRate();
        long frames=endFrame-startFrame;
        long dataBytes=frames*4L;
        if(dataBytes>0xffffffffL)throw new IOException("WAV export exceeds RIFF limit");
        try(RandomAccessFile out=new RandomAccessFile(output,"rw")){
            out.setLength(0);writeHeader(out,sampleRate,dataBytes);
            final int block=4096;
            long at=startFrame;
            while(at<endFrame){
                int count=(int)Math.min(block,endFrame-at);
                short[] pcm=runtime.render(at,count);
                for(short s:pcm){out.write(s&255);out.write((s>>>8)&255);}
                at+=count;
            }
        }
        return output;
    }

    public File exportProject(File output) throws IOException{
        long end=0;for(Clip c:project.clips())end=Math.max(end,c.startFrame+c.lengthFrames);
        if(end<=0)throw new IOException("Project has no exportable timeline range");
        return exportWav(output,0,end);
    }

    public java.util.List<File> exportTrackStems(File directory,long startFrame,long endFrame) throws IOException{
        if(directory==null||(!directory.exists()&&!directory.mkdirs()))throw new IOException("Cannot create stem directory");
        java.util.ArrayList<File> files=new java.util.ArrayList<>();
        java.util.ArrayList<AudioGraph.Node> channels=new java.util.ArrayList<>();
        java.util.ArrayList<Double> solos=new java.util.ArrayList<>();
        for(Track t:project.tracks()){AudioGraph.Node n=project.audioGraph.node(t.graphNodeId);if(n!=null){channels.add(n);solos.add(n.get("solo",0));n.set("solo",0);}}
        try{
            int index=1;
            for(Track target:project.tracks()){
                if(target.kind!=Track.Kind.AUDIO)continue;
                AudioGraph.Node tn=project.audioGraph.node(target.graphNodeId);if(tn==null)continue;
                for(AudioGraph.Node n:channels)n.set("solo",n==tn?1:0);
                String safe=target.name.replaceAll("[^A-Za-z0-9._-]+","_");
                File out=new File(directory,String.format(java.util.Locale.US,"%02d_%s.wav",index++,safe));
                exportWav(out,startFrame,endFrame);files.add(out);
            }
        }finally{for(int i=0;i<channels.size();i++)channels.get(i).set("solo",solos.get(i));}
        return files;
    }

    private static void writeHeader(RandomAccessFile out,int rate,long data) throws IOException{
        ascii(out,"RIFF");le32(out,36+data);ascii(out,"WAVEfmt ");le32(out,16);le16(out,1);le16(out,2);
        le32(out,rate);le32(out,rate*4L);le16(out,4);le16(out,16);ascii(out,"data");le32(out,data);
    }
    private static void ascii(RandomAccessFile out,String s)throws IOException{out.write(s.getBytes("US-ASCII"));}
    private static void le16(RandomAccessFile out,int v)throws IOException{out.write(v&255);out.write((v>>>8)&255);}
    private static void le32(RandomAccessFile out,long v)throws IOException{out.write((int)(v&255));out.write((int)((v>>>8)&255));out.write((int)((v>>>16)&255));out.write((int)((v>>>24)&255));}
}

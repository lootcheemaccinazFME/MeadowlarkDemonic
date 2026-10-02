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

    private static void writeHeader(RandomAccessFile out,int rate,long data) throws IOException{
        ascii(out,"RIFF");le32(out,36+data);ascii(out,"WAVEfmt ");le32(out,16);le16(out,1);le16(out,2);
        le32(out,rate);le32(out,rate*4L);le16(out,4);le16(out,16);ascii(out,"data");le32(out,data);
    }
    private static void ascii(RandomAccessFile out,String s)throws IOException{out.write(s.getBytes("US-ASCII"));}
    private static void le16(RandomAccessFile out,int v)throws IOException{out.write(v&255);out.write((v>>>8)&255);}
    private static void le32(RandomAccessFile out,long v)throws IOException{out.write((int)(v&255));out.write((int)((v>>>8)&255));out.write((int)((v>>>16)&255));out.write((int)((v>>>24)&255));}
}

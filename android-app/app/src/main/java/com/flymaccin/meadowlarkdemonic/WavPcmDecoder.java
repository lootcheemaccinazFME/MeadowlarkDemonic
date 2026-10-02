package com.flymaccin.meadowlarkdemonic;

import android.content.Context;
import android.net.Uri;
import java.io.*;

/** PCM WAV decoder for AudioRuntime. Supports 16-bit mono/stereo WAV assets. */
public final class WavPcmDecoder implements AudioRuntime.Decoder {
    private final Context context;
    public WavPcmDecoder(Context context){this.context=context.getApplicationContext();}

    @Override public int decode(Asset asset,long sourceFrame,long frames,short[] out){
        if(asset==null||asset.kind!=Asset.Kind.AUDIO||out==null)return 0;
        try(InputStream raw=context.getContentResolver().openInputStream(Uri.parse(asset.uri));
            DataInputStream in=new DataInputStream(new BufferedInputStream(raw))){
            Header h=header(in);
            long byteOffset=sourceFrame*h.channels*2L;
            skipFully(in,byteOffset);
            int wanted=(int)Math.min(frames,Integer.MAX_VALUE),done=0;
            for(;done<wanted;done++){
                int l=read16(in);if(l==Integer.MIN_VALUE)break;
                int r=h.channels==2?read16(in):l;if(r==Integer.MIN_VALUE)break;
                out[done*2]=(short)l;out[done*2+1]=(short)r;
            }
            return done;
        }catch(Exception ignored){return 0;}
    }

    private static Header header(DataInputStream in)throws IOException{
        byte[] riff=new byte[12];in.readFully(riff);
        if(riff[0]!='R'||riff[1]!='I'||riff[2]!='F'||riff[3]!='F'||riff[8]!='W'||riff[9]!='A'||riff[10]!='V'||riff[11]!='E')throw new IOException("Not WAV");
        int channels=0,bits=0;
        while(true){byte[] id=new byte[4];in.readFully(id);int size=read32(in);
            String s=new String(id,"US-ASCII");
            if("fmt ".equals(s)){int format=read16u(in);channels=read16u(in);read32(in);read32(in);read16u(in);bits=read16u(in);skipFully(in,size-16);if(format!=1||bits!=16||(channels!=1&&channels!=2))throw new IOException("Unsupported WAV");}
            else if("data".equals(s)){if(channels==0)throw new IOException("Missing fmt");return new Header(channels);}
            else skipFully(in,size+(size&1));
        }
    }
    private static int read16(DataInputStream in)throws IOException{int a=in.read(),b=in.read();if(a<0||b<0)return Integer.MIN_VALUE;return (short)(a|(b<<8));}
    private static int read16u(DataInputStream in)throws IOException{int a=in.readUnsignedByte(),b=in.readUnsignedByte();return a|(b<<8);}
    private static int read32(DataInputStream in)throws IOException{return in.readUnsignedByte()|(in.readUnsignedByte()<<8)|(in.readUnsignedByte()<<16)|(in.readUnsignedByte()<<24);}
    private static void skipFully(InputStream in,long n)throws IOException{while(n>0){long s=in.skip(n);if(s<=0){if(in.read()<0)throw new EOFException();s=1;}n-=s;}}
    private static final class Header{final int channels;Header(int channels){this.channels=channels;}}
}

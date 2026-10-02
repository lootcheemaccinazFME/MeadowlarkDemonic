package com.flymaccin.meadowlarkdemonic;

import android.content.Context;
import java.io.*;
import java.nio.charset.StandardCharsets;

/** Crash-safe active-project snapshots. DemonicProject JSON remains the sole project state authority. */
public final class ProjectRecovery {
    private static final String DIR="projects", ACTIVE="active.demonic", RECOVERY="active.recovery";
    private ProjectRecovery(){}

    public static void save(Context context,DemonicProject project) throws IOException{
        File dir=dir(context),active=new File(dir,ACTIVE),recovery=new File(dir,RECOVERY),temp=new File(dir,ACTIVE+".tmp");
        String json=project.toJson().toString();
        write(temp,json);
        if(active.exists())copy(active,recovery);
        if(active.exists()&&!active.delete())throw new IOException("Cannot replace active project");
        if(!temp.renameTo(active)){copy(temp,active);if(!temp.delete())temp.deleteOnExit();}
    }

    public static DemonicProject load(Context context){
        File dir=dir(context);
        DemonicProject p=readProject(new File(dir,ACTIVE));
        if(p!=null)return p;
        return readProject(new File(dir,RECOVERY));
    }

    public static boolean hasRecovery(Context context){return new File(dir(context),RECOVERY).isFile();}
    public static DemonicProject recover(Context context){return readProject(new File(dir(context),RECOVERY));}

    private static File dir(Context c){File d=new File(c.getFilesDir(),DIR);if(!d.exists())d.mkdirs();return d;}
    private static DemonicProject readProject(File f){if(!f.isFile())return null;try{return DemonicProject.fromJson(read(f));}catch(Exception ignored){return null;}}
    private static String read(File f)throws IOException{ByteArrayOutputStream out=new ByteArrayOutputStream();try(InputStream in=new FileInputStream(f)){byte[] b=new byte[8192];int n;while((n=in.read(b))!=-1)out.write(b,0,n);}return new String(out.toByteArray(),StandardCharsets.UTF_8);}
    private static void write(File f,String s)throws IOException{try(FileOutputStream out=new FileOutputStream(f)){out.write(s.getBytes(StandardCharsets.UTF_8));out.flush();out.getFD().sync();}}
    private static void copy(File from,File to)throws IOException{try(InputStream in=new FileInputStream(from);FileOutputStream out=new FileOutputStream(to)){byte[] b=new byte[8192];int n;while((n=in.read(b))!=-1)out.write(b,0,n);out.flush();out.getFD().sync();}}
}

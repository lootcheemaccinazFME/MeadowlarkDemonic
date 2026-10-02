package com.flymaccin.meadowlarkdemonic;

import android.content.Context;
import java.io.*;
import java.net.*;

/** Network/file execution boundary for canonical DemonicDownloader jobs. */
public final class AndroidDownloadWorker {
    private final Context context;
    private final DemonicProject project;

    public AndroidDownloadWorker(Context context,DemonicProject project){
        if(context==null||project==null)throw new IllegalArgumentException();
        this.context=context.getApplicationContext();this.project=project;
    }

    public Asset execute(String jobId) throws IOException {
        DemonicDownloader.Job job=project.downloader.job(jobId);
        if(job==null)throw new IllegalArgumentException("Unknown download "+jobId);
        URLConnection connection=new URL(job.url).openConnection();
        connection.setConnectTimeout(15000);connection.setReadTimeout(30000);
        long total=connection.getContentLengthLong();
        File dir=new File(context.getFilesDir(),"downloads");
        if(!dir.exists()&&!dir.mkdirs())throw new IOException("Cannot create download directory");
        File out=new File(dir,safeName(job.name)+"-"+job.id);
        long done=0;
        try(InputStream in=new BufferedInputStream(connection.getInputStream());OutputStream os=new BufferedOutputStream(new FileOutputStream(out))){
            byte[] buffer=new byte[64*1024];int n;
            while((n=in.read(buffer))!=-1){
                if(job.state==DemonicDownloader.State.CANCELLED)throw new IOException("Download cancelled");
                while(job.state==DemonicDownloader.State.PAUSED){
                    try{Thread.sleep(100);}catch(InterruptedException e){Thread.currentThread().interrupt();throw new IOException("Download interrupted",e);}
                }
                os.write(buffer,0,n);done+=n;project.downloader.downloading(jobId,done,total);
            }
        }catch(IOException e){if(job.state!=DemonicDownloader.State.CANCELLED)project.downloader.fail(jobId,e.getMessage());throw e;}
        return project.downloader.complete(jobId,UriCompat.file(out));
    }

    private static String safeName(String value){String s=value==null?"download":value.replaceAll("[^A-Za-z0-9._-]","_");return s.isEmpty()?"download":s;}
    private static final class UriCompat{static String file(File f){return android.net.Uri.fromFile(f).toString();}}
}
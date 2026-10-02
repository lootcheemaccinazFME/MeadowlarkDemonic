package com.flymaccin.meadowlarkdemonic;

import android.webkit.*;
import android.graphics.Bitmap;

/** Android WebView adapter for canonical DemonicBrowser navigation and Downloader handoff. */
public final class DemonicWebViewController {
    private final DemonicProject project;
    private final WebView webView;

    public DemonicWebViewController(DemonicProject project,WebView webView){
        if(project==null||webView==null)throw new IllegalArgumentException();
        this.project=project;this.webView=webView;
        WebSettings s=webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override public void onPageStarted(WebView view,String url,Bitmap favicon){
                if(url!=null&&!url.equals(project.browser.currentUrl()))project.browser.navigate(url);
            }
        });
        webView.setDownloadListener((url,userAgent,contentDisposition,mimetype,contentLength)->{
            Asset.Kind kind=kindForMime(mimetype);
            String name=URLUtil.guessFileName(url,contentDisposition,mimetype);
            project.downloader.enqueue(url,kind,name);
        });
    }

    public void navigate(String url){project.browser.navigate(url);webView.loadUrl(url);}
    public boolean back(){if(!webView.canGoBack())return false;webView.goBack();return true;}
    public boolean forward(){if(!webView.canGoForward())return false;webView.goForward();return true;}
    public void reload(){webView.reload();}
    public void stop(){webView.stopLoading();}
    public void bookmarkCurrent(){String url=webView.getUrl();if(url!=null)project.browser.bookmark(url);}

    private static Asset.Kind kindForMime(String mime){
        if(mime==null)return Asset.Kind.VIDEO;
        String m=mime.toLowerCase();
        if(m.startsWith("audio/"))return Asset.Kind.AUDIO;
        if(m.startsWith("image/"))return Asset.Kind.IMAGE;
        if(m.contains("midi"))return Asset.Kind.MIDI;
        return Asset.Kind.VIDEO;
    }
}

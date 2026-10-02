package com.flymaccin.meadowlarkdemonic;

import java.util.*;

/** Browser workspace state. Discovery only; downloaded/imported media belongs to DemonicProject. */
public final class DemonicBrowser {
    private final DemonicProject project;
    private final ArrayList<String> history = new ArrayList<>();
    private final LinkedHashSet<String> bookmarks = new LinkedHashSet<>();
    private String currentUrl = "about:blank";

    public DemonicBrowser(DemonicProject project) { this.project = project; }

    public String currentUrl() { return currentUrl; }
    public List<String> history() { return Collections.unmodifiableList(history); }
    public Set<String> bookmarks() { return Collections.unmodifiableSet(bookmarks); }

    public void navigate(String url) {
        if (url == null || url.trim().isEmpty()) throw new IllegalArgumentException("url");
        currentUrl = url.trim();
        history.add(currentUrl);
    }

    public void bookmark(String url) {
        if (url == null || url.trim().isEmpty()) throw new IllegalArgumentException("url");
        bookmarks.add(url.trim());
    }

    public void removeBookmark(String url) { bookmarks.remove(url); }

    /** Directly register a discovered remote media URL when explicit import is desired. */
    public Asset importRemote(Asset.Kind kind, String url, String name) {
        if (kind == null) throw new IllegalArgumentException("kind");
        if (url == null || url.trim().isEmpty()) throw new IllegalArgumentException("url");
        Asset asset = new Asset(kind, url.trim(), name == null ? "Browser Asset" : name);
        return project.addAsset(asset);
    }
}

package com.flymaccin.meadowlarkdemonic;

import java.util.*;

/** Download queue and ingestion authority. Completed files become canonical project Assets. */
public final class DemonicDownloader {
    public enum State { QUEUED, DOWNLOADING, PAUSED, COMPLETE, FAILED, CANCELLED }

    public static final class Job {
        public final String id = UUID.randomUUID().toString();
        public final String url;
        public final Asset.Kind kind;
        public final String name;
        public State state = State.QUEUED;
        public long bytesDownloaded;
        public long totalBytes = -1;
        public String localUri;
        public String error;

        Job(String url, Asset.Kind kind, String name) {
            this.url = url; this.kind = kind; this.name = name;
        }

        public double progress() {
            return totalBytes <= 0 ? -1 : Math.min(1.0, (double) bytesDownloaded / totalBytes);
        }
    }

    private final DemonicProject project;
    private final LinkedHashMap<String, Job> jobs = new LinkedHashMap<>();

    public DemonicDownloader(DemonicProject project) { this.project = project; }

    public Job enqueue(String url, Asset.Kind kind, String name) {
        if (url == null || url.trim().isEmpty()) throw new IllegalArgumentException("url");
        if (kind == null) throw new IllegalArgumentException("kind");
        Job j = new Job(url.trim(), kind, name == null ? "Download" : name);
        jobs.put(j.id, j);
        return j;
    }

    public Collection<Job> jobs() { return Collections.unmodifiableCollection(jobs.values()); }
    public Job job(String id) { return jobs.get(id); }

    public void downloading(String id, long bytes, long total) {
        Job j = require(id); j.state = State.DOWNLOADING; j.bytesDownloaded = Math.max(0, bytes); j.totalBytes = total;
    }

    public void pause(String id) { require(id).state = State.PAUSED; }
    public void resume(String id) { require(id).state = State.QUEUED; }
    public void cancel(String id) { require(id).state = State.CANCELLED; }

    public Asset complete(String id, String localUri) {
        Job j = require(id);
        if (localUri == null || localUri.trim().isEmpty()) throw new IllegalArgumentException("localUri");
        j.localUri = localUri.trim(); j.state = State.COMPLETE;
        Asset asset = new Asset(j.kind, j.localUri, j.name);
        return project.addAsset(asset);
    }

    public void fail(String id, String error) { Job j=require(id);j.state=State.FAILED;j.error=error; }

    private Job require(String id) {
        Job j = jobs.get(id);
        if (j == null) throw new IllegalArgumentException("Unknown download " + id);
        return j;
    }
}

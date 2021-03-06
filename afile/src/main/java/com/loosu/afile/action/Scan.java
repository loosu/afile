package com.loosu.afile.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loosu.afile.AFileUtils;
import com.loosu.afile.interfaces.IBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Scan extends Action {

    private final File[] sources;
    private final File dst;
    @Nullable
    private Result result;

    private Scan(@NonNull File[] sources, @NonNull File dst, @Nullable Listener listener) {
        AFileUtils.requireNonNull(sources, "sources is null");
        AFileUtils.requireNonNull(dst, "dst is null");
        this.sources = sources;
        this.dst = dst;
        setListener(listener);
    }

    @NonNull
    public final synchronized Result scan() {
        if (!isScanned()) {
            run();
        }
        return result;
    }

    public boolean isScanned() {
        return result != null;
    }

    @NonNull
    public Result getResult() {
        return AFileUtils.requireNonNull(result, "did not scanned");
    }

    @Override
    public final synchronized void run() {
        if (isScanned()) {
            return;
        }

        try {
            checkCanceled();
            notifyOnPrepare();
            notifyOnStart();

            final Map<File, File> dirsCache = new HashMap<>();
            final Map<File, File> filesCache = new HashMap<>();
            long totalSize = 0;

            for (File source : sources) {
                totalSize += recursiveScan(source, dst, dirsCache, filesCache);
            }

            result = new Result(dirsCache, filesCache, totalSize);

            notifyOnEnd();
        } catch (Throwable throwable) {
            // notify - error
            notifyOnError(throwable);
            throw new RuntimeException(throwable);
        }
    }

    private long recursiveScan(@NonNull File in, @NonNull File parentDir,
                               @NonNull Map<File, File> dirsCache,
                               @NonNull Map<File, File> filesCache) {

        // check if canceled.
        checkCanceled();

        // notify - progress
        File out = new File(parentDir, in.getName());
        notifyOnProgress(in, out);

        if (in.isDirectory()) {
            dirsCache.put(in, out);
            final File[] subFiles = in.listFiles();
            long size = 0;
            if (subFiles != null) {
                for (File subFile : subFiles) {
                    size += recursiveScan(subFile, out, dirsCache, filesCache);
                }
            }
            return size;

        } else {
            filesCache.put(in, out);
            return in.length();
        }
    }

    @Override
    public String toString() {
        return "Scan@" + hashCode();
    }

    public static final class Builder implements IBuilder<Scan> {

        private final Collection<File> sources = new ArrayList<>();

        @Nullable
        private Listener listener;

        @Nullable
        private File dst;

        public Builder() {
        }

        public Builder append(@NonNull String file) {
            return append(new File(file));
        }

        public Builder append(@NonNull File file) {
            if (!sources.contains(file)) {
                sources.add(file);
            }
            return this;
        }

        public Builder setDst(@NonNull String file) {
            return setDst(new File(file));
        }

        public Builder setDst(@NonNull File dst) {
            this.dst = dst;
            return this;
        }

        public Builder setListener(@Nullable Listener listener) {
            this.listener = listener;
            return this;
        }

        @Override
        public Scan build() {
            return new Scan(
                    sources.toArray(new File[0]),
                    dst == null ? new File("") : dst,
                    listener);
        }

        public Result start() {
            return build().scan();
        }
    }

    public static final class Result {
        final Map<File, File> dirs;
        final Map<File, File> files;
        private final long totalSize;

        public Result(@NonNull Map<File, File> dirs, @NonNull Map<File, File> files, long totalSize) {
            AFileUtils.requireNonNull(dirs, "dir is null");
            AFileUtils.requireNonNull(files, "files is null");
            this.dirs = dirs;
            this.files = files;
            this.totalSize = totalSize;
        }

        public int getDirSize() {
            return dirs.size();
        }

        public int getFileSize() {
            return files.size();
        }

        public long getTotalSize() {
            return totalSize;
        }

        @Override
        public String toString() {
            return "FileSources{" +
                    "dirs=" + getDirSize() +
                    ", files=" + getFileSize() +
                    ", totalSize=" + getTotalSize() +
                    '}';
        }
    }
}

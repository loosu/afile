package com.loosu.afile.afile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loosu.afile.afile.interfaces.IBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FileInOutScanner extends Scanner<FileInOutSources> {
    private final File[] sources;
    private final File destination;

    private FileInOutScanner(@NonNull File[] sources, @NonNull File destination, @Nullable Listener listener) {
        super(listener);
        this.sources = sources;
        this.destination = destination;
    }

    @Override
    public FileInOutSources scan() {
        try {
            checkCanceled();

            // notify - start
            notifyOnStart();

            final Map<File, File> dirsCache = new HashMap<>();
            final Map<File, File> filesCache = new HashMap<>();
            long totalSize = 0;

            for (File source : sources) {
                totalSize += recursiveScan(source, destination, dirsCache, filesCache);
            }

            // notify - end
            notifyOnEnd();

            return new FileInOutSources(dirsCache, filesCache, totalSize);
        } catch (Throwable throwable) {
            // notify - error
            notifyOnError(throwable);

            throw throwable;
        }
    }

    private long recursiveScan(@NonNull File in, @NonNull File out,
                               @NonNull Map<File, File> dirsCache,
                               @NonNull Map<File, File> filesCache) {

        // check if canceled.
        checkCanceled();

        // notify - progress
        notifyOnProgress(in);

        File newOut = new File(out, in.getName());
        if (in.isDirectory()) {
            dirsCache.put(in, newOut);
            final File[] subFiles = in.listFiles();
            long size = 0;
            if (subFiles != null) {
                for (File subFile : subFiles) {
                    size += recursiveScan(subFile, newOut, dirsCache, filesCache);
                }
            }
            return size;

        } else {
            filesCache.put(in, newOut);
            return in.length();
        }
    }

    public static final class Builder implements IBuilder<FileInOutScanner> {

        private final Collection<File> sources = new ArrayList<>();

        @Nullable
        private Listener listener;

        @Nullable
        private File destination;

        Builder() {
        }

        public FileInOutScanner.Builder append(@NonNull String file) {
            return append(new File(file));
        }

        public FileInOutScanner.Builder append(@NonNull File file) {
            if (!sources.contains(file)) {
                sources.add(file);
            }
            return this;
        }

        public FileInOutScanner.Builder setDst(@NonNull File file) {
            destination = file;
            return this;
        }

        public FileInOutScanner.Builder setListener(@Nullable Listener listener) {
            this.listener = listener;
            return this;
        }

        @Override
        public FileInOutScanner build() {
            AFileUtils.requireNonNull(destination, "dst is null");
            return new FileInOutScanner(sources.toArray(new File[0]), destination, listener);
        }

        public FileInOutSources scan() {
            return build().scan();
        }
    }
}

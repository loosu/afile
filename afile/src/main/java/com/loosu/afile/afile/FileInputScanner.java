package com.loosu.afile.afile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loosu.afile.afile.excepton.CancelException;
import com.loosu.afile.afile.interfaces.IBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class FileInputScanner extends Scanner<FileInputSources> {
    private final File[] sources;

    private FileInputScanner(@NonNull File[] sources, @Nullable Listener listener) {
        super(listener);
        this.sources = sources;
    }

    @NonNull
    @Override
    public synchronized FileInputSources scan() throws CancelException {
        try {
            checkCanceled();

            // notify - start
            notifyOnStart();

            final Collection<File> dirsCache = new ArrayList<>();
            final Collection<File> filesCache = new ArrayList<>();
            long totalSize = 0;

            for (File source : sources) {
                totalSize += recursiveScan(source, dirsCache, filesCache);
            }

            // notify - end
            notifyOnEnd();

            return new FileInputSources(dirsCache, filesCache, totalSize);
        } catch (Throwable throwable) {
            // notify - error
            notifyOnError(throwable);

            throw throwable;
        }
    }

    private long recursiveScan(@NonNull File file,
                               @NonNull Collection<File> dirsCache,
                               @NonNull Collection<File> filesCache) {

        // check if canceled.
        checkCanceled();

        // notify - progress
        notifyOnProgress(file);

        if (file.isDirectory()) {
            dirsCache.add(file);
            final File[] subFiles = file.listFiles();
            long size = 0;
            if (subFiles != null) {
                for (File subFile : subFiles) {
                    size += recursiveScan(subFile, dirsCache, filesCache);
                }
            }
            return size;

        } else {
            filesCache.add(file);
            return file.length();
        }
    }

    public static final class Builder implements IBuilder<FileInputScanner> {

        private final Collection<File> sources = new ArrayList<>();

        private Listener listener;

        Builder() {
        }

        public FileInputScanner.Builder append(@NonNull String file) {
            return append(new File(file));
        }

        public FileInputScanner.Builder append(@NonNull File file) {
            if (!sources.contains(file)) {
                sources.add(file);
            }
            return this;
        }

        public FileInputScanner.Builder setListener(@Nullable Listener listener) {
            this.listener = listener;
            return this;
        }

        @Override
        public FileInputScanner build() {
            return new FileInputScanner(sources.toArray(new File[0]), listener);
        }

        public FileInputSources scan() {
            return build().scan();
        }
    }
}

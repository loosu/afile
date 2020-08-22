package com.loosu.afile.afile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loosu.afile.afile.excepton.CancelException;
import com.loosu.afile.afile.interfaces.IBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public final class FileScanner extends Canceller {
    private final File[] sources;

    @Nullable
    private Listener listener;

    private FileScanner(@NonNull File[] sources, @Nullable Listener listener) {
        this.sources = sources;
        this.listener = listener;
    }

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    @NonNull
    public FileSources scan() throws CancelException {
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

            return new FileSources(dirsCache, filesCache, totalSize);
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

    private void notifyOnStart() {
        if (listener != null) {
            listener.onStart(this);
        }
    }

    private void notifyOnEnd() {
        if (listener != null) {
            listener.onEnd(this);
        }
    }

    private void notifyOnProgress(@NonNull File file) {
        if (listener != null) {
            listener.onProgress(this, file);
        }
    }

    private void notifyOnError(@NonNull Throwable throwable) {
        if (listener != null) {
            listener.onError(this, throwable);
        }
    }

    public interface Listener {

        public void onStart(@NonNull FileScanner scanner);

        public void onEnd(@NonNull FileScanner scanner);

        public void onProgress(@NonNull FileScanner scanner, @NonNull File file);

        public void onError(@NonNull FileScanner scanner, @NonNull Throwable throwable);
    }

    public static class AdapterListener implements Listener{

        @Override
        public void onStart(@NonNull FileScanner scanner) {

        }

        @Override
        public void onEnd(@NonNull FileScanner scanner) {

        }

        @Override
        public void onProgress(@NonNull FileScanner scanner, @NonNull File file) {

        }

        @Override
        public void onError(@NonNull FileScanner scanner, @NonNull Throwable throwable) {

        }
    }

    public static final class Builder implements IBuilder<FileScanner> {

        private final Collection<File> sources = new ArrayList<>();

        private Listener listener;

        Builder() {
        }

        public FileScanner.Builder append(@NonNull String file) {
            return append(new File(file));
        }

        public FileScanner.Builder append(@NonNull File file) {
            if (!sources.contains(file)) {
                sources.add(file);
            }
            return this;
        }

        public FileScanner.Builder setListener(@Nullable Listener listener) {
            this.listener = listener;
            return this;
        }

        @Override
        public FileScanner build() {
            return new FileScanner(sources.toArray(new File[0]), listener);
        }

        public FileSources scan() {
            return build().scan();
        }
    }
}

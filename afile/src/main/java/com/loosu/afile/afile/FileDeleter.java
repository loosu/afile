package com.loosu.afile.afile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loosu.afile.afile.interfaces.IBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public final class FileDeleter extends Canceller {

    private final File[] sources;

    @Nullable
    private Listener listener;

    private FileDeleter(@NonNull File[] sources, @Nullable Listener listener) {
        this.sources = sources;
        this.listener = listener;
    }

    /**
     * 1. scan all sources file, group dir and file。
     * 2. delete files.
     * 3. delete dirs.
     *
     * @return true if all files delete success.
     */
    public boolean delete() {
        try {
            checkCanceled();

            notifyOnStart();

            // 1. scan
            FileScanner.Builder scanner = new FileScanner.Builder();
            for (File source : sources) {
                scanner.append(source);
            }
            FileSources scanResult = scanner.setListener(new FileScanner.AdapterListener() {
                @Override
                public void onProgress(@NonNull FileScanner scanner, @NonNull File file) {
                    notifyOnScan(file);
                }
            }).scan();

            notifyOnScanResult(scanResult);

            // 2. delete files
            boolean result = true;
            File[] files = scanResult.files;
            for (File file : files) {
                notifyOnDelete(file);
                if (!checkCancelAndDelete(file)) {
                    result = false;
                }
            }

            // 3. delete dirs
            File[] dirs = scanResult.dirs;
            for (File dir : dirs) {
                notifyOnDelete(dir);
                if (!checkCancelAndDelete(dir)) {
                    result = false;
                }
            }

            notifyOnEnd();

            return result;
        } catch (Throwable throwable) {
            notifyOnError(throwable);
            throw throwable;
        }
    }

    private boolean checkCancelAndDelete(@NonNull File file) {
        checkCanceled();
        return file.delete();
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

    private void notifyOnScan(@NonNull File file) {
        if (listener != null) {
            listener.onScan(this, file);
        }
    }

    private void notifyOnDelete(@NonNull File file) {
        if (listener != null) {
            listener.onDelete(this, file);
        }
    }

    private void notifyOnScanResult(@NonNull FileSources sources) {
        if (listener != null) {
            listener.onScanResult(this, sources);
        }
    }

    private void notifyOnError(@NonNull Throwable throwable) {
        if (listener != null) {
            listener.onError(this, throwable);
        }
    }

    public interface Listener {

        public void onStart(@NonNull FileDeleter deleter);

        public void onEnd(@NonNull FileDeleter deleter);

        public void onScan(@NonNull FileDeleter deleter, @NonNull File file);

        public void onScanResult(@NonNull FileDeleter deleter, @NonNull FileSources sources);

        public void onDelete(@NonNull FileDeleter deleter, @NonNull File file);

        public void onError(@NonNull FileDeleter deleter, @NonNull Throwable throwable);
    }

    public static class AdapterListener implements Listener {

        @Override
        public void onStart(@NonNull FileDeleter deleter) {

        }

        @Override
        public void onEnd(@NonNull FileDeleter deleter) {

        }

        @Override
        public void onScan(@NonNull FileDeleter deleter, @NonNull File file) {

        }

        @Override
        public void onScanResult(@NonNull FileDeleter deleter, @NonNull FileSources sources) {

        }

        @Override
        public void onDelete(@NonNull FileDeleter deleter, @NonNull File file) {

        }

        @Override
        public void onError(@NonNull FileDeleter deleter, @NonNull Throwable throwable) {

        }
    }

    public static final class Builder implements IBuilder<FileDeleter> {

        private final Collection<File> sources = new ArrayList<>();

        private Listener listener;

        Builder() {
        }

        public FileDeleter.Builder append(@NonNull String file) {
            return append(new File(file));
        }

        public FileDeleter.Builder append(@NonNull File file) {
            if (!sources.contains(file)) {
                sources.add(file);
            }
            return this;
        }

        public FileDeleter.Builder setListener(@Nullable Listener listener) {
            this.listener = listener;
            return this;
        }

        @Override
        public FileDeleter build() {
            return new FileDeleter(sources.toArray(new File[0]), listener);
        }

        public boolean delete() {
            return build().delete();
        }
    }
}

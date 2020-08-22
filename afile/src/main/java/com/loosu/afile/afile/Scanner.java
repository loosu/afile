package com.loosu.afile.afile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loosu.afile.afile.interfaces.IScanner;

import java.io.File;

abstract public class Scanner<T> extends Canceller implements IScanner<T> {
    @Nullable
    private Listener listener;

    protected Scanner(@Nullable Listener listener) {
        this.listener = listener;
    }

    protected final void notifyOnStart() {
        if (listener != null) {
            listener.onStart(this);
        }
    }

    protected final void notifyOnEnd() {
        if (listener != null) {
            listener.onEnd(this);
        }
    }

    protected final void notifyOnProgress(@NonNull File file) {
        if (listener != null) {
            listener.onProgress(this, file);
        }
    }

    protected final void notifyOnError(@NonNull Throwable throwable) {
        if (listener != null) {
            listener.onError(this, throwable);
        }
    }

    public interface Listener {

        public void onStart(@NonNull Scanner scanner);

        public void onEnd(@NonNull Scanner scanner);

        public void onProgress(@NonNull Scanner scanner, @NonNull File file);

        public void onError(@NonNull Scanner scanner, @NonNull Throwable throwable);
    }

    public static class AdapterListener implements Listener {

        @Override
        public void onStart(@NonNull Scanner scanner) {

        }

        @Override
        public void onEnd(@NonNull Scanner scanner) {

        }

        @Override
        public void onProgress(@NonNull Scanner scanner, @NonNull File file) {

        }

        @Override
        public void onError(@NonNull Scanner scanner, @NonNull Throwable throwable) {

        }
    }
}

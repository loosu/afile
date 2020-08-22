package com.loosu.afile.afile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

abstract public class Copier extends Canceller {
    @Nullable
    private Listener listener;

    protected Copier(@Nullable Listener listener) {
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

    protected void notifyOnScan(@NonNull File file) {
        if (listener != null) {
            listener.onScan(this, file);
        }
    }

    protected void notifyOnScanResult(@NonNull Sources sources) {
        if (listener != null) {
            listener.onScanResult(this, sources);
        }
    }

    protected final void notifyOnCopy(@NonNull File file) {
        if (listener != null) {
            listener.onCopy(this, file);
        }
    }

    protected final void notifyOnError(@NonNull Throwable throwable) {
        if (listener != null) {
            listener.onError(this, throwable);
        }
    }

    public interface Listener {

        public void onStart(@NonNull Copier copier);

        public void onEnd(@NonNull Copier copier);

        public void onScan(@NonNull Copier copier, @NonNull File file);

        public void onScanResult(@NonNull Copier copier, @NonNull Sources sources);

        public void onCopy(@NonNull Copier copier, @NonNull File file);

        public void onError(@NonNull Copier copier, @NonNull Throwable throwable);
    }

    public static class AdapterListener implements Listener {

        @Override
        public void onStart(@NonNull Copier copier) {

        }

        @Override
        public void onEnd(@NonNull Copier copier) {

        }

        @Override
        public void onScan(@NonNull Copier copier, @NonNull File file) {

        }

        @Override
        public void onScanResult(@NonNull Copier copier, @NonNull Sources sources) {

        }

        @Override
        public void onCopy(@NonNull Copier copier, @NonNull File file) {

        }

        @Override
        public void onError(@NonNull Copier copier, @NonNull Throwable throwable) {

        }
    }
}

package com.loosu.afile.afile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loosu.afile.afile.excepton.CancelException;

import java.io.File;

public abstract class Zipper extends Canceller {
    @Nullable
    private Listener listener;

    protected Zipper(@Nullable Listener listener) {
        this.listener = listener;
    }

    public abstract boolean zipAs(@NonNull File dst) throws CancelException;

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

    protected final void notifyOnZip(@NonNull File file) {
        if (listener != null) {
            listener.onZip(this, file);
        }
    }

    protected final void notifyOnError(@NonNull Throwable throwable) {
        if (listener != null) {
            listener.onError(this, throwable);
        }
    }

    public interface Listener {

        public void onStart(@NonNull Zipper zipper);

        public void onEnd(@NonNull Zipper zipper);

        public void onScan(@NonNull Zipper zipper, @NonNull File file);

        public void onScanResult(@NonNull Zipper zipper, @NonNull Sources sources);

        public void onZip(@NonNull Zipper zipper, @NonNull File file);

        public void onError(@NonNull Zipper zipper, @NonNull Throwable throwable);
    }

    public static class AdapterListener implements Listener {

        @Override
        public void onStart(@NonNull Zipper zipper) {

        }

        @Override
        public void onEnd(@NonNull Zipper zipper) {

        }

        @Override
        public void onScan(@NonNull Zipper zipper, @NonNull File file) {

        }

        @Override
        public void onScanResult(@NonNull Zipper zipper, @NonNull Sources sources) {

        }

        @Override
        public void onZip(@NonNull Zipper zipper, @NonNull File file) {

        }

        @Override
        public void onError(@NonNull Zipper Zipper, @NonNull Throwable throwable) {

        }
    }
}

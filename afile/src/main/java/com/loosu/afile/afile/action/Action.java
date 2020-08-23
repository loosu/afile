package com.loosu.afile.afile.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loosu.afile.afile.Canceller;

import java.io.File;

/**
 * Action - base class.
 * <p>
 * AFile 规范了文件操作的流程:
 * 1. 扫描(统计源文件信息);
 * 2. 执行;
 * <p>
 * 所以, {@link Action.Listener} 的回调顺序是:
 * 1. onPrepare;
 * 2. onStart;
 * 3. onProgress;
 * 4. onEnd;
 * <p>
 * onError的回调时机在 onPrepare 和 onEnd 之间.
 */
public abstract class Action extends Canceller implements Runnable {
    @Nullable
    private Listener listener;

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    protected final void notifyOnPrepare() {
        if (listener != null) {
            listener.onPrepare(this);
        }
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

    protected final void notifyOnProgress(@Nullable File in, @Nullable File out) {
        if (listener != null) {
            listener.onProgress(this, in, out);
        }
    }

    protected final void notifyOnError(@NonNull Throwable throwable) {
        if (listener != null) {
            listener.onError(this, throwable);
        }
    }

    public interface Listener {

        public void onPrepare(@NonNull Action action);

        public void onStart(@NonNull Action action);

        public void onProgress(@NonNull Action action, @Nullable File in, @Nullable File out);

        public void onEnd(@NonNull Action action);

        public void onError(@NonNull Action action, @NonNull Throwable throwable);
    }

    public static class AdapterListener implements Listener {

        @Override
        public void onPrepare(@NonNull Action action) {

        }

        @Override
        public void onStart(@NonNull Action action) {

        }

        @Override
        public void onEnd(@NonNull Action action) {

        }

        @Override
        public void onProgress(@NonNull Action action, @Nullable File in, @Nullable File out) {

        }

        @Override
        public void onError(@NonNull Action action, @NonNull Throwable throwable) {

        }
    }
}

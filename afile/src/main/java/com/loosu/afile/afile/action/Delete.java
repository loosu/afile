package com.loosu.afile.afile.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loosu.afile.afile.AFileUtils;
import com.loosu.afile.afile.interfaces.IBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Delete extends Action {

    private final File[] sources;
    @Nullable
    private Listener scanListener;

    private Delete(@NonNull File[] sources, @Nullable Listener listener, @Nullable Listener scanListener) {
        AFileUtils.requireNonNull(sources, "sources is null");
        this.sources = sources;
        this.scanListener = scanListener;
        setListener(listener);
    }

    public void delete() {
        run();
    }

    @Override
    public final synchronized void run() {
        try {
            checkCanceled();
            notifyOnPrepare();
            // 1. 扫描所有输入文件.
            Scan.Builder scanner = new Scan.Builder();
            for (File source : sources) {
                scanner.append(source);
            }
            FileInfo scanResult = scanner.setListener(scanListener)
                    .start();

            // 2. 删除
            // 2.1 删除文件
            checkCanceled();
            notifyOnStart();
            Map<File, File> files = scanResult.files;
            for (Map.Entry<File, File> entry : files.entrySet()) {
                checkCanceled();
                File file = entry.getKey();
                notifyOnProgress(file, null);
                file.delete();
            }

            // 2.2 删除文件夹
            List<File> dirs = new ArrayList<>(scanResult.dirs.keySet());
            Collections.reverse(dirs);
            for (File dir : dirs) {
                checkCanceled();
                notifyOnProgress(dir, null);
                dir.delete();
            }

            notifyOnEnd();
        } catch (Throwable throwable) {
            notifyOnEnd();
            throw new RuntimeException(throwable);
        }
    }

    public static final class Builder implements IBuilder<Delete> {

        private final Collection<File> sources = new ArrayList<>();

        @Nullable
        private Listener listener;

        @Nullable
        private Listener scanListener;

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

        public Builder setListener(@Nullable Listener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setScanListener(@Nullable Listener scanListener) {
            this.scanListener = scanListener;
            return this;
        }

        @Override
        public Delete build() {
            return new Delete(sources.toArray(new File[0]), listener, scanListener);
        }

        public void start() {
            build().delete();
        }
    }
}

package com.loosu.afile.afile.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loosu.afile.afile.AFileUtils;
import com.loosu.afile.afile.interfaces.IBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Copy extends Action {

    private final File[] sources;
    private final File dst;
    @Nullable
    private Listener scanListener;

    private Copy(@NonNull File[] sources, @NonNull File dst, @Nullable Listener listener, @Nullable Listener scanListener) {
        AFileUtils.requireNonNull(sources, "dst is null");
        AFileUtils.requireNonNull(dst, "dst is null");
        this.sources = sources;
        this.dst = dst;
        this.scanListener = scanListener;
        setListener(listener);
    }

    public void copy() {
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
                    .setDst(dst)
                    .start();

            // 2. 拷贝
            // 2.1 拷贝文件夹
            checkCanceled();
            notifyOnStart();
            Map<File, File> dirs = scanResult.dirs;
            for (Map.Entry<File, File> entry : dirs.entrySet()) {
                File inFile = entry.getKey();
                File outFile = entry.getValue();
                checkCanceled();
                notifyOnProgress(inFile, outFile);
                entry.getValue().mkdirs();
            }

            // 2.2 拷贝文件
            Map<File, File> files = scanResult.files;
            for (Map.Entry<File, File> entry : files.entrySet()) {
                File inFile = entry.getKey();
                File outFile = entry.getValue();
                checkCanceled();
                notifyOnProgress(inFile, outFile);
                copyFile(inFile, outFile);
            }

            notifyOnEnd();
        } catch (Throwable throwable) {
            notifyOnEnd();
            throw new RuntimeException(throwable);
        }
    }

    /**
     * 文件数据的赋值.
     *
     * @param in  输入文件
     * @param out 输出文件
     * @throws IOException 可能抛出IO异常
     */
    private void copyFile(@NonNull File in, @NonNull File out) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(in);
            fos = new FileOutputStream(out);

            int len = -1;
            byte[] buff = new byte[200 * 1024];

            while ((len = fis.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }

            fos.flush();
            fos.getFD().sync();

        } finally {
            AFileUtils.close(fos);
            AFileUtils.close(fis);
        }
    }

    @Override
    public String toString() {
        return "Copy@" + hashCode();
    }

    public static final class Builder implements IBuilder<Copy> {

        private final Collection<File> sources = new ArrayList<>();

        @Nullable
        private File dst;

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

        public Builder setDst(@NonNull String dst) {
            return setDst(new File(dst));
        }

        public Builder setDst(@NonNull File dst) {
            this.dst = dst;
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
        public Copy build() {
            AFileUtils.requireNonNull(dst, "dst is null");
            return new Copy(sources.toArray(new File[0]), dst, listener, scanListener);
        }

        public void start() {
            build().copy();
        }
    }
}

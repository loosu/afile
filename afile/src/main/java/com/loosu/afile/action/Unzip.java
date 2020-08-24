package com.loosu.afile.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loosu.afile.AFileUtils;
import com.loosu.afile.interfaces.IBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class Unzip extends Action {
    private static final String TAG = "FileUnzip";

    private final File src;
    private final File dst;

    public Unzip(@NonNull File source, @NonNull File dst, @Nullable Listener listener) {
        this.src = source;
        this.dst = dst;
        setListener(listener);
    }

    public final synchronized void unzip() {
        run();
    }

    @Override
    public final synchronized void run() {
        try {
            checkCanceled();
            notifyOnPrepare();
            notifyOnStart();

            ZipInputStream zis = new ZipInputStream(new FileInputStream(src));
            ZipEntry zipEntry = null;
            while ((zipEntry = zis.getNextEntry()) != null) {
                String name = zipEntry.getName();
                File file = new File(dst, name);
                notifyOnProgress(null, file);
                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                    continue;
                }

                File parentDir = file.getParentFile();
                if (parentDir != null) {
                    parentDir.mkdirs();
                }
                file.createNewFile();

                FileOutputStream fos = new FileOutputStream(file);
                int len = -1;
                byte[] buff = new byte[200 * 1024];
                while ((len = zis.read(buff)) != -1) {
                    fos.write(buff, 0, len);
                }
                fos.close();
            }

            notifyOnEnd();
        } catch (Throwable throwable) {
            notifyOnError(throwable);
            throw new RuntimeException(throwable);
        }
    }

    public static final class Builder implements IBuilder<Unzip> {

        @Nullable
        private File source = null;
        @Nullable
        private File dst = null;
        @Nullable
        private Listener listener;

        public Builder() {
        }

        public Builder source(@NonNull String dst) {
            return source(new File(dst));
        }

        public Builder source(@NonNull File file) {
            source = file;
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

        @Override
        public Unzip build() {
            AFileUtils.requireNonNull(source, "source is null");
            AFileUtils.requireNonNull(dst, "dst is null");
            return new Unzip(source, dst, listener);
        }

        public void start() {
            build().unzip();
        }
    }
}

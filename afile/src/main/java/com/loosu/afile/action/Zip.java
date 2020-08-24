package com.loosu.afile.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loosu.afile.AFileUtils;
import com.loosu.afile.interfaces.IBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip extends Action {
    private final File[] sources;
    private final File dst;
    private final Listener scanListener;

    private Zip(@NonNull File[] sources, @NonNull File dst, @Nullable Listener listener, @Nullable Listener scanListener) {
        this.sources = sources;
        this.dst = dst;
        this.scanListener = scanListener;
        setListener(listener);
    }

    public void zip() {
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
            Scan.Result scanResult = scanner.setListener(scanListener)
                    .setDst(new File(""))
                    .start();

            checkCanceled();
            notifyOnStart();
            ZipOutputStream zos = null;
            FileInputStream fis = null;
            try {
                zos = new ZipOutputStream(new FileOutputStream(dst));
                Map<File, File> dirs = scanResult.dirs;
                for (Map.Entry<File, File> entry : dirs.entrySet()) {
                    File inFile = entry.getKey();
                    File outFile = entry.getValue();
                    notifyOnProgress(inFile, outFile);
                    ZipEntry zipEntry = new ZipEntry(outFile.getAbsolutePath() + File.separator);
                    zos.putNextEntry(zipEntry);
                    zos.closeEntry();
                }

                Map<File, File> files = scanResult.files;
                for (Map.Entry<File, File> entry : files.entrySet()) {
                    File inFile = entry.getKey();
                    File outFile = entry.getValue();
                    notifyOnProgress(inFile, outFile);

                    ZipEntry zipEntry = new ZipEntry(outFile.getAbsolutePath());
                    zos.putNextEntry(zipEntry);
                    fis = new FileInputStream(inFile);
                    copyData(fis, zos);
                    zos.closeEntry();
                    AFileUtils.close(fis);
                }
            } finally {
                AFileUtils.close(fis);
                AFileUtils.close(zos);
            }

            notifyOnEnd();
        } catch (Throwable throwable) {
            notifyOnError(throwable);
            throw new RuntimeException(throwable);
        }
    }

    private void copyData(@NonNull InputStream in, @NonNull OutputStream out) throws IOException {
        int len = -1;
        byte[] buff = new byte[200 * 1024];
        while ((len = in.read(buff)) != -1) {
            out.write(buff, 0, len);
        }
    }

    @Override
    public String toString() {
        return "Zip@" + hashCode();
    }

    public static final class Builder implements IBuilder<Zip> {

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
        public Zip build() {
            AFileUtils.requireNonNull(dst, "dst is null");
            return new Zip(sources.toArray(new File[0]), dst, listener, scanListener);
        }

        public void start() {
            build().zip();
        }
    }
}

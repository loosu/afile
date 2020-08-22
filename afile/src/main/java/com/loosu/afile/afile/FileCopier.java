package com.loosu.afile.afile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loosu.afile.afile.excepton.CancelException;
import com.loosu.afile.afile.interfaces.IBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class FileCopier extends Copier {

    private final File[] sources;

    private FileCopier(@NonNull File[] sources, @Nullable Listener listener) {
        super(listener);
        this.sources = sources;
    }

    public synchronized boolean copyTo(@NonNull File dst) throws CancelException {
        try {
            checkCanceled();

            notifyOnStart();

            // scan
            FileInOutScanner.Builder scanner = new FileInOutScanner.Builder();
            for (File source : sources) {
                scanner.append(source);
            }

            FileInOutSources scanResult = scanner.setListener(new Scanner.AdapterListener() {
                @Override
                public void onProgress(@NonNull Scanner scanner, @NonNull File file) {
                    notifyOnScan(file);
                }
            }).setDst(dst).scan();

            notifyOnScanResult(scanResult);

            // real copy
            boolean result = true;
            Map<File, File> dirs = scanResult.dirs;
            for (Map.Entry<File, File> entry : dirs.entrySet()) {
                notifyOnCopy(entry.getKey());
                if (!entry.getValue().mkdirs()) {
                    result = false;
                }
            }

            Map<File, File> files = scanResult.files;
            for (Map.Entry<File, File> entry : files.entrySet()) {
                File inFile = entry.getKey();
                File outFile = entry.getValue();
                notifyOnCopy(inFile);
                copyFile(inFile, outFile);
            }

            notifyOnEnd();

            return result;
        } catch (Throwable throwable) {
            notifyOnError(throwable);
            throw new RuntimeException(throwable);
        }
    }

    private void copyFile(@NonNull File in, @NonNull File out) throws CancelException, IOException {
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


    public static final class Builder implements IBuilder<FileCopier> {

        private final Collection<File> sources = new ArrayList<>();

        @Nullable
        private Listener listener;

        Builder() {
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

        @Override
        public FileCopier build() {
            return new FileCopier(sources.toArray(new File[0]), listener);
        }

        public boolean copyTo(@NonNull File dst) throws CancelException {
            return build().copyTo(dst);
        }
    }
}

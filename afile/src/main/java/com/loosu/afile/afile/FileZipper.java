package com.loosu.afile.afile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loosu.afile.afile.excepton.CancelException;
import com.loosu.afile.afile.interfaces.IBuilder;

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

class FileZipper extends Zipper {
    private final File[] sources;

    private FileZipper(@NonNull File[] sources, @Nullable Listener listener) {
        super(listener);
        this.sources = sources;
    }

    @Override
    public synchronized boolean zipAs(@NonNull File dst) throws CancelException {
        try {
            checkCanceled();

            notifyOnStart();

            FileInOutScanner.Builder scanner = new FileInOutScanner.Builder();
            for (File source : sources) {
                scanner.append(source);
            }

            FileInOutSources scanResult = scanner.setListener(new Scanner.AdapterListener() {
                @Override
                public void onProgress(@NonNull Scanner scanner, @NonNull File file) {
                    notifyOnScan(file);
                }
            }).setDst(new File("")).scan();

            notifyOnScanResult(scanResult);

            ZipOutputStream zos = null;
            FileInputStream fis = null;

            try {
                zos = new ZipOutputStream(new FileOutputStream(dst));
                Map<File, File> dirs = scanResult.dirs;
                for (Map.Entry<File, File> entry : dirs.entrySet()) {
                    File inFile = entry.getKey();
                    File outFile = entry.getValue();
                    notifyOnZip(inFile);
                    ZipEntry zipEntry = new ZipEntry(outFile.getAbsolutePath() + File.separator);
                    zos.putNextEntry(zipEntry);
                    zos.closeEntry();
                }

                Map<File, File> files = scanResult.files;
                for (Map.Entry<File, File> entry : files.entrySet()) {
                    File inFile = entry.getKey();
                    File outFile = entry.getValue();
                    notifyOnZip(inFile);

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
            return false;
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

    public static final class Builder implements IBuilder<FileZipper> {

        private final Collection<File> sources = new ArrayList<>();

        @Nullable
        private Listener listener;

        Builder() {
        }

        public  Builder append(@NonNull File file) {
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
        public FileZipper build() {
            return new FileZipper(sources.toArray(new File[0]), listener);
        }

        public boolean zipAs(@NonNull File dst) throws CancelException {
            return build().zipAs(dst);
        }
    }
}

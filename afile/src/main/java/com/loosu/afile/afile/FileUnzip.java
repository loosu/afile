package com.loosu.afile.afile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loosu.afile.afile.excepton.CancelException;
import com.loosu.afile.afile.interfaces.IBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

final class FileUnzip {
    private static final String TAG = "FileUnzip";

    private final File source;

    public FileUnzip(File source) {
        this.source = source;
    }

    public synchronized void unzipTo(@NonNull File dst) {
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(source));
            ZipEntry zipEntry = null;
            while ((zipEntry = zis.getNextEntry()) != null) {
                String name = zipEntry.getName();
                File file = new File(dst, name);
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
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static final class Builder implements IBuilder<FileUnzip> {

        @Nullable
        private File source = null;

        Builder() {
        }

        public Builder source(@NonNull File file) {
            source = file;
            return this;
        }

        @Override
        public FileUnzip build() {
            return new FileUnzip(source);
        }

        public void unzipTo(@NonNull File dst) throws CancelException {
            build().unzipTo(dst);
        }
    }
}

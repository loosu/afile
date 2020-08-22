package com.loosu.afile.afile;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.Collection;

public class FileInputSources extends Sources{
    final File[] dirs;
    final File[] files;

    FileInputSources(@NonNull Collection<File> dirs, @NonNull Collection<File> files, long totalSize) {
        super(totalSize);
        this.dirs = dirs.toArray(new File[0]);
        this.files = files.toArray(new File[0]);
    }

    public int getDirSize() {
        return dirs.length;
    }

    public int getFileSize() {
        return files.length;
    }
}

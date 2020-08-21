package com.loosu.afile.afile.core;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.Collection;

public class FileSources {
    private final File[] dirs;
    private final File[] files;
    private final long totalSize;

    FileSources(@NonNull File[] dirs, @NonNull File[] files, long totalSize) {
        this.dirs = dirs;
        this.files = files;
        this.totalSize = totalSize;
    }

    FileSources(@NonNull Collection<File> dirs, @NonNull Collection<File> files, long totalSize) {
        this.dirs = dirs.toArray(new File[0]);
        this.files = files.toArray(new File[0]);
        this.totalSize = totalSize;
    }

    public int getDirSize() {
        return dirs.length;
    }

    public int getFileSize() {
        return files.length;
    }

    public long getTotalSize() {
        return totalSize;
    }
}

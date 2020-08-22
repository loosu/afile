package com.loosu.afile.afile;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

public class FileSources {
    final File[] dirs;
    final File[] files;
    final long totalSize;

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

    @Override
    public String toString() {
        return "FileSources{" +
                "dirs=" + dirs.length +
                ", files=" + files.length +
                ", totalSize=" + totalSize +
                '}';
    }
}

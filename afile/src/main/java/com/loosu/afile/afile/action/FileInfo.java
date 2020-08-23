package com.loosu.afile.afile.action;

import androidx.annotation.NonNull;

import com.loosu.afile.afile.AFileUtils;

import java.io.File;
import java.util.Map;

public class FileInfo {
    final Map<File, File> dirs;
    final Map<File, File> files;
    private final long totalSize;

    public FileInfo(@NonNull Map<File, File> dirs, @NonNull Map<File, File> files, long totalSize) {
        AFileUtils.requireNonNull(dirs, "dir is null");
        AFileUtils.requireNonNull(files, "files is null");
        this.dirs = dirs;
        this.files = files;
        this.totalSize = totalSize;
    }

    public int getDirSize() {
        return dirs.size();
    }

    public int getFileSize() {
        return files.size();
    }

    public long getTotalSize() {
        return totalSize;
    }

    @Override
    public String toString() {
        return "FileSources{" +
                "dirs=" + getDirSize() +
                ", files=" + getFileSize() +
                ", totalSize=" + getTotalSize() +
                '}';
    }
}

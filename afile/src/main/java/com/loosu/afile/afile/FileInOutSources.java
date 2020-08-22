package com.loosu.afile.afile;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.Map;

public class FileInOutSources extends Sources {
    final Map<File, File> dirs;
    final Map<File, File> files;

    FileInOutSources(@NonNull Map<File, File> dirs, @NonNull Map<File, File> files, long totalSize) {
        super(totalSize);
        this.dirs = dirs;
        this.files = files;
    }

    public int getDirSize() {
        return dirs.size();
    }

    public int getFileSize() {
        return files.size();
    }

}

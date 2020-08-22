package com.loosu.afile.afile;

public abstract class Sources {
    private final long totalSize;

    protected Sources(long totalSize) {
        this.totalSize = totalSize;
    }

    public abstract int getDirSize();

    public abstract int getFileSize();

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

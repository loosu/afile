package com.loosu.afile.afile;


public final class AFile {
    private AFile() {

    }

    public static FileDeleter.Builder delete() {
        return new FileDeleter.Builder();
    }

    public static FileScanner.Builder scan() {
        return new FileScanner.Builder();
    }

    public static FileCopier.Builder copy() {
        return new FileCopier.Builder();
    }
}

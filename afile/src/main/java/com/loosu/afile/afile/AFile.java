package com.loosu.afile.afile;


public final class AFile {
    private AFile() {

    }

    public static FileDeleter.Builder delete() {
        return new FileDeleter.Builder();
    }

    public static FileInputScanner.Builder scan() {
        return new FileInputScanner.Builder();
    }

    public static FileCopier.Builder copy() {
        return new FileCopier.Builder();
    }

    public static FileZipper.Builder zip() {
        return new FileZipper.Builder();
    }
}

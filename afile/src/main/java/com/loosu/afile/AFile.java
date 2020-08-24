package com.loosu.afile;


import com.loosu.afile.action.Copy;
import com.loosu.afile.action.Delete;
import com.loosu.afile.action.Scan;
import com.loosu.afile.action.Unzip;
import com.loosu.afile.action.Zip;

public final class AFile {
    private AFile() {

    }

    public static Scan.Builder scan() {
        return new Scan.Builder();
    }

    public static Copy.Builder copy() {
        return new Copy.Builder();
    }

    public static Delete.Builder delete() {
        return new Delete.Builder();
    }

    public static Zip.Builder zip() {
        return new Zip.Builder();
    }

    public static Unzip.Builder unzip() {
        return new Unzip.Builder();
    }
}

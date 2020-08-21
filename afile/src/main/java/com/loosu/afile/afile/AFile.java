package com.loosu.afile.afile;


import com.loosu.afile.afile.core.FileScanner;

public final class AFile {

    public static FileScanner.Builder scan(){
        return new FileScanner.Builder();
    }

}

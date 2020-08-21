package com.loosu.afile.afile.interfaces;


import androidx.annotation.NonNull;

import com.loosu.afile.afile.excepton.CancelException;

import java.io.File;

public interface Copier {

    public boolean copyTo(@NonNull File dst) throws CancelException;
}

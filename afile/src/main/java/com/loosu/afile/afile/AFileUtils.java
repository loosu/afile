package com.loosu.afile.afile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.IOException;

final class AFileUtils {
    private AFileUtils() {
    }

    @NonNull
    static <T> T requireNonNull(@Nullable T obj) {
        if (obj == null)
            throw new NullPointerException();
        return obj;
    }

    @NonNull
    static <T> T requireNonNull(@Nullable T obj, @Nullable String message) {
        if (obj == null)
            throw new NullPointerException(message);
        return obj;
    }

    static void close(@Nullable Closeable closeable) {
        if (closeable == null){
            return;
        }

        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

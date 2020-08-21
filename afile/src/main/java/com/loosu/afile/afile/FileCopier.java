package com.loosu.afile.afile;

import androidx.annotation.NonNull;

import com.loosu.afile.afile.excepton.CancelException;
import com.loosu.afile.afile.interfaces.Copier;
import com.loosu.afile.afile.interfaces.IBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class FileCopier implements Copier {
    private final File[] sources;

    private FileCopier(@NonNull File[] sources) {
        this.sources = sources;
    }

    @Override
    public boolean copyTo(@NonNull File dst) throws CancelException {
        return false;
    }


    public final class Builder implements IBuilder<FileCopier> {
        private final Collection<File> sources = new ArrayList<>();

        public Builder append(@NonNull File file) {
            if (!sources.contains(file)) {
                sources.add(file);
            }
            return this;
        }

        @Override
        public FileCopier build() {
            return new FileCopier(sources.toArray(new File[0]));
        }

        public boolean copyTo(@NonNull File dst) {
            return build().copyTo(dst);
        }
    }
}

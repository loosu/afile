package com.loosu.afile.afile;

import com.loosu.afile.afile.excepton.CancelException;
import com.loosu.afile.afile.interfaces.Cancelable;

public abstract class Canceller implements Cancelable {
    private boolean canceled = false;

    @Override
    public void cancel() {
        canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }

    protected void checkCanceled() {
        if (isCanceled()) {
            throw new CancelException();
        }
    }
}

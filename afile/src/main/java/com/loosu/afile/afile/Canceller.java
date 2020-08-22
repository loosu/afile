package com.loosu.afile.afile;

import com.loosu.afile.afile.excepton.CancelException;
import com.loosu.afile.afile.interfaces.Cancelable;

abstract class Canceller implements Cancelable {
    private boolean canceled = false;

    @Override
    public void cancel() throws CancelException {
        canceled = true;
    }

    public boolean isCanceled(){
        return canceled;
    }

    protected void checkCanceled() {
        if (isCanceled()) {
            throw new CancelException();
        }
    }
}

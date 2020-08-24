package com.loosu.afile.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;

import com.loosu.afile.AFile;
import com.loosu.afile.action.Action;
import com.loosu.afile.action.Scan;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_scan_test:
                onClickBtnScanTest();
                break;
            case R.id.btn_copy_test:
                onClickBtnCopyTest();
                break;
            case R.id.btn_zip_test:
                onClickBtnZipTest();
                break;
            case R.id.btn_unzip_test:
                onCLickBtnUnzip();
                break;
            case R.id.btn_del_test:
                onClickBtnDeleteTest();
                break;
        }
    }

    private void onClickBtnScanTest() {
        Log.i(TAG, "onClickBtnScanTest");
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }
        File file2 = new File("/system");
        Scan.Result result = AFile.scan().setListener(actionListener)
                .append(file2)
                .start();

        Log.i(TAG, "************************************");
        Log.i(TAG, "dirs      : " + result.getDirSize());
        Log.i(TAG, "files     : " + result.getFileSize());
        Log.i(TAG, "total size: " + Formatter.formatFileSize(getApplication(), result.getTotalSize()));
    }

    private void onClickBtnCopyTest() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }

        AFile.copy().setListener(actionListener)
                .append(new File("/sdcard/DCIM"))
                .append(new File("/sdcard/DCIM/Camera"))
                .setDst(getFilesDir())
                .start();
    }

    private void onClickBtnZipTest() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }


        AFile.zip().setListener(actionListener)
                .append(new File("/sdcard/DCIM/Camera"))
                .setDst(new File(getFilesDir(), "1.zip"))
                .start();
    }

    private void onCLickBtnUnzip() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }

        AFile.unzip().setListener(actionListener)
                .source(new File(getFilesDir(), "1.zip"))
                .setDst(new File(getFilesDir(), "1unzip"))
                .start();
    }

    private void onClickBtnDeleteTest() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }

        AFile.delete().setListener(actionListener)
                .setScanListener(actionListener)
                .append(getFilesDir())
                .start();
    }

    public final Action.Listener actionListener = new Action.Listener() {
        @Override
        public void onPrepare(@NonNull Action action) {
            Log.i(TAG, "onPrepare: action = " + action);
        }

        @Override
        public void onStart(@NonNull Action action) {
            Log.i(TAG, "onStart: action = " + action);
        }

        @Override
        public void onEnd(@NonNull Action action) {
            Log.i(TAG, "onEnd: action = " + action);
        }

        @Override
        public void onProgress(@NonNull Action action, @NonNull File in, @NonNull File out) {
            Log.d(TAG, "onProgress:" + action + ", in = " + in + ", out = " + out);
        }

        @Override
        public void onError(@NonNull Action action, @NonNull Throwable throwable) {
            Log.w(TAG, "onError: action = " + action, throwable);
        }
    };
}
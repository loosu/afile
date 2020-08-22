package com.loosu.afile.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;

import com.loosu.afile.afile.AFile;
import com.loosu.afile.afile.Copier;
import com.loosu.afile.afile.FileDeleter;
import com.loosu.afile.afile.FileInputScanner;
import com.loosu.afile.afile.FileInputSources;
import com.loosu.afile.afile.Scanner;
import com.loosu.afile.afile.Sources;

import java.io.File;
import java.io.IOException;

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
            case R.id.btn_del_test:
                onClickBtnDeleteTest();
                break;
        }
    }

    private void onClickBtnScanTest() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }
        File file1 = new File(Environment.getExternalStorageDirectory(), "Android");
        File file2 = new File("/system");
        FileInputSources result = AFile.scan().setListener(listener)
                .append(file2)
                .scan();

        Log.i(TAG, "************************************");
        Log.i(TAG, "dirs      : " + result.getDirSize());
        Log.i(TAG, "files     : " + result.getFileSize());
        Log.i(TAG, "total size: " + Formatter.formatFileSize(this, result.getTotalSize()));
    }

    private void onClickBtnCopyTest() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }

        try {
            AFile.copy().setListener(copyListener)
                    .append(new File("/sdcard/DCIM"))
                    .append(new File("/sdcard/DCIM/Camera"))
                    .copyTo(getFilesDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onClickBtnDeleteTest() {
        AFile.delete().setListener(deleteListener)
                .append(getFilesDir())
                .delete();
    }

    private final FileInputScanner.Listener listener = new FileInputScanner.Listener() {

        @Override
        public void onStart(@NonNull Scanner scanner) {
            Log.i(TAG, "onStart:");
            scanner.cancel();
        }

        @Override
        public void onEnd(@NonNull Scanner scanner) {
            Log.i(TAG, "onEnd:");
        }

        @Override
        public void onProgress(@NonNull Scanner scanner, @NonNull File file) {
            Log.d(TAG, "onProgress: file = " + file);
        }

        @Override
        public void onError(@NonNull Scanner scanner, @NonNull Throwable throwable) {
            Log.w(TAG, "onError: ", throwable);
        }
    };

    public final Copier.Listener copyListener = new Copier.Listener() {
        @Override
        public void onStart(@NonNull Copier copier) {
            Log.i(TAG, "onStart:");
        }

        @Override
        public void onEnd(@NonNull Copier copier) {
            Log.i(TAG, "onEnd:");
        }

        @Override
        public void onScan(@NonNull Copier copier, @NonNull File file) {
            Log.d(TAG, "onScan:" + file);
        }

        @Override
        public void onScanResult(@NonNull Copier copier, @NonNull Sources sources) {
            Log.i(TAG, "onScanResult: " + sources);
        }

        @Override
        public void onCopy(@NonNull Copier copier, @NonNull File file) {
            Log.d(TAG, "onCopy:" + file);
        }

        @Override
        public void onError(@NonNull Copier copier, @NonNull Throwable throwable) {
            Log.w(TAG, "onError: ", throwable);
        }
    };

    public final FileDeleter.Listener deleteListener = new FileDeleter.Listener() {
        @Override
        public void onStart(@NonNull FileDeleter deleter) {
            Log.i(TAG, "onStart:");
        }

        @Override
        public void onEnd(@NonNull FileDeleter deleter) {
            Log.i(TAG, "onEnd:");
        }

        @Override
        public void onScan(@NonNull FileDeleter deleter, @NonNull File file) {
            Log.d(TAG, "onScan:" + file);
        }

        @Override
        public void onScanResult(@NonNull FileDeleter deleter, @NonNull FileInputSources sources) {
            Log.i(TAG, "onScanResult: " + sources);
        }

        @Override
        public void onDelete(@NonNull FileDeleter deleter, @NonNull File file) {
            Log.d(TAG, "onDelete:" + file);
        }

        @Override
        public void onError(@NonNull FileDeleter deleter, @NonNull Throwable throwable) {
            Log.w(TAG, "onError: ", throwable);
        }
    };
}
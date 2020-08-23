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

import com.loosu.afile.afile.AFile;
import com.loosu.afile.afile.Copier;
import com.loosu.afile.afile.FileDeleter;
import com.loosu.afile.afile.FileInputScanner;
import com.loosu.afile.afile.FileInputSources;
import com.loosu.afile.afile.Scanner;
import com.loosu.afile.afile.Sources;
import com.loosu.afile.afile.Zipper;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private final Executor mExecutor = Executors.newFixedThreadPool(3);

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
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }
        File file2 = new File("/system");
        final FileInputScanner scanner = AFile.scan().setListener(listener)
                .append(file2)
                .build();


        for (int i = 0; i < 2; i++) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    FileInputSources result = scanner.scan();
                    Log.i(TAG, "************************************");
                    Log.i(TAG, "dirs      : " + result.getDirSize());
                    Log.i(TAG, "files     : " + result.getFileSize());
                    Log.i(TAG, "total size: " + Formatter.formatFileSize(getApplication(), result.getTotalSize()));
                }
            });
        }
    }

    private void onClickBtnCopyTest() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }

        AFile.copy().setListener(copyListener)
                .append(new File("/sdcard/DCIM"))
                .append(new File("/sdcard/DCIM/Camera"))
                .copyTo(getFilesDir());
    }

    private void onClickBtnZipTest() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }


        AFile.zip().setListener(zipListener)
                .append(new File("/sdcard/DCIM/Camera"))
                .append(new File("/sdcard/DCIM/Camera/beaut" +
                        ".y_20190416230730.jpg"))
                .zipAs(new File(getFilesDir(), "1.zip"));
    }

    private void onCLickBtnUnzip() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }

        AFile.unzip().source(new File(getFilesDir(), "A-ORC.zip"))
                .unzipTo(new File(getFilesDir(), "1unzip"));
    }

    private void onClickBtnDeleteTest() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }

        AFile.delete().setListener(deleteListener)
                .append(getFilesDir())
                .delete();
    }

    private final FileInputScanner.Listener listener = new FileInputScanner.Listener() {

        @Override
        public void onStart(@NonNull Scanner scanner) {
            Log.i(TAG, "onStart:");
            //scanner.cancel();
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

    private final Zipper.Listener zipListener = new Zipper.Listener() {
        @Override
        public void onStart(@NonNull Zipper zipper) {
            Log.i(TAG, "onStart:");
        }

        @Override
        public void onEnd(@NonNull Zipper zipper) {
            Log.i(TAG, "onEnd:");
        }

        @Override
        public void onScan(@NonNull Zipper zipper, @NonNull File file) {
            Log.d(TAG, "onScan:" + file);
        }

        @Override
        public void onScanResult(@NonNull Zipper zipper, @NonNull Sources sources) {
            Log.i(TAG, "onScanResult: " + sources);
        }

        @Override
        public void onZip(@NonNull Zipper zipper, @NonNull File file) {
            Log.d(TAG, "onZip:" + file);
        }

        @Override
        public void onError(@NonNull Zipper zipper, @NonNull Throwable throwable) {
            Log.w(TAG, "onError: ", throwable);
        }
    };
}
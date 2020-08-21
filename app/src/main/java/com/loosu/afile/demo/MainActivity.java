package com.loosu.afile.demo;

import androidx.annotation.MainThread;
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
import com.loosu.afile.afile.core.FileScanner;
import com.loosu.afile.afile.core.FileSources;

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
        }
    }

    private void onClickBtnScanTest() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }

        File file1 = new File(Environment.getExternalStorageDirectory(), "Android");
        File file2 = new File("/system");
        FileSources result = AFile.scan().append(file2)
                .setListener(listener)
                .scan();

        Log.i(TAG, "************************************");
        Log.i(TAG, "dirs      : " + result.getDirSize());
        Log.i(TAG, "files     : " + result.getFileSize());
        Log.i(TAG, "total size: " + Formatter.formatFileSize(this, result.getTotalSize()));
    }

    private final FileScanner.Listener listener = new FileScanner.Listener() {
        @Override
        public void onStart(@NonNull FileScanner scanner) {
            Log.i(TAG, "onStart:");
            scanner.cancel();
        }

        @Override
        public void onEnd(@NonNull FileScanner scanner) {
            Log.i(TAG, "onEnd:");
        }

        @Override
        public void onProgress(@NonNull FileScanner scanner, @NonNull File file) {
            Log.d(TAG, "onProgress: file = " + file);
        }

        @Override
        public void onError(@NonNull FileScanner scanner, @NonNull Throwable throwable) {
            Log.w(TAG, "onError: ", throwable);
        }
    };
}
package com.example.englishlearningapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.utils.CustomTextRecognizer;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.FileInputStream;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {

    SurfaceView mCameraView;
    TextView mTextView;
    CameraSource mCameraSource;
    ImageView imgTranslate;

    private static final String TAG = "MainActivity";
    private static final int requestPermissionID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        imgTranslate = findViewById(R.id.image_view_translate);
        Bitmap bmp = null;
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            imgTranslate.setImageBitmap(bmp);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode != requestPermissionID) {
//            Log.d(TAG, "Got unexpected permission result: " + requestCode);
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//            return;
//        }
//
//        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            try {
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//                mCameraSource.start(mCameraView.getHolder());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void startCameraResource() {
//        {
//            final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
//            final CustomTextRecognizer customTextRecognizer = new CustomTextRecognizer(textRecognizer, 300, 300);
//
//            if (!textRecognizer.isOperational()) {
//                Log.w(TAG, "Detector dependencies not loaded yet");
//            } else {
//
//                //Initialize camerasource to use high resolution and set Autofocus on.
//                mCameraSource = new CameraSource.Builder(getApplicationContext(), customTextRecognizer)
//                        .setFacing(CameraSource.CAMERA_FACING_BACK)
//                        .setRequestedPreviewSize(1280, 1024)
//                        .setAutoFocusEnabled(true)
//                        .setRequestedFps(2.0f)
//                        .build();
//
//                /**
//                 * Add call back to SurfaceView and check if camera permission is granted.
//                 * If permission is granted we can start our cameraSource and pass it to surfaceView
//                 */
//                mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
//                    @Override
//                    public void surfaceCreated(SurfaceHolder holder) {
//                        try {
//
//                            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
//                                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//
//                                ActivityCompat.requestPermissions(CameraActivity.this,
//                                        new String[]{Manifest.permission.CAMERA},
//                                        requestPermissionID);
//                                return;
//                            }
//                            mCameraSource.start(mCameraView.getHolder());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//                    }
//
//                    /**
//                     * Release resources for cameraSource
//                     */
//                    @Override
//                    public void surfaceDestroyed(SurfaceHolder holder) {
//                        mCameraSource.stop();
//                    }
//                });
//
//                //Set the TextRecognizer's Processor.
//                customTextRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
//                    @Override
//                    public void release() {
//                    }
//
//                    /**
//                     * Detect all the text from camera using TextBlock and the values into a stringBuilder
//                     * which will then be set to the textView.
//                     * */
//                    @Override
//                    public void receiveDetections(Detector.Detections<TextBlock> detections) {
//                        final SparseArray<TextBlock> items = detections.getDetectedItems();
//
//                        if (items.size() != 0 ){
//
//
//                            mTextView.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    StringBuilder stringBuilder = new StringBuilder();
//                                    TextBlock newitem = items.valueAt(0);
//                                    String itemValue = newitem.getValue();
//                                    for(int i=0;i<items.size();i++) {
//                                        TextBlock item = items.valueAt(i);
//                                        stringBuilder.append(item.getValue());
//                                        stringBuilder.append("\n");
//                                    }
//                                    Log.d("AAA", itemValue);
//                                    mTextView.setText(stringBuilder.toString());
//                                }
//                            });
//                        }
//                    }
//                });
//            }
//        }
//    }


}

package com.example.englishlearningapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.fragments.HistoryFragment;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.CustomTextRecognizer;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity {

    ImageView imgTranslate;
    ListView lvCaptureText;
    ArrayList<String> arrCaptureText;
    ArrayAdapter adapter;
    DatabaseAccess databaseAccess;

    private static final String TAG = "MainActivity";
    private static final int requestPermissionID = 101;
    Bitmap bmp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        imgTranslate = findViewById(R.id.image_view_translate);
        lvCaptureText = findViewById(R.id.list_view_capture_text);
        databaseAccess = DatabaseAccess.getInstance(this);
        arrCaptureText = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrCaptureText);
        String filename = getIntent().getStringExtra("image");
        getBitmapIntent(filename, bmp);
        lvCaptureText.setAdapter(adapter);
        lvCaptureText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(CameraActivity.this, arrCaptureText.get(position), Toast.LENGTH_SHORT).show();
                ArrayList<Word> wordList = databaseAccess.getWords(arrCaptureText.get(position).trim().toLowerCase());
//                Toast.makeText(CameraActivity.this, wordList.get(position).getWord(), Toast.LENGTH_SHORT).show();
                String html = wordList.get(0).getHtml();
                String word = wordList.get(0).getWord();
                int wordId = wordList.get(0).getId();
                int remembered = wordList.get(0).getRemembered();
                moveToMeaningActivity(html, word, wordId, remembered);
            }
        });
    }

    private void moveToMeaningActivity(String html, String word, int wordId, int remembered) {
        Intent meaningIntent = new Intent(this, MeaningActivity.class);
        meaningIntent.putExtra("html", html);
        meaningIntent.putExtra("word", word);
        meaningIntent.putExtra("id", wordId);
        meaningIntent.putExtra("remembered", remembered);
        startActivity(meaningIntent);
    }

    private void getBitmapIntent(String filename, Bitmap bmp){
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            imgTranslate.setImageBitmap(bmp);
            is.close();
            getTextFromImage(bmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTextFromImage(Bitmap bitmap){
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()){
            Toast.makeText(this, "Could not get the text", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            for (int i = 0; i < items.size(); i++){
                TextBlock item = items.valueAt(i);
                String[] words = item.getValue().split(" ");
                for (String word: words) {
                    arrCaptureText.add(word);
                }
            }

        }
    }


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

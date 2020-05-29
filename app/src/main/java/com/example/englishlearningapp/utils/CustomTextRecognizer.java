package com.example.englishlearningapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;

import java.io.ByteArrayOutputStream;

public class CustomTextRecognizer extends Detector<TextBlock> {
    private Detector<TextBlock> delegate;
    private int boxWidth, boxHeight;

    public CustomTextRecognizer(Detector<TextBlock> delegate, int boxWidth, int boxHeight) {
        this.delegate = delegate;
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
    }

    @Override
    public SparseArray<TextBlock> detect(Frame frame) {
        int width = frame.getMetadata().getWidth();
        int height = frame.getMetadata().getHeight();
        int right = (width / 2) + (boxHeight / 2);
        int left = (width / 2) - (boxHeight / 2);
        int bottom = (height / 2) + (boxWidth / 2);
        int top = (height / 2) - (boxWidth / 2);

        YuvImage yuvImage = new YuvImage(frame.getGrayscaleImageData().array(), ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(left, top, right, bottom), 100, byteArrayOutputStream);
        byte[] jpegArray = byteArrayOutputStream.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(jpegArray, 0, jpegArray.length);

        Frame croppedFrame =
                new Frame.Builder()
                        .setBitmap(bitmap)
                        .setRotation(frame.getMetadata().getRotation())
                        .build();

        return delegate.detect(croppedFrame);
    }

    public boolean isOperational() {
        return delegate.isOperational();
    }

    public boolean setFocus(int id) {
        return delegate.setFocus(id);
    }
}

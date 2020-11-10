package com.example.facecar20.Face;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.facecar20.FaceDetection.Box;
import com.example.facecar20.FaceDetection.MTCNN;

import java.util.Vector;

public class FaceOperations {
    private Bitmap crop;
    public FaceOperations(Bitmap bitmap, MTCNN mtcnn){crop=cropFace(bitmap, mtcnn);}
    public Bitmap getCrop(){
        return crop;
    }
    private Bitmap cropFace(Bitmap bitmap, MTCNN mtcnn){
        Bitmap croppedBitmap = null;
        try {
            Vector<Box> boxes = mtcnn.detectFaces(bitmap, 10);

            Log.i("MTCNN", "No. of faces detected: " + boxes.size());

            int left = boxes.get(0).left();
            int top = boxes.get(0).top();

            int x = boxes.get(0).left();
            int y = boxes.get(0).top();
            int width = boxes.get(0).width();
            int height = boxes.get(0).height();


            if (y + height >= bitmap.getHeight())
                height -= (y + height) - (bitmap.getHeight() - 1);
            if (x + width >= bitmap.getWidth())
                width -= (x + width) - (bitmap.getWidth() - 1);

            Log.i("MTCNN", "Final x: " + String.valueOf(x + width));
            Log.i("MTCNN", "Width: " + bitmap.getWidth());
            Log.i("MTCNN", "Final y: " + String.valueOf(y + width));
            Log.i("MTCNN", "Height: " + bitmap.getWidth());

            croppedBitmap = Bitmap.createBitmap(bitmap, x, y, width, height);
        }catch (Exception e){
            e.printStackTrace();
        }
        return croppedBitmap;
    }
}

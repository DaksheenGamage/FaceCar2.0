package com.example.facecar20.Camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.camerakit.CameraKitView;
import com.example.facecar20.Face.FaceOperations;
import com.example.facecar20.FaceDetection.MTCNN;
import com.example.facecar20.FaceRecognition.FaceNet;
import com.example.facecar20.GuestDashboard;
import com.example.facecar20.MemberDashboard;
import com.example.facecar20.OwnerDashboard;
import com.example.facecar20.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import es.dmoral.toasty.Toasty;

public class CameraScanFace extends AppCompatActivity {
    private CameraKitView cameraView;
    private FloatingActionButton btnCapture;
    String username,userType;
    SharedPreferences prf;
    Bitmap savedBitmap,scanBitmap,addedBitmap=null;
    RelativeLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_scan_face);
        cameraView=findViewById(R.id.cameraScan);
        btnCapture=findViewById(R.id.btnFAScan);
        layout=findViewById(R.id.layoutScan);

        prf = getSharedPreferences("LogDetails",MODE_PRIVATE);

        username = prf.getString("USERNAME","");
        userType=prf.getString("LOGEDACC","");
        viewImage(username,"Face");
        btnCapture.setOnClickListener(btnCaptureListener);

    }

    private View.OnClickListener btnCaptureListener = new View.OnClickListener() {
        public void onClick(View view) {
            btnCapture.setEnabled(false);
            cameraView.captureImage(new CameraKitView.ImageCallback() {

                @Override
                public void onImage(CameraKitView cameraKitView, final byte[] photo) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layout.bringToFront();
                            layout.setVisibility(View.VISIBLE);


                        }
                    });

                    addedBitmap=BitmapFactory.decodeByteArray(photo,0,photo.length);

                    if (savedBitmap==null){
                        Toasty.warning(getApplicationContext(), "No registered faces", Toast.LENGTH_SHORT).show();
                        if(userType.equals("O")){
                            Intent intent = new Intent(getApplicationContext(), OwnerDashboard.class);
                            startActivity(intent);
                        }
                        else if(userType.equals("M")){
                            Intent intent = new Intent(getApplicationContext(), MemberDashboard.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(getApplicationContext(), GuestDashboard.class);
                            startActivity(intent);
                        }
                    }else{

                        MTCNN mtcnn = new MTCNN(getAssets());
                        FaceOperations fo =new FaceOperations(addedBitmap,mtcnn);
                        mtcnn.close();
                        Bitmap face1 = fo.getCrop();
                        scanBitmap=face1;
                        scan();

                    }
                }
            });
        }
    };

    public void viewImage(String user,String i) {
        final BitmapFactory.Options[] options = new BitmapFactory.Options[1];

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference().child(user).child(i+".jpg");
        //final StorageReference gsReference = storage.getReferenceFromUrl(faceRef.toString());
        final File localFile;
        storageRef.getBytes(1024*1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        savedBitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                savedBitmap=null;
            }
        });
    }

    public void scan(){
        FaceNet facenet;
        try {
            facenet = new FaceNet(getAssets());
            if (scanBitmap != null) {
                double score = facenet.getSimilarityScore(savedBitmap, scanBitmap);
                Log.i("score", String.valueOf(score));
                //Toast.makeText(MainActivity.this, "Similarity score: " + score, Toast.LENGTH_SHORT).show();
                if(score<=15.0){
                    Toasty.success(getApplicationContext(), "Faces are matching", Toast.LENGTH_SHORT).show();
                    if(userType.equals("O")){
                        Intent intent = new Intent(getApplicationContext(), OwnerDashboard.class);
                        startActivity(intent);
                    }
                    else if(userType.equals("M")){
                        Intent intent = new Intent(getApplicationContext(), MemberDashboard.class);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(getApplicationContext(), GuestDashboard.class);
                        startActivity(intent);
                    }


                }else{
                    Toasty.error(getApplicationContext(), "Not Matching", Toast.LENGTH_SHORT).show();
                    if(userType.equals("O")){
                        Intent intent = new Intent(getApplicationContext(), OwnerDashboard.class);
                        startActivity(intent);
                    }
                    else if(userType.equals("M")){
                        Intent intent = new Intent(getApplicationContext(), MemberDashboard.class);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(getApplicationContext(), GuestDashboard.class);
                        startActivity(intent);
                    }
                }
            }else{
                Toasty.warning(getApplicationContext(),"No faces detected",Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layout.setVisibility(View.INVISIBLE);
                        btnCapture.setEnabled(true);

                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            if(userType.equals("O")){
                Intent intent = new Intent(getApplicationContext(), OwnerDashboard.class);
                startActivity(intent);
            }
            else if(userType.equals("M")){
                Intent intent = new Intent(getApplicationContext(), MemberDashboard.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(getApplicationContext(), GuestDashboard.class);
                startActivity(intent);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.onResume();
    }

    @Override
    protected void onPause() {
        cameraView.onPause();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public void onBackPressed() {
        if(userType.equals("O")){
            Intent intent = new Intent(getApplicationContext(), OwnerDashboard.class);
            startActivity(intent);
        }
        else if(userType.equals("M")){
            Intent intent = new Intent(getApplicationContext(), MemberDashboard.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getApplicationContext(), GuestDashboard.class);
            startActivity(intent);
        }
    }
    public void camPermission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CameraScanFace.this,
                    new String[]{
                            Manifest.permission.CAMERA},
                    100);
        }
    }

}
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
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.camerakit.CameraKitView;
import com.example.facecar20.Face.FaceOperations;
import com.example.facecar20.FaceDetection.MTCNN;
import com.example.facecar20.GuestDashboard;
import com.example.facecar20.MemberDashboard;
import com.example.facecar20.OwnerDashboard;
import com.example.facecar20.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import es.dmoral.toasty.Toasty;

public class CameraAddFace extends AppCompatActivity {
    private CameraKitView cameraView;
    private FloatingActionButton btnCapture,btnAccept,btnReject;
    private ImageView imageView;
    String mode,username,userType;
    SharedPreferences prf;
    Bitmap addedBitmap = null;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        camPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_add_face);
        prf = getSharedPreferences("LogDetails",MODE_PRIVATE);

        username = prf.getString("USERNAME","");
        userType=prf.getString("LOGEDACC","");

        cameraView = findViewById(R.id.camera);
        btnAccept=findViewById(R.id.btnFAAccept);
        btnCapture=findViewById(R.id.btnFACapture);
        btnReject=findViewById(R.id.btnFACancel);
        imageView=findViewById(R.id.ivCamera);
        loading=findViewById(R.id.loadingAddFace);

        Intent intent = getIntent();
        mode=intent.getStringExtra("mode");
        btnCapture.setOnClickListener(btnCaptureListener);
    }

    private View.OnClickListener btnCaptureListener = new View.OnClickListener() {
        public void onClick(View view) {
            //loading.setVisibility(View.VISIBLE);
            cameraView.captureImage(new CameraKitView.ImageCallback() {
                @Override
                public void onImage(CameraKitView cameraKitView, final byte[] photo) {


                    final Bitmap bitmap= BitmapFactory.decodeByteArray(photo,0,photo.length);
                    addedBitmap=bitmap;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                            if(mode.equals("add")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnCapture.setVisibility(View.INVISIBLE);
                                        cameraView.setVisibility(View.INVISIBLE);

                                        imageView.setVisibility(View.VISIBLE);
                                        btnAccept.setVisibility(View.VISIBLE);
                                        btnReject.setVisibility(View.VISIBLE);
                                    }
                                });

                                btnReject.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                loading.setVisibility(View.INVISIBLE);

                                                btnCapture.setVisibility(View.VISIBLE);
                                                cameraView.setVisibility(View.VISIBLE);

                                                imageView.setVisibility(View.INVISIBLE);
                                                btnAccept.setVisibility(View.INVISIBLE);
                                                btnReject.setVisibility(View.INVISIBLE);
                                            }
                                        });

                                    }
                                });

                                btnAccept.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        loading.bringToFront();
                                        loading.setVisibility(View.VISIBLE);

                                        if (addedBitmap == null){
                                            loading.setVisibility(View.INVISIBLE);
                                            Toasty.warning(getApplicationContext(), "No Image Catured", Toast.LENGTH_SHORT).show();
                                        }else{

                                            MTCNN mtcnn = new MTCNN(getAssets());
                                            FaceOperations fo =new FaceOperations(addedBitmap,mtcnn);
                                            Bitmap face1 = fo.getCrop();
                                            mtcnn.close();
                                            if (face1!=null){
                                                acceptPhoto(username,"Face",face1);

                                            }
                                            else{
                                                Toasty.warning(getApplicationContext(),"No faces detected",Toast.LENGTH_SHORT).show();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        loading.setVisibility(View.INVISIBLE);
                                                        btnCapture.setVisibility(View.VISIBLE);
                                                        cameraView.setVisibility(View.VISIBLE);

                                                        imageView.setVisibility(View.INVISIBLE);
                                                        btnAccept.setVisibility(View.INVISIBLE);
                                                        btnReject.setVisibility(View.INVISIBLE);

                                                    }
                                                });
                                            }

                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    };

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

    public void acceptPhoto(final String user, String i, Bitmap bitmap){


        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference faceRef=storage.getReference().child(user).child(i+".jpg");
        Bitmap uploadBitmap = bitmap;
        ByteArrayOutputStream boas=new ByteArrayOutputStream();
        uploadBitmap.compress(Bitmap.CompressFormat.JPEG,100,boas);
        faceRef.putBytes(boas.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"Face Saved",Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.INVISIBLE);
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
        });

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
            ActivityCompat.requestPermissions(CameraAddFace.this,
                    new String[]{
                            Manifest.permission.CAMERA},
                    100);
        }
    }




}
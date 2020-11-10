package com.example.facecar20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CreateProfile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
    }

    public void createOwner(View view) {
        Intent intent = new Intent(this, CreateAsOwner.class);
        startActivity(intent);
    }
    public void createMember(View view) {
        Intent intent = new Intent(this, CreateAsMember.class);
        startActivity(intent);
    }
    public void backCreateProfile(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }}
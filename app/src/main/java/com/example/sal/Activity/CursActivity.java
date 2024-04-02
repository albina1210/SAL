package com.example.sal.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CursActivity extends AppCompatActivity {
    private ImageView backButton;
    private Button testButton;
    private FirebaseAuth mAuth;
    private DatabaseReference progressRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curs);

        backButton = findViewById(R.id.backButton);
        testButton = findViewById(R.id.testButton);
        mAuth = FirebaseAuth.getInstance();
        progressRef = FirebaseDatabase.getInstance().getReference("progress");
        String userId = mAuth.getCurrentUser().getUid();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CursActivity.this, TestActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Обновление прогресса на 50, если вы просто заходите в CursActivity
        updateProgress(50);
    }

    private void updateProgress(int progress) {
        String userId = mAuth.getCurrentUser().getUid();
        progressRef.child(userId).child("progress").setValue(progress);
    }
}
package com.example.sal.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sal.Fragment.CoursesFragment;
import com.example.sal.Model.ItemCourses;
import com.example.sal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CreateCourseActivity extends AppCompatActivity {
    private static final String TAG = "CreateCourseActivity";

    private EditText editTextCourseCurs;
    private EditText editTextCourseOpisanie;
    private Button buttonCreate;
    private DatabaseReference coursesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        coursesRef = FirebaseDatabase.getInstance().getReference("courses");
        editTextCourseCurs = findViewById(R.id.editTextCourseCurs);
        editTextCourseOpisanie = findViewById(R.id.editTextCourseOpisanie);
        buttonCreate = findViewById(R.id.buttonCreate);

        // Найти иконку возврата в toolbar
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Создание интента для перехода к CoursesFragment
                        Intent intent = new Intent(CreateCourseActivity.this, CoursesFragment.class);
                        intent.putExtra("back", R.id.backButton);
                        startActivity(intent);
                        finish();
                    }
                });
                finish();
            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button Clicked");

                final String curs = editTextCourseCurs.getText().toString();
                final String opisanie = editTextCourseOpisanie.getText().toString();

                if (curs.isEmpty() || opisanie.isEmpty()) {
                    Log.d(TAG, "Empty fields");
                    Toast.makeText(CreateCourseActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }

                Query duplicateCourseQuery = coursesRef.orderByChild("curs").equalTo(curs);
                duplicateCourseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isDuplicate = dataSnapshot.exists();

                        if (isDuplicate) {
                            Log.d(TAG, "Duplicate course");
                        } else {
                            String id = coursesRef.push().getKey();
                            ItemCourses newCourse = new ItemCourses(id, curs, opisanie); // Обновление конструктора
                            coursesRef.child(id).setValue(newCourse);
                            Log.d(TAG, "Course saved to Firebase");

                            // Отправка информации о новом курсе обратно в CoursesFragment
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("newCourse", newCourse);
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "Database error: " + databaseError.getMessage());
                    }
                });
            }
        });
    }
}
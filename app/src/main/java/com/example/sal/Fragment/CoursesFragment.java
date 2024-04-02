package com.example.sal.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sal.Activity.CreateCourseActivity;
import com.example.sal.Adapter.CoursesAdapter;
import com.example.sal.Model.ItemCourses;
import com.example.sal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment {
    private FloatingActionButton addCourseButton;
    private FirebaseAuth mAuth;
    private static final int REQUEST_CODE_CREATE_COURSE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courses, container, false);
        addCourseButton = view.findViewById(R.id.addCourseButton);
        mAuth = FirebaseAuth.getInstance();

        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateCourseActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CREATE_COURSE);
            }
        });

        // Проверка аутентификации пользователя
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String role = dataSnapshot.child("role").getValue(String.class);
                    if (role != null && role.equals("Студент")) {
                        addCourseButton.setVisibility(View.GONE);
                    } else {
                        addCourseButton.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Обработка ошибки
                }
            });
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference coursesRef = databaseRef.child("courses");

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        List<ItemCourses> courseList = new ArrayList<>();
        CoursesAdapter coursesAdapter = new CoursesAdapter(courseList, coursesRef);
        recyclerView.setAdapter(coursesAdapter);

        coursesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courseList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ItemCourses course = dataSnapshot.getValue(ItemCourses.class);
                    if (course != null) {
                        courseList.add(course);
                    }
                }
                coursesAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибок при чтении данных
            }
        });
    }
}
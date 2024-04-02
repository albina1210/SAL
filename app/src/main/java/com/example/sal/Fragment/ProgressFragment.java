package com.example.sal.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sal.Model.ItemProgress;
import com.example.sal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProgressFragment extends Fragment {
    private TextView txtProgressTotal;
    private ProgressBar progressBar;
    private DatabaseReference progressRef;
    private DatabaseReference userRef;
    private String userId;

    public ProgressFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressRef = FirebaseDatabase.getInstance().getReference("progress");
        userRef = FirebaseDatabase.getInstance().getReference("users");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userfam = dataSnapshot.child("userfam").getValue(String.class);
                    String username = dataSnapshot.child("username").getValue(String.class);

                    // Получаем текущий прогресс из базы данных
                    progressRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                // Создаем объект ItemProgress со значениями фамилии, имени, айди и начальным прогрессом 0
                                ItemProgress itemProgress = new ItemProgress(userId, userfam, username, 0);
                                progressRef.child(userId).setValue(itemProgress);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Обработка ошибок
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок
            }
        });
    }

    public static ProgressFragment newInstance() {
        return new ProgressFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_progress, container, false);
        txtProgressTotal = myView.findViewById(R.id.txtProgress_total);
        progressBar = myView.findViewById(R.id.progressBar_total);

        updateProgress();

        return myView;
    }

    private void updateProgress() {
        progressRef.child(userId).child("progress").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int progress = dataSnapshot.getValue(Integer.class);
                    txtProgressTotal.setText(String.valueOf(progress));
                    progressBar.setProgress(progress);
                } else {
                    int progress = 0;
                    progressRef.child(userId).child("progress").setValue(progress);
                    txtProgressTotal.setText(String.valueOf(progress));
                    progressBar.setProgress(progress);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Обработка ошибок
            }
        });
    }
}
package com.example.sal.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.sal.Adapter.ProgressStudentAdapter;
import com.example.sal.Model.ItemProgressStudent;
import com.example.sal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProgressStudentFragment extends Fragment {

    private ListView progressListView;
    private ProgressStudentAdapter progressAdapter;
    private DatabaseReference progressRef;

    public ProgressStudentFragment() {
        // Обязательный пустой публичный конструктор
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress_student, container, false);

        progressListView = view.findViewById(R.id.progressListView);
        ProgressStudentAdapter progressAdapter = new ProgressStudentAdapter(getActivity(), new ArrayList<>());
        progressListView.setAdapter(progressAdapter);


        // Получение ссылки на базу данных "progress"
        progressRef = FirebaseDatabase.getInstance().getReference().child("progress");

        // Наблюдатель для получения данных из таблицы "progress"
        progressRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ItemProgressStudent> progressList = new ArrayList<>();

                for (DataSnapshot progressSnapshot : dataSnapshot.getChildren()) {
                    String userFam = progressSnapshot.child("userfam").getValue(String.class);
                    String userName = progressSnapshot.child("username").getValue(String.class);
                    Long progress = progressSnapshot.child("progress").getValue(Long.class);
                    ItemProgressStudent itemProgressStudent = new ItemProgressStudent(userFam, userName, String.valueOf(progress));
                    progressList.add(itemProgressStudent);
                }

                progressAdapter.setData(progressList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Обработка ошибки получения данных из Firebase
            }
        });

        return view;
    }
}

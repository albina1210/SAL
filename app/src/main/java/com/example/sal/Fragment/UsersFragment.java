package com.example.sal.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sal.Adapter.UsersAdapter;
import com.example.sal.Model.ItemUsers;
import com.example.sal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {
    private FirebaseDatabase mDatabase;
    private ListView userListView;
    private UsersAdapter userAdapter;
    private List<ItemUsers> userList;
    private List<String> rolesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance();
        userList = new ArrayList<>();
        rolesList = new ArrayList<>();
        loadUsers();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        userListView = view.findViewById(R.id.userListView);
        userAdapter = new UsersAdapter(getActivity(), userList, rolesList);
        userListView.setAdapter(userAdapter);
        return view;
    }

    private void loadUsers() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Получение идентификатора текущего пользователя

        mDatabase.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userfam = userSnapshot.child("userfam").getValue(String.class);
                    String username = userSnapshot.child("username").getValue(String.class);
                    String role = userSnapshot.child("role").getValue(String.class);

                    // Проверка наличия данных пользователя и исключения текущего пользователя
                    if (userfam != null && username != null && role != null &&
                            !userSnapshot.getKey().equals(currentUserUid)) {
                        ItemUsers user = new ItemUsers();
                        user.setUserFam(userfam);
                        user.setUserName(username);
                        user.setRole(role);

                        if (!userList.contains(user)) {
                            userList.add(user);
                        }

                        if (!rolesList.contains(role)) {
                            rolesList.add(role);
                        }
                    }
                }

                if (!rolesList.contains("Администратор")) {
                    rolesList.add("Администратор");
                }

                if (!rolesList.contains("Преподаватель")) {
                    rolesList.add("Преподаватель");
                }

                if (!rolesList.contains("Студент")) {
                    rolesList.add("Студент");
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибки при загрузке пользователей
            }
        });
    }
}
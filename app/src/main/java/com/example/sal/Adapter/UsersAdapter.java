package com.example.sal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.sal.Model.ItemUsers;
import com.example.sal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UsersAdapter extends ArrayAdapter<ItemUsers> {
    private List<String> rolesList;
    private Context context;

    public UsersAdapter(Context context, List<ItemUsers> users, List<String> roles) {
        super(context, 0, users);
        this.context = context;
        this.rolesList = roles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemUsers user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_users, parent, false);
        }

        TextView lastNameTextView = convertView.findViewById(R.id.lastNameTextView);
        TextView firstNameTextView = convertView.findViewById(R.id.firstNameTextView);
        Spinner roleSpinner = convertView.findViewById(R.id.roleSpinner);

        lastNameTextView.setText(user.getUserFam());
        firstNameTextView.setText(user.getUserName());

        ArrayAdapter<String> roleAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, rolesList) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(context.getResources().getColor(R.color.color_button));
                textView.setTextSize(10);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(context.getResources().getColor(R.color.white));
                textView.setTextSize(15);
                return view;
            }
        };

        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAdapter);

        int selectedPosition = rolesList.indexOf(user.getRole());
        if (selectedPosition != -1) {
            roleSpinner.setSelection(selectedPosition);
        }

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newRole = rolesList.get(position);
                user.setRole(newRole);
                saveUser(user);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ничего не делать
            }
        });

        return convertView;
    }

    private void saveUser(ItemUsers user) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("userfam").equalTo(user.getUserFam()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    usersRef.child(userId).child("role").setValue(user.getRole());
                }
                if (!rolesList.contains(user.getRole())) {
                    rolesList.add(user.getRole());
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибки при чтении базы данных
            }
        });
    }
}
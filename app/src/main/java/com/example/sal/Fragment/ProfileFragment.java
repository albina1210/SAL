package com.example.sal.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sal.Activity.MainActivity;
import com.example.sal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    FirebaseAuth mAuth;
    DatabaseReference myRef;
    private FirebaseUser mFirebaseUser;


    EditText familiya;
    EditText name;
    EditText otchestvo;
    EditText login;
    EditText role;
    EditText pass1;
    EditText pass2;
    Button btnsave;
    String originalRole;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_profile, container, false);

        familiya = (EditText) myView.findViewById(R.id.editTextFamilya);
        name = (EditText) myView.findViewById(R.id.editTextName);
        otchestvo = (EditText) myView.findViewById(R.id.editTextOtchestvo);
        login = (EditText) myView.findViewById(R.id.editTextLogin);
        role = (EditText) myView.findViewById(R.id.editTextRole);
        pass1 = (EditText) myView.findViewById(R.id.editTextPassword);
        pass2 = (EditText) myView.findViewById(R.id.editTextPasswordRepeat);
        btnsave = (Button) myView.findViewById(R.id.btn_save);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("users/" + mAuth.getUid());

        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                familiya.setText(snapshot.child("userfam").getValue(String.class));
                name.setText(snapshot.child("username").getValue(String.class));
                otchestvo.setText(snapshot.child("userot").getValue(String.class));
                login.setText(snapshot.child("email").getValue(String.class));
                role.setText(snapshot.child("role").getValue(String.class));
                pass1.setText(snapshot.child("password").getValue(String.class));
                pass2.setText(snapshot.child("password").getValue(String.class));

                btnsave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (pass1.getText().toString().equalsIgnoreCase(pass2.getText().toString())
                                && !familiya.getText().toString().isEmpty()
                                && !name.getText().toString().isEmpty()
                                && !otchestvo.getText().toString().isEmpty()) {
                            try {
                                myRef.child("userfam").setValue(familiya.getText().toString());
                                myRef.child("username").setValue(name.getText().toString());
                                myRef.child("userot").setValue(otchestvo.getText().toString());
                                myRef.child("role").setValue(role.getText().toString());
                                Toast.makeText(getActivity(), "Данные обновлены", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                try {
                                    myRef.child("email").setValue(login.getText().toString());
                                    myRef.child("password").setValue(pass1.getText().toString());
                                    changeEmail(login.getText().toString());
                                    changePassword(pass1.getText().toString());
                                } catch (Exception ex) {
                                    System.out.println("****\n" + ex.getMessage() + "\n****");
                                }
                            } catch (Exception ex) {
                                System.out.println("****\n" + ex.getMessage() + "\n****");
                            }
                        } else {
                            Toast.makeText(getActivity(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

        return myView;
    }

    private void changeEmail(String email) {
        mFirebaseUser.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(),"Почта была обновлена",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else{
                            Toast.makeText(getActivity(),"Почта не была обновлена",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void changePassword(String password) {
        mFirebaseUser.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(),"Пароль был обновлен",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else{
                            Toast.makeText(getActivity(),"Пароль не был обновлен",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
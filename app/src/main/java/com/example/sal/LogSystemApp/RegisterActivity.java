package com.example.sal.LogSystemApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText familiya;
    EditText name;
    EditText otchestvo;
    EditText login;
    EditText pass1;
    EditText pass2;

    FirebaseDatabase database;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        familiya = (EditText) findViewById(R.id.editTextFamiliya);
        name = (EditText) findViewById(R.id.editTextName);
        otchestvo = (EditText) findViewById(R.id.editTextOtchestvo);
        login = (EditText) findViewById(R.id.editTextLogin);
        pass1 = (EditText) findViewById(R.id.editTextPassword);
        pass2 = (EditText) findViewById(R.id.editTextPasswordRepeat);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        mAuth = FirebaseAuth.getInstance();

    }

    public void onClick_Login(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void onClickRegiss(View view) {

        String fam = familiya.getText().toString();
        String imya = name.getText().toString();
        String ot = otchestvo.getText().toString();
        String log = login.getText().toString();
        String password1 = pass1.getText().toString();
        String password2 = pass2.getText().toString();

        String Expn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";


        if (fam.isEmpty() || imya.isEmpty() || ot.isEmpty() || log.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
        } else if (!PASSWORD_PATTERN.matcher(password1).matches()) {
            Toast.makeText(RegisterActivity.this, "В пароле должны присуствовать заглавные и строчные буквы и другие символы", Toast.LENGTH_SHORT).show();
        } else if (password1.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Пароль должен быть больше 6 символов", Toast.LENGTH_SHORT).show();
        } else if (!password1.equals(password2)) {
            Toast.makeText(RegisterActivity.this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
        } else if (!(log.matches(Expn) && login.length() > 0)) {
            Toast.makeText(RegisterActivity.this, "Почта должна быть в виде: x@x.x", Toast.LENGTH_SHORT).show();
        } else {

            mAuth.createUserWithEmailAndPassword(login.getText().toString(),
                    pass1.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    myRef.child(mAuth.getUid()).child("userfam").setValue(fam);
                    myRef.child(mAuth.getUid()).child("username").setValue(imya);
                    myRef.child(mAuth.getUid()).child("userot").setValue(ot);
                    myRef.child(mAuth.getUid()).child("email").setValue(log);
                    myRef.child(mAuth.getUid()).child("password").setValue(password1);
                    myRef.child(mAuth.getUid()).child("role").setValue("Студент", new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(RegisterActivity.this, "Ошибка при установке роли", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Успешно", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "Пользователь с такой почтой уже существует" +
                            "Регистрация не удалась", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                // Обработка ошибки
            });
        }
    }
}
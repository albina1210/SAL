package com.example.sal.LogSystemApp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.sal.Activity.MainActivity;
import com.example.sal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText login;
    EditText password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    SharedPreferences mSettings;


    Boolean isInternetPresent = false;
    ConnectionDetector cd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (EditText) findViewById(R.id.editTextLogin);
        password = (EditText) findViewById(R.id.editTextPassword);
        mAuth = FirebaseAuth.getInstance();




        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }


    public void onClick_Regis(View view) {
        Intent intent =new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void onClick_login(View view) {

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.ConnectingToInternet();

        String log = login.getText().toString();
        String pass = password.getText().toString();

        if(!isInternetPresent) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder2.setMessage("Отсуствует интернет-соединение! Пожалуйста, включите интернет!");
            builder2.setCancelable(true);

            builder2.setPositiveButton(
                    "Ок",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert12 = builder2.create();
            alert12.show();

            //цвет текста кнопки в диалоговм окне
            Button b = alert12.getButton(DialogInterface.BUTTON_POSITIVE);
            b.setTextColor(ContextCompat.getColor(this, R.color.aqua));


        }
        else{

            if (log.isEmpty() || pass.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.signInWithEmailAndPassword(login.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {


                                if (task.isSuccessful()) {
                                    Log.d("log", "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    Toast.makeText(LoginActivity.this, "Успешно", Toast.LENGTH_SHORT).show();

                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();


                                } else {
                                    Log.w("log", "signInWithEmail:failture", task.getException());
                                    Toast.makeText(LoginActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }

        }

    }

    public void onClick_ResetPass(View view) {
        TextView tv= (TextView) findViewById(R.id.ResetPassword);
        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }
}
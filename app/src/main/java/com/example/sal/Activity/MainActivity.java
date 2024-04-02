package com.example.sal.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.sal.LogSystemApp.ConnectionDetector;
import com.example.sal.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.ConnectingToInternet();
        if (!isInternetPresent) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder2.setMessage("Отсуствует интернет-соединение. Пожалуйста, включите интернет.");
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
            b.setTextColor(ContextCompat.getColor(this, R.color.color_button));
        }
        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView,navController);
        setupMenu();
    }

    private void setupMenu() {
        Menu menu = navigationView.getMenu();
        MenuItem usersMenuItem = menu.findItem(R.id.users_menu);
        MenuItem progressMenuItem = menu.findItem(R.id.progress_menu);
        MenuItem progressstudentMenuItem = menu.findItem(R.id.progress_student_menu);
        MenuItem coursesMenuItem = menu.findItem(R.id.courses_menu);
        MenuItem dictionaryMenuItem = menu.findItem(R.id.dictionary_menu);

        // Получение роли пользователя из Firebase
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            //Проверка роли пользователя
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String role = dataSnapshot.child("role").getValue(String.class);
                    //Роль пользователя "Администратор", скрываем "Прогресс", "Курсы", "Словарь" и показываем "Пользователи"
                    if (role != null && role.equals("Администратор")) {
                        usersMenuItem.setVisible(true);
                        progressMenuItem.setVisible(false);
                        progressstudentMenuItem.setVisible(false);
                        coursesMenuItem.setVisible(false);
                        dictionaryMenuItem.setVisible(false);
                    //Роль пользователя "Преподаватель", скрываем "Пользователм"
                    } else if (role != null && role.equals("Преподаватель")){
                        usersMenuItem.setVisible(false);
                        progressMenuItem.setVisible(false);
                    //Роль пользователя "Студент", скрываем "Пользователи"
                    } else if (role != null && role.equals("Студент")) {
                        usersMenuItem.setVisible(false);
                        progressstudentMenuItem.setVisible(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Обработка ошибки
                }
            });
        }
    }
    public void onCheck(View view) {
        // Обработка нажатия на кнопку
    }
}
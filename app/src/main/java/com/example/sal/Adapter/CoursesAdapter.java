package com.example.sal.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sal.Activity.CursActivity;
import com.example.sal.Model.ItemCourses;
import com.example.sal.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CourseViewHolder> {
    private List<ItemCourses> courseList;
    private OnItemClickListener mListener;
    private DatabaseReference databaseRef; // Ссылка на базу данных Firebase
    private Button buttonCreate;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public CoursesAdapter(List<ItemCourses> courseList, DatabaseReference databaseRef) {
        this.courseList = courseList;
        this.databaseRef = databaseRef;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_courses, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        ItemCourses course = courseList.get(position);

        holder.titleTextView.setText(course.getCurs());
        holder.descriptionTextView.setText(course.getOpisanie());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Проверка роли пользователя
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String role = dataSnapshot.child("role").getValue(String.class);

                            // Проверка роли студента
                            if (role != null && role.equals("Студент")) {
                                // Роль пользователя "Студент", осуществляется прямой переход к CursActivity
                                Intent intent = new Intent(holder.itemView.getContext(), CursActivity.class);
                                intent.putExtra("course", course);
                                holder.itemView.getContext().startActivity(intent);
                            } else {
                                // Роль пользователя "Преподаватель", отображается диалоговое окно
                                showConfirmationDialog(holder.itemView.getContext(), course);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Обработка ошибки
                        }
                    });
                }
            }
        });
    }


    private void showConfirmationDialog(Context context, final ItemCourses course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Выберите действие");
        builder.setItems(new String[]{"Перейти к курсу", "Редактировать курс", "Удалить курс"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent(context, CursActivity.class);
                        intent.putExtra("course", course);
                        context.startActivity(intent);
                        break;
                    case 1:
                        showEditConfirmationDialog(context, course);
                        break;
                    case 2:
                        showDeleteConfirmationDialog(context, course);
                        break;
                }
            }
        });
        builder.show();
    }


    // Отображение диалогового окна с редактированием курса
    private void showEditConfirmationDialog(Context context, ItemCourses course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogLayout = inflater.inflate(R.layout.dialog_edit_course, null);
        builder.setView(dialogLayout);

        final EditText EdCurs = dialogLayout.findViewById(R.id.EdCurs);
        final EditText EdOpisanie = dialogLayout.findViewById(R.id.EdOpisanie);

        EdCurs.setText(course.getCurs());
        EdOpisanie.setText(course.getOpisanie());

        builder.setTitle("Редактировать курс")
                .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String editedCurs = EdCurs.getText().toString();
                        String editedOpisanie = EdOpisanie.getText().toString();

                        if (!TextUtils.isEmpty(editedCurs) && !TextUtils.isEmpty(editedOpisanie)) {
                            course.setCurs(editedCurs);
                            course.setOpisanie(editedOpisanie);
                            editCourseFromDatabase(course);
                        }
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }



    // Отображение диалогового окна с подтверждением удаления
    private void showDeleteConfirmationDialog(Context context, ItemCourses course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Удалить курс")

                .setMessage("Вы действительно хотите удалить курс \"" + course.getCurs() + "\"?")
                .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCourseFromDatabase(course);
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void editCourseFromDatabase(ItemCourses course) {
        // Обновление данных курса в Firebase Realtime Database
        databaseRef.child(course.getId()).setValue(course)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Данные успешно обновлены
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ошибка при обновлении данных
                    }
                });
    }

    private void deleteCourseFromDatabase(ItemCourses course) {
        // Удаление курса из Firebase Realtime Database
        databaseRef.child(course.getId()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Курс успешно удален
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ошибка при удалении курса
                    }
                });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        CourseViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.cursTextView);
            descriptionTextView = itemView.findViewById(R.id.opisanieTextView);
        }
    }
}
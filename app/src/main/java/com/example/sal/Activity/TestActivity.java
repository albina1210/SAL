package com.example.sal.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sal.Model.ItemResult;
import com.example.sal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TestActivity extends AppCompatActivity {
    private TextView questionTextView;
    private RadioGroup answerRadioGroup;
    private Button submitButton;
    private ImageView backButton;
    private FirebaseAuth mAuth;
    private String[] questions;
    private String[][] answers;
    private int[] correctAnswers;
    private int currentQuestionIndex;

    private DatabaseReference resultsRef;
    private DatabaseReference progressRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultsRef = FirebaseDatabase.getInstance().getReference("results");
        setContentView(R.layout.activity_test);


        questionTextView = findViewById(R.id.questionTextView);
        answerRadioGroup = findViewById(R.id.answerRadioGroup);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            resultsRef = FirebaseDatabase.getInstance().getReference("results").child(uid);
            progressRef = FirebaseDatabase.getInstance().getReference("progress").child(uid);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        questions = new String[]{
                "1. С чего начать системному администратору?",
                "2. Сколько ключей (опций) содержит команда ps?",
                "3. Сколько существует задач системного администратора?",
                "4. Что не входит в задачи системного администратора?",
                "5. О какой задаче системного администратора идёт речь:\n" +
                        "'Сбои систем неизбежны. Задача администратора — диагностировать сбои в системе\n" +
                        "и в случае необходимости вызвать специалистов...'"
        };

        answers = new String[][]{
                {"Руководство", "Словарь", "Книга", "Интернет"},
                {"100", "15", "80", "Такой команды не существует"},
                {"10", "8", "5", "6"},
                {"Инициализация пользователей", "Инсталляция и обновление программ", "Поиск неисправностей", "Написание программ"},
                {"Поиск неисправностей", "Слежение за безопасностью системы", "Мониторинг системы", "Оказание помощи пользователям"}
        };

        correctAnswers = new int[]{0, 2, 1, 3, 0};
        currentQuestionIndex = 0;

        displayQuestion();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });
    }

    private void displayQuestion() {
        questionTextView.setText(questions[currentQuestionIndex]);
        answerRadioGroup.clearCheck();
        answerRadioGroup.removeAllViews();

        for (int i = 0; i < answers[currentQuestionIndex].length; i++) {
            RadioButton answerRadioButton = new RadioButton(this);
            answerRadioButton.setText(answers[currentQuestionIndex][i]);
            answerRadioButton.setId(i);
            answerRadioGroup.addView(answerRadioButton);
        }
    }

    private void checkAnswer() {
        int selectedAnswerIndex = answerRadioGroup.getCheckedRadioButtonId();
        if (selectedAnswerIndex == -1) {
            Toast.makeText(this, "Выберите ответ", Toast.LENGTH_SHORT).show();
        } else {
            boolean isCorrectAnswer = selectedAnswerIndex == correctAnswers[currentQuestionIndex];
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userfam = dataSnapshot.child("userfam").getValue(String.class);
                        String username = dataSnapshot.child("username").getValue(String.class);
                        ItemResult itemResult = new ItemResult(userId, userfam, username, currentQuestionIndex, selectedAnswerIndex, isCorrectAnswer);

                        DatabaseReference resultRef = FirebaseDatabase.getInstance().getReference("results").child(userId).child(String.valueOf(currentQuestionIndex));
                        resultRef.setValue(itemResult, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    if (isCorrectAnswer) {
                                        Toast.makeText(TestActivity.this, "Правильно", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(TestActivity.this, "Неправильно", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    //Обработка ошибок
                                }

                                if (currentQuestionIndex < questions.length - 1) {
                                    currentQuestionIndex++;
                                    displayQuestion();
                                } else {
                                    checkAllAnswers();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //Обработка ошибок
                }
            });
        }
    }

    private void checkAllAnswers() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        resultsRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean allCorrect = true;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    ItemResult itemResult = childSnapshot.getValue(ItemResult.class);
                    if (!itemResult.isCorrectAnswer()) {
                        allCorrect = false;
                        break;
                    }
                }

                if (allCorrect) {
                    int progress = 100;
                    progressRef.child("progress").setValue(progress);
                }

                showResult(currentQuestionIndex);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок
            }
        });
    }

    private void showResult(int correctAnswersCount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
        builder.setTitle("Тест завершён");

        String message = correctAnswersCount == questions.length ? "Тест пройден" : "Тест не пройден";
        message += "\nВы ответили правильно на " + correctAnswersCount + " вопросов из " + questions.length;

        builder.setMessage(message)
                .setPositiveButton("Выход", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("Перепройти тест", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        resetTest();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void resetTest() {
        // Сброс текущего индекса вопроса и других переменных, связанных с тестом
        currentQuestionIndex = 0;

        // Очистка результатов теста пользователя в базе данных
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        resultsRef.child(userId).removeValue();

        // Перезапуск отображения первого вопроса
        displayQuestion();
    }
}
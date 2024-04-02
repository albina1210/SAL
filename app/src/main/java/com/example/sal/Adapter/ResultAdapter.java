package com.example.sal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sal.Model.ItemResult;
import com.example.sal.R;

import java.util.List;

public class ResultAdapter extends ArrayAdapter<ItemResult> {
    private String[][] answers;
    private String[][] questions;

    public ResultAdapter(Context context, List<ItemResult> results, String[][] answers, String[][] questions) {
        super(context, 0, results);
        this.answers = answers;
        this.questions = questions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_result, parent, false);
        }

        TextView questionTextView = convertView.findViewById(R.id.questionTextView);
        TextView answerTextView = convertView.findViewById(R.id.answerTextView);
        TextView resultTextView = convertView.findViewById(R.id.resultTextView);

        ItemResult result = getItem(position);
        if (result != null) {
            int questionIndex = result.getQuestionIndex();
            int selectedAnswerIndex = result.getSelectedAnswerIndex();

            questionTextView.setText((questionIndex >= 1 && questionIndex < questions.length) ? questions[questionIndex][0] : "");
            answerTextView.setText((questionIndex >= 0 && questionIndex < answers.length && selectedAnswerIndex >= 0 && selectedAnswerIndex < answers[questionIndex].length)
                    ? answers[questionIndex][selectedAnswerIndex] : "");
            resultTextView.setText(result.isCorrectAnswer() ? "Правильно" : "Неправильно");
        }

        return convertView;
    }
}
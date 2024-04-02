package com.example.sal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sal.Model.ItemProgressStudent;
import com.example.sal.R;

import java.util.List;

public class ProgressStudentAdapter extends ArrayAdapter<ItemProgressStudent> {
    private List<ItemProgressStudent> progressList;
    private Context context;

    public ProgressStudentAdapter(Context context, List<ItemProgressStudent> progressList) {
        super(context, 0, progressList);
        this.context = context;
        this.progressList = progressList;
    }

    public void setData(List<ItemProgressStudent> progressList) {
        this.progressList.clear();
        this.progressList.addAll(progressList);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_progress_student, parent, false);
        }

        TextView lastName = convertView.findViewById(R.id.lastName);
        TextView firstName = convertView.findViewById(R.id.firstName);
        TextView progress = convertView.findViewById(R.id.progress);

        // Получите текущий элемент данных
        ItemProgressStudent item = getItem(position);

        // Установите значения фамилии, имени и прогресса в TextView
        if (item != null) {
            lastName.setText(item.getUserFam());
            firstName.setText(item.getUserName());
            progress.setText(item.getProgress());
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return progressList.size();
    }
}
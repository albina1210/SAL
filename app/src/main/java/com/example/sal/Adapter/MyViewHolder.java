package com.example.sal.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sal.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView Termin, Ponyatie;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        Termin = itemView.findViewById(R.id.Termin);
        Ponyatie = itemView.findViewById(R.id.Ponyatie);
    }
}

package com.example.sal.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.sal.LogSystemApp.LoginActivity;
import com.example.sal.R;
import com.google.firebase.auth.FirebaseAuth;

public class OutSystemFragment extends Fragment {

    private FirebaseAuth mAuth;
    Button btn_exit;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OutSystemFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static OutSystemFragment newInstance(String param1, String param2) {
        OutSystemFragment fragment = new OutSystemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_out_system, container, false);

        btn_exit = (Button) myView.findViewById(R.id.btn_outSystem);

        btn_exit.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getActivity(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return myView;
    }
}
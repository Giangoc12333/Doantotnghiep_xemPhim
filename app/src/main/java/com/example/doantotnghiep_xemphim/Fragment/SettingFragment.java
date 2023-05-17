package com.example.doantotnghiep_xemphim.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.doantotnghiep_xemphim.AddMoviesActivity;
import com.example.doantotnghiep_xemphim.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private LinearLayout linearLayout_add_movies;
    private Button btn_add_movies;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private String mParam1;
    private String mParam2;

    private Button Btn_setting_profile;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        linearLayout_add_movies = view.findViewById(R.id.linearLayout_add_movies);
        btn_add_movies = view.findViewById(R.id.btn_add_movies);
        linearLayout_add_movies.findViewById(R.id.linearLayout_add_movies).setVisibility(View.INVISIBLE);

        checkAdmin();
        addMoviesOnClick();
        return view;
    }

    public void addMoviesOnClick() {
        btn_add_movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(getActivity(), AddMoviesActivity.class);
                startActivity(i1);
            }
        });
    }


    private void checkAdmin(){

        String userId = user.getUid();
        mDatabase.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        DataSnapshot dataSnapshot = task.getResult();
                        int CHECK_ADMIN = dataSnapshot.child("Admin").getValue(Integer.class);
                        if (CHECK_ADMIN == 1){
                            linearLayout_add_movies.findViewById(R.id.linearLayout_add_movies).setVisibility(View.VISIBLE);
                        } else {
                            linearLayout_add_movies.findViewById(R.id.linearLayout_add_movies).setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        });
    }

}
package com.example.doantotnghiep_xemphim.Fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.doantotnghiep_xemphim.MoviesInformationActivity;
import com.example.doantotnghiep_xemphim.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class RecommendFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private String category;
    private GridLayout show_layout;
    private ProgressBar progressBar;
    private DatabaseReference mDatabase;

    public RecommendFragment() {
        // Required empty public constructor
    }


    public static RecommendFragment newInstance(String param1, String param2) {
        RecommendFragment fragment = new RecommendFragment();
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
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);

        show_layout = view.findViewById(R.id.show_layout);
        progressBar = view.findViewById(R.id.progressBar);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        browserData();
        return view;
    }

    private void browserData(){

        mDatabase.child("Movies").addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){

                    category = getArguments().getString("category", "").trim();
                    System.out.println("CATEGORY: " + category);
                    String[] words= category.split(",");
                    int random_int = (int)(Math.random() * (words.length));
                    System.out.println("random int: " + random_int);
                    System.out.println("random String: " + words[random_int]);
                    collectData((Map<String,Object>) dataSnapshot.getValue(), words[random_int]);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    private void collectData(Map<String,Object> listresult, String text) {
        for (Map.Entry<String, Object> entry : listresult.entrySet()){

            Map singleUser = (Map) entry.getValue();
            String value = singleUser.get("category").toString();
            if (value.contains(text)) {
                String name = singleUser.get("name").toString();
                getInfor(name);
            }
        }
    }
    private void getInfor(String IDMOVIES){
        mDatabase.child("Movies").child(IDMOVIES).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){

                        DataSnapshot dataSnapshot = task.getResult();
                        String nameMovies = String.valueOf(dataSnapshot.child("name").getValue());

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference listRef = storage.getReference().child("Images").child(nameMovies);
                        listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(ListResult listResult) {
                                for(StorageReference file:listResult.getItems()){
                                    file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            StorageReference storageReference = storage.getReferenceFromUrl(String.valueOf(uri));
                                            String link = storageReference.getName();
                                            if (link.equals(nameMovies + "_3x4")){
                                                String poster_link= "";
                                                poster_link = uri.toString();
                                                try {
                                                    setLayoutSearch(nameMovies, poster_link);
                                                } catch (NullPointerException e){
                                                    System.out.println("Lỗi: " + e);
                                                }
                                            }
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    private void setLayoutSearch(String text, String poster_link){
        LinearLayout parent = new LinearLayout(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(dpToPx(10), dpToPx(10), dpToPx(10), 0);
        parent.setLayoutParams(params);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity() ,MoviesInformationActivity.class);
                intent.putExtra("id",  text);
                startActivity(intent);
            }
        });

        CardView cardView1 = new CardView(getActivity());
        cardView1.setLayoutParams(new CardView.LayoutParams(dpToPx(162), dpToPx(217)));
        cardView1.setRadius(dpToPx(15));
        cardView1.setCardBackgroundColor(getResources().getColor(R.color.white));

        CardView.LayoutParams params3 = new CardView.LayoutParams(dpToPx(157), dpToPx(212), Gravity.CENTER);
        CardView cardView2 = new CardView(getActivity());
        cardView2.setLayoutParams(params3);
        cardView2.setRadius(dpToPx(15));

        ImageView imageView = new ImageView(getActivity());
        Glide.with(getActivity()).load(poster_link).into(imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        imageView.setBackgroundResource(R.drawable.null_image34);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(45));
        params2.setMargins(0, dpToPx(10), 0, 0);
        TextView textView = new TextView(getActivity());
        textView.setLayoutParams(params2);
        textView.setText(text);
        textView.setTextSize(18);
        textView.setMaxLines(2);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(getResources().getColor(R.color.white));

        show_layout.addView(parent);
        parent.addView(cardView1);
        cardView1.addView(cardView2);
        cardView2.addView(imageView);
        parent.addView(textView);

        progressBar.setVisibility(View.INVISIBLE);

    }
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
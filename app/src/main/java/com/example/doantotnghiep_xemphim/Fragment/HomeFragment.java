package com.example.doantotnghiep_xemphim.Fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doantotnghiep_xemphim.MoviesActivity;
import com.example.doantotnghiep_xemphim.MoviesInformationActivity;
import com.example.doantotnghiep_xemphim.R;
import com.example.doantotnghiep_xemphim.WatchMoviesActivity;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


public class HomeFragment extends Fragment {

    private TextView textView_category, textView_new_movies, textView_nation, textView_category_movies_home, textView_name_movies_home,
            btn_more_recommend_movies, btn_more_new_movies, btn_more_action_movies, btn_more_comedy_movies, btn_more_fiction_movies,
            btn_more_cartoon_movies;
    private Button btn_play;
    private ImageView imageView_poster_home_3x4;
    private ProgressBar progressBar_recommend_movies, progressBar_action_movies, progressBar_new_movies, progressBar_comedy_movies,
            progressBar_fiction_movies, progressBar_cartoon_movies;
    private DatabaseReference mDatabase;
    private LinearLayout recommend_movies_layout, action_movies_layout, new_movies_layout, btn_information_home, btn_trailer_home,
            comedy_movies_layout, fiction_movies_layout, cartoon_movies_layout;
    private ArrayList<String> listPoster;
    private String str1;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recommend_movies_layout = view.findViewById(R.id.recommend_movies_layout);
        textView_category_movies_home = view.findViewById(R.id.textView_category_movies_home);
        textView_name_movies_home = view.findViewById(R.id.textView_name_movies_home);
        textView_new_movies = view.findViewById(R.id.textView_new_movies);
        progressBar_recommend_movies = view.findViewById(R.id.progressBar_recommend_movies);
        progressBar_action_movies = view.findViewById(R.id.progressBar_action_movies);
        progressBar_new_movies = view.findViewById(R.id.progressBar_new_movies);
        action_movies_layout = view.findViewById(R.id.action_movies_layout);
        new_movies_layout = view.findViewById(R.id.new_movies_layout);
        imageView_poster_home_3x4 = view.findViewById(R.id.imageView_poster_home_3x4);
        btn_information_home = view.findViewById(R.id.btn_information_home);
        btn_trailer_home = view.findViewById(R.id.btn_trailer_home);
        btn_more_recommend_movies = view.findViewById(R.id.btn_more_recommend_movies);
        btn_more_new_movies = view.findViewById(R.id.btn_more_new_movies);
        btn_more_action_movies = view.findViewById(R.id.btn_more_action_movies);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        btn_play = view.findViewById(R.id.btn_play);
        textView_category = view.findViewById(R.id.textView_category);
        textView_nation = view.findViewById(R.id.textView_nation);
        btn_more_comedy_movies = view.findViewById(R.id.btn_more_comedy_movies);
        progressBar_comedy_movies = view.findViewById(R.id.progressBar_comedy_movies);
        comedy_movies_layout = view.findViewById(R.id.comedy_movies_layout);
        btn_more_fiction_movies = view.findViewById(R.id.btn_more_fiction_movies);
        progressBar_fiction_movies = view.findViewById(R.id.progressBar_fiction_movies);
        fiction_movies_layout = view.findViewById(R.id.fiction_movies_layout);
        btn_more_cartoon_movies = view.findViewById(R.id.btn_more_cartoon_movies);
        progressBar_cartoon_movies = view.findViewById(R.id.progressBar_cartoon_movies);
        cartoon_movies_layout = view.findViewById(R.id.cartoon_movies_layout);

        browserData();
        nemuCategoryOnClick();
        showMoreMoviesOnClick();
        return view;
    }

    private void showMoreMoviesOnClick(){
        btn_more_recommend_movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("Phim đề cử");
            }
        });
        btn_more_action_movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("Phim hành động");
            }
        });
        btn_more_new_movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("Phim mới");
            }
        });
        btn_more_comedy_movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("Phim hài hước");
            }
        });
        btn_more_fiction_movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("Phim viễn tưởng");
            }
        });
        btn_more_cartoon_movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("Phim hoạt hình");
            }
        });
    }
    private void sendData(String value){
        Intent intent = new Intent(getActivity() ,MoviesActivity.class);
        intent.putExtra("id",  value);
        startActivity(intent);
    }

    private void nemuCategoryOnClick(){
        textView_new_movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("Phim mới");
            }
        });

        textView_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pm = new PopupMenu(getActivity(), v);
                pm.getMenuInflater().inflate(R.menu.category_popup_menu, pm.getMenu());
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.category_item1:
                                sendData("Phim chiến tranh");
                                break;
                            case R.id.category_item2:
                                sendData("Phim hành động");
                                break;
                            case R.id.category_item3:
                                sendData("Phim tâm lý");
                                break;
                            case R.id.category_item4:
                                sendData("Phim viễn tưởng");
                                break;
                            case R.id.category_item5:
                                sendData("Phim kinh dị");
                                break;
                            case R.id.category_item6:
                                sendData("Phim hài hước");
                                break;
                        }
                        return true;
                    }
                });
                pm.show();
            }
        });

        textView_nation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PopupMenu pm = new PopupMenu(getActivity(), v);
                pm.getMenuInflater().inflate(R.menu.nation_popup_menu, pm.getMenu());
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.nation_item1:
                                sendData("Phim Việt Nam");
                                break;
                            case R.id.nation_item2:
                                sendData("Phim Thái Lan");
                                break;
                            case R.id.nation_item3:
                                sendData("Phim Hàn Quốc");
                                break;
                            case R.id.nation_item4:
                                sendData("Phim Mỹ");
                                break;
                            case R.id.nation_item5:
                                sendData("Phim Hồng Kông");
                                break;
                            case R.id.nation_item6:
                                sendData("Phim Âu");
                                break;
                        }
                        return true;
                    }
                });
                pm.show();
            }
        });
    }

    private void browserData(){

        mDatabase.child("Movies").addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    collectData((Map<String,Object>) dataSnapshot.getValue());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    private void collectData(Map<String,Object> listresult) {

        collectDataRecommend(listresult);

        collectDataAction(listresult, "category","Hành động", action_movies_layout, progressBar_action_movies);

        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        Date date = new Date();
        String year = dateFormat.format(date);
        collectDataAction(listresult, "year",year, new_movies_layout, progressBar_new_movies);
        collectDataAction(listresult, "category","Hài hước", comedy_movies_layout, progressBar_comedy_movies);
        collectDataAction(listresult, "category","Viễn tưởng", fiction_movies_layout, progressBar_fiction_movies);
        collectDataAction(listresult, "category","Hoạt hình", cartoon_movies_layout, progressBar_cartoon_movies);
    }

    private void collectDataRecommend(Map<String,Object> listresult) {

        ArrayList<String> listAfter = new ArrayList<String>();
        listPoster = new ArrayList<String>();
        int z = 0;
        for (Map.Entry<String, Object> entry : listresult.entrySet()){

            Map singleUser = (Map) entry.getValue();
            String name = singleUser.get("name").toString();
            listAfter.add(name);
            if (z < 3 ){
                listPoster.add(name);
            }
            z++;
        }

        Object[] arr = listAfter.toArray();
        for (int i = 0; i < (arr.length/2); i ++){
            if (i == 0 ){
            }
            getInfor(arr[i].toString(), recommend_movies_layout, progressBar_recommend_movies);
        }

        setBigPoster();
    }

    private void setBigPoster(){
        Object[] arr = listPoster.toArray();
        int random_int = (int)(Math.random() * (arr.length));
        System.out.println(" listPoster: " + listPoster.get(random_int));
        System.out.println(" arr: " + arr[random_int].toString());
        System.out.println(" ramdome: " + (random_int));
        setImagePoster(arr[random_int].toString());
        setValueMovies(arr[random_int].toString());
        btnPlayOnClick(arr[random_int].toString());

    }
    public void btnTrailerOnClick(String trailer) {
        btn_trailer_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(trailer);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.youtube");
                startActivity(intent);
            }
        });
    }

    private void btnPlayOnClick(String id){
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity() ,MoviesInformationActivity.class);
                intent.putExtra("id",  id);
                startActivity(intent);
            }
        });
        btn_information_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity() ,MoviesInformationActivity.class);
                intent.putExtra("id",  id);
                startActivity(intent);
            }
        });
    }

    private void setValueMovies(String id){
        mDatabase.child("Movies").child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        DataSnapshot dataSnapshot = task.getResult();
                        String trailer = String.valueOf(dataSnapshot.child("trailer").getValue());
                        String name = String.valueOf(dataSnapshot.child("name").getValue());
                        String category = String.valueOf(dataSnapshot.child("category").getValue());
                        textView_name_movies_home.setText(name);
                        textView_category_movies_home.setText(category);
                        btnTrailerOnClick(trailer);
                    }
                }

            }
        });
    }

    private void setImagePoster(String id){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReference().child("Images").child(id);

        listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference file:listResult.getItems()){
                    file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            StorageReference storageReference = storage.getReferenceFromUrl(String.valueOf(uri));
                            String link = storageReference.getName();
                            if (link.equals(id + "_3x4")){
                                str1 = uri.toString();
                            }
                            try {
                                Glide.with(getActivity()).load(str1).into(imageView_poster_home_3x4);
                            } catch (NullPointerException e){
                                System.out.println("Lỗi: " + e);
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

    private void collectDataAction(Map<String,Object> listresult, String values, String text, LinearLayout linearlayout, ProgressBar progressbar) {

        ArrayList<String> listAfter = new ArrayList<String>();

        for (Map.Entry<String, Object> entry : listresult.entrySet()){

            Map singleUser = (Map) entry.getValue();
            String value = singleUser.get(values).toString();
            if (value.contains(text)) {
                String name = singleUser.get("name").toString();
                listAfter.add(name);
            }
        }

        Object[] arr = listAfter.toArray();
        for (int i = 0; i < (arr.length/2); i ++){
            getInfor(arr[i].toString(), linearlayout, progressbar);
        }
    }

    private void getInfor(String IDMOVIES, LinearLayout linearlayout, ProgressBar progressbar){
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
                                                    setLayoutSearch(nameMovies, poster_link, linearlayout, progressbar);
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
    private void setLayoutSearch(String text, String poster_link, LinearLayout linearlayout, ProgressBar progressbar){
        LinearLayout parent = new LinearLayout(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dpToPx(10), dpToPx(20), 0);
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
        cardView1.setLayoutParams(new CardView.LayoutParams(dpToPx(165), dpToPx(220)));
        cardView1.setRadius(dpToPx(15));
        cardView1.setCardBackgroundColor(getResources().getColor(R.color.white));

        CardView.LayoutParams params3 = new CardView.LayoutParams(dpToPx(160), dpToPx(215), Gravity.CENTER);
        CardView cardView2 = new CardView(getActivity());
        cardView2.setLayoutParams(params3);
        cardView2.setRadius(dpToPx(15));

        ImageView imageView = new ImageView(getActivity());
        imageView.setId((int)1);
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

        linearlayout.addView(parent);
        parent.addView(cardView1);
        cardView1.addView(cardView2);
        cardView2.addView(imageView);
        parent.addView(textView);

        progressbar.setVisibility(View.INVISIBLE);

    }
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


}
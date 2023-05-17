package com.example.doantotnghiep_xemphim;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.rpc.Help;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class AddMoviesActivity extends AppCompatActivity {

    private LinearLayout link_movies;
    private ImageButton btn_add_more_link, btn_remove_link, btn_image_1, btn_image_2, btn_back;
    private Button btn_add;
    private EditText link_0, editText_year, editText_nation, editText_content, editText_trailer, editText_name, editText_cast, editText_director, editText_time;
    private CheckBox checkbox_1, checkbox_2, checkbox_3, checkbox_4, checkbox_5, checkbox_6, checkbox_7, checkbox_8, checkbox_9, checkbox_10, checkbox_11, checkbox_12, checkbox_13;
    private int EDITTEXT_COUNT = 2;
    private EditText editText_link;
    private DatabaseReference mDatabase;
    private ImageView image1, image2;
    private int SELECT_PICTURE = 200;
    private int IMAGE_CLICK = 1;
    private StorageReference storageRef;
    private Uri ImageUri_1, ImageUri_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movies);

        initUi();
        btnAddMoreLinkOnClick();
        btnRemoveLinkOnClick();
        btnAddOnClick();
        addImage();
        btnBackOnClick();
    }
    private void initUi(){
        link_movies = findViewById(R.id.link_movies);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        btn_image_1 = findViewById(R.id.btn_image_1);
        btn_image_2 = findViewById(R.id.btn_image_2);
        btn_back = findViewById(R.id.btn_back);
        btn_add_more_link = findViewById(R.id.btn_add_more_link);
        btn_remove_link = findViewById(R.id.btn_remove_link);
        btn_add = findViewById(R.id.btn_add);
        link_0 = findViewById(R.id.link_0);
        editText_year = findViewById(R.id.editText_year);
        editText_nation = findViewById(R.id.editText_nation);
        editText_trailer = findViewById(R.id.editText_trailer);
        editText_content = findViewById(R.id.editText_content);
        editText_name = findViewById(R.id.editText_name);
        editText_cast = findViewById(R.id.editText_cast);
        editText_director = findViewById(R.id.editText_director);
        editText_time = findViewById(R.id.editText_time);
        checkbox_1 = findViewById(R.id.checkbox_1);
        checkbox_2 = findViewById(R.id.checkbox_2);
        checkbox_3 = findViewById(R.id.checkbox_3);
        checkbox_4 = findViewById(R.id.checkbox_4);
        checkbox_5 = findViewById(R.id.checkbox_5);
        checkbox_6 = findViewById(R.id.checkbox_6);
        checkbox_7 = findViewById(R.id.checkbox_7);
        checkbox_8 = findViewById(R.id.checkbox_8);
        checkbox_9 = findViewById(R.id.checkbox_9);
        checkbox_10 = findViewById(R.id.checkbox_10);
        checkbox_11 = findViewById(R.id.checkbox_11);
        checkbox_12 = findViewById(R.id.checkbox_12);
        checkbox_13 = findViewById(R.id.checkbox_13);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void btnBackOnClick(){
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private boolean checkError(){
        int check = 0;

        if (link_0.getText().toString().trim().matches("")){
            check = 1;
        }
        EditText et;
        if (EDITTEXT_COUNT > 2){
            for (int i = 2; i < EDITTEXT_COUNT; i++) {
                et = link_movies.findViewById(i);
                if (et.getText().toString().trim().matches("")){
                    check = 1;
                }
            }
        }

        if (!checkbox_1.isChecked() && !checkbox_2.isChecked() && !checkbox_3.isChecked() && !checkbox_4.isChecked()
                && !checkbox_5.isChecked() && !checkbox_6.isChecked() && !checkbox_7.isChecked() && !checkbox_8.isChecked()
                && !checkbox_9.isChecked() && !checkbox_10.isChecked() && !checkbox_11.isChecked() && !checkbox_12.isChecked() && !checkbox_13.isChecked()){
            check = 1;
        }

        if (editText_name.getText().toString().trim().matches("") || editText_year.getText().toString().trim().matches("")
                || editText_nation.getText().toString().trim().matches("") || editText_trailer.getText().toString().trim().matches("")
                || editText_content.getText().toString().trim().matches("") || editText_cast.getText().toString().trim().matches("")
                || editText_director.getText().toString().trim().matches("") || editText_time.getText().toString().trim().matches("")){
            check = 1;
        }

        if (ImageUri_1==null || ImageUri_2==null){
            check = 1;
        }

        if (check == 0){
            return true;
        } else {
            return false;
        }
    }

    private void addImage(){
        btn_image_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IMAGE_CLICK = 1;
                imageChooser();
            }
        });
        btn_image_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IMAGE_CLICK = 2;
                imageChooser();

            }
        });
    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    if (IMAGE_CLICK == 1){
                        image1.setImageURI(selectedImageUri);
                        ImageUri_1 = selectedImageUri;
                    } else {
                        image2.setImageURI(selectedImageUri);
                        ImageUri_2 = selectedImageUri;
                    }
                }
            }
        }
    }

    private void uploadImage(){
        String NAME = editText_name.getText().toString();
        for (int i = 0; i<= 1; i++){
            if (i == 0){
                String SIZE = "3x4";
                firebaseUploadImage(NAME, SIZE, ImageUri_1);
            } else if (i == 1){
                String SIZE = "4x3";
                firebaseUploadImage(NAME, SIZE, ImageUri_2);
            }
        }
    }

    private void firebaseUploadImage(String name, String size, Uri imageUri){
        storageRef = FirebaseStorage.getInstance().getReference().child("Images").child(name).child(name + "_" + size);
        UploadTask uploadTask = storageRef.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(AddMoviesActivity.this,"Thêm ảnh "+ size +" thất bại! vui lòng thử lại sau.", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });

    }

    private String getCategory(){
        String CATEGORY = "";
        if (checkbox_1.isChecked()){
            CATEGORY += checkbox_1.getText() + ", ";
        }
        if (checkbox_2.isChecked()){
            CATEGORY += checkbox_2.getText().toString() + ", ";
        }
        if (checkbox_3.isChecked()){
            CATEGORY += checkbox_3.getText().toString() + ", ";
        }
        if (checkbox_4.isChecked()){
            CATEGORY += checkbox_4.getText().toString() + ", ";
        }
        if (checkbox_5.isChecked()){
            CATEGORY += checkbox_5.getText().toString() + ", ";
        }
        if (checkbox_6.isChecked()){
            CATEGORY += checkbox_6.getText().toString() + ", ";
        }
        if (checkbox_7.isChecked()){
            CATEGORY += checkbox_7.getText().toString() + ", ";
        }
        if (checkbox_8.isChecked()){
            CATEGORY += checkbox_8.getText().toString() + ", ";
        }
        if (checkbox_9.isChecked()){
            CATEGORY += checkbox_9.getText().toString() + ", ";
        }
        if (checkbox_10.isChecked()){
            CATEGORY += checkbox_10.getText().toString() + ", ";
        }
        if (checkbox_11.isChecked()){
            CATEGORY += checkbox_11.getText().toString() + ", ";
        }
        if (checkbox_12.isChecked()){
            CATEGORY += checkbox_12.getText().toString() + ", ";
        }
        if (checkbox_13.isChecked()){
            CATEGORY += checkbox_13.getText().toString() + ", ";
        }

        return CATEGORY;
    }


    private void btnAddOnClick(){
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkError() == true){
                    String YEAR = editText_year.getText().toString();
                    String CAST = editText_cast.getText().toString();
                    String TRAILER = editText_trailer.getText().toString();
                    String CONTENT = editText_content.getText().toString();
                    String NATIONS = editText_nation.getText().toString();
                    String NAME = editText_name.getText().toString();
                    String DIRECTOR = editText_director.getText().toString();
                    String TIME = editText_time.getText().toString();

                    uploadImage();

                    addMovies(NAME, CAST, TRAILER, CONTENT, NATIONS, getCategory(), YEAR, DIRECTOR,TIME);

                    for (int i = 1; i < EDITTEXT_COUNT; i++){
                        String TAP = "Tap_" + i;
                        String LINK = "";
                        if ( i == 1){
                            LINK = link_0.getText().toString();
                        } else {
                            EditText et= link_movies.findViewById(i);
                            LINK = et.getText().toString();
                        }
                        mDatabase.child("Movies").child(NAME).child("link").child(TAP).setValue(LINK).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AddMoviesActivity.this,"Thêm phim thành công!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(AddMoviesActivity.this,"Thêm phim thất bại! vui lòng thử lại sau.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    Toast.makeText(AddMoviesActivity.this, "thanh cong", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddMoviesActivity.this, "Vui lòng nhập đầy đủ thông tin phim", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void createEditText(){
        editText_link = new EditText(this);
        editText_link.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        editText_link.setHint("link " + EDITTEXT_COUNT);
        editText_link.setId(EDITTEXT_COUNT);
        link_movies.addView(editText_link);
        EDITTEXT_COUNT++;
    }
    private void btnAddMoreLinkOnClick(){
        btn_add_more_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEditText();
            }
        });
    }

    private void btnRemoveLinkOnClick(){
        btn_remove_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EDITTEXT_COUNT > 2){
                    EDITTEXT_COUNT--;
                    link_movies.removeView(findViewById(EDITTEXT_COUNT));
                }
            }
        });
    }

    private String getDateTime(String format){
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        String time = dateFormat.format(date);
        return time;
    }

    private void addMovies(String name, String cast, String trailer, String content, String nation, String category, String year, String director, String time){
        stars stars = new stars(0,0);

        String id = name.replaceAll(" ", "") + "_" + getDateTime("HH:mm:ss_dd_MM_yyyy") ;

        int viewcount = 0;

        Movies movies = new Movies(name, cast, trailer, content, nation, category, year, stars, id, String.valueOf(viewcount), director, time, getDateTime("HH:mm:ss dd/MM/yyyy"));

        mDatabase.child("Movies").child(name).setValue(movies).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddMoviesActivity.this,"Thêm phim thành công!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddMoviesActivity.this,"Thêm phim thất bại! vui lòng thử lại sau.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class Movies {
        public String name, cast, trailer, content, nation, category, year, id, viewcount, director, time, postingdate;
        public stars stars;

        public Movies(){

        }

        public Movies(String name, String cast, String trailer, String content, String nation, String category,
                      String year, stars stars, String id, String viewcount, String director, String time, String postingdate) {
            this.name = name;
            this.cast = cast;
            this.trailer = trailer;
            this.content = content;
            this.nation = nation;
            this.category = category;
            this.year = year;
            this.stars = stars;
            this.id = id;
            this.viewcount = viewcount;
            this.director = director;
            this.time = time;
            this.postingdate = postingdate;
        }
    }

    public class stars {
        public int star, total_rating;

        public stars(){

        }

        public stars(int star, int total_rating) {
            this.star = star;
            this.total_rating = total_rating;
        }
    }
}
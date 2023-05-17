package com.example.doantotnghiep_xemphim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doantotnghiep_xemphim.Fragment.ChangePasswordFragment;
import com.example.doantotnghiep_xemphim.Fragment.UserProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ChangeInformationActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private ScrollView croll;
    private ImageView dropdown,Btn_change_information_back, Btn_save_information;
    private EditText Edittext_change_infor_birthday, Edittext_change_infor_name, Edittext_change_infor_email;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private CardView cardView_avatar;
    private ImageView imageView_avatar;
    private String userId, email, name, birthday;
    private boolean drop = false;
    private int SELECT_PICTURE = 200;
    private Uri ImageUri;
    private StorageReference storageRef;

    final Calendar myCalendar= Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_information);

        initUi();
        getUserInfor();
        btnBackOnclick();
        btnSaveOnClick();
        btnCardViewAvatarOnClick();
        pickDate();
       // changePassWordDropDown();

    }
    private void initUi(){
        progressDialog = new ProgressDialog(this);
        croll = findViewById(R.id.croll);
      //  dropdown = findViewById(R.id.dropdown);
        Btn_change_information_back = findViewById(R.id.btn_change_information_back);
        Btn_save_information = findViewById(R.id.btn_save_information);
        Edittext_change_infor_birthday = findViewById(R.id.edittext_change_infor_birthday);
        Edittext_change_infor_name = findViewById(R.id.edittext_change_infor_name);
        Edittext_change_infor_email = findViewById(R.id.edittext_change_infor_email);
        cardView_avatar = findViewById(R.id.cardView_avatar);
        imageView_avatar = findViewById(R.id.imageView_avatar);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void btnBackOnclick(){
        Btn_change_information_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void btnCardViewAvatarOnClick(){
        cardView_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    imageView_avatar.setImageURI(selectedImageUri);
                    ImageUri = selectedImageUri;
                }
            }
        }
    }
    public void btnSaveOnClick(){
        Btn_save_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });
    }

    private void updateUserProfile(){
        String emailUpdate = Edittext_change_infor_email.getText().toString().trim();
        String nameUpdate = Edittext_change_infor_name.getText().toString().trim();
        String birthdaylUpdate = Edittext_change_infor_birthday.getText().toString().trim();
        userId = user.getUid();
        user.updateEmail(emailUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mDatabase.child(userId).child("email").setValue(emailUpdate);
                            mDatabase.child(userId).child("name").setValue(nameUpdate);
                            mDatabase.child(userId).child("birthday").setValue(birthdaylUpdate);
                            finish();
                            Toast.makeText(ChangeInformationActivity.this,"Cập nhật thông tin thành công!",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChangeInformationActivity.this,"Cập nhật thông tin thất bại!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //up ảnh người dùng lên store
        storageRef = FirebaseStorage.getInstance().getReference().child("UsersAvatar").child(userId);
        UploadTask uploadTask = storageRef.child("avatar").putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });

        //lấy link ảnh người dùng từ store để thêm vào thông tin người dùng
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference file:listResult.getItems()){
                    file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String avatar_link = uri.toString();
                            mDatabase.child(userId).child("avatar").setValue(avatar_link);
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

    private void getUserInfor(){
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.show();
        userId = user.getUid();

        mDatabase.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){

                        DataSnapshot dataSnapshot = task.getResult();
                        name = String.valueOf(dataSnapshot.child("name").getValue());
                        email = String.valueOf(dataSnapshot.child("email").getValue());
                        birthday = String.valueOf(dataSnapshot.child("birthday").getValue());
                        String avatar = String.valueOf(dataSnapshot.child("avatar").getValue());

                        Glide.with(ChangeInformationActivity.this).load(avatar).into(imageView_avatar);

                        Edittext_change_infor_name.setText(name);
                        Edittext_change_infor_email.setText(email);
                        Edittext_change_infor_birthday.setText(birthday);

                    }else {
                        Toast.makeText(ChangeInformationActivity.this,"Không thể tìm thấy tài khoản của bạn!",Toast.LENGTH_SHORT).show();

                    }
                }
                progressDialog.dismiss();
            }
        });
    }

//
//    private void changePassWordDropDown(){
//        dropdown.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (drop == false){
//                    replaceFragment(new ChangePasswordFragment());
//                    dropdown.setBackgroundResource(R.drawable.arrow_drop_up);
//                    drop = true;
//                } else {
//                    LinearLayout frag =(LinearLayout) findViewById(R.id.fragment_pass);
//                    frag.setVisibility(View.GONE);
//                    dropdown.setBackgroundResource(R.drawable.arrow_drop_down);
//                    drop = false;
//                }
//            }
//        });
//    }
//
//    private void replaceFragment(Fragment fragment){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.passwordFragment,fragment);
//        fragmentTransaction.commit();
//    }

    public void pickDate(){
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        Edittext_change_infor_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ChangeInformationActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        Edittext_change_infor_birthday.setText(dateFormat.format(myCalendar.getTime()));
    }
}
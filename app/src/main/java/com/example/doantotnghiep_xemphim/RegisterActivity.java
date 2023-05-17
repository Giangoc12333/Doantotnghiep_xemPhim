package com.example.doantotnghiep_xemphim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private TextView Tv_login;
    private Button Btn_register;
    private EditText Edt_email, Edt_password, Edt_confirm_password, Edt_register_user_birthday, Edt_register_user_name;
    private TextInputLayout tiplayout_email, tiplayout_pass, tiplayout_confirm_pass, tiplayout_register_user_name, tiplayout_register_user_birthday;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    private String userId;
    private CheckBox Cb_register_confirm;

    final Calendar myCalendar= Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUi();
        pickDate();
        initListener();
    }


    private void initUi(){
        Tv_login = findViewById(R.id.tv_login);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Edt_email = findViewById(R.id.edittext_register_email);
        Edt_password = findViewById(R.id.edittext_register_password);
        Edt_confirm_password = findViewById(R.id.edittext_register_confirm_password);
        Edt_register_user_birthday = findViewById(R.id.edittext_register_user_birthday);
        Edt_register_user_name = findViewById(R.id.edittext_register_user_name);

        Cb_register_confirm = findViewById(R.id.cb_register_confirm);

        Btn_register = findViewById(R.id.btn_register);

        tiplayout_email = findViewById(R.id.tiplayout_register_email);
        tiplayout_pass = findViewById(R.id.tiplayout_register_password);
        tiplayout_confirm_pass = findViewById(R.id.tiplayout_register_confirm_password);
        tiplayout_register_user_name = findViewById(R.id.tiplayout_register_user_name);
        tiplayout_register_user_birthday = findViewById(R.id.tiplayout_register_user_birthday);

        progressDialog = new ProgressDialog(this);
    }

    private void initListener() {
        Tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i1);
            }
        });

        btnRegisterOnClick();
    }

    public void btnRegisterOnClick(){
        Btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(Edt_register_user_name.getText().toString())) {
                    tiplayout_register_user_name.setError(getString(R.string.error_not_be_empty));
                }
                else {
                    tiplayout_register_user_name.setErrorEnabled(false);
                }
                if (TextUtils.isEmpty(Edt_register_user_birthday.getText().toString())) {
                    tiplayout_register_user_birthday.setError(getString(R.string.error_not_be_empty));
                }
                else {
                    tiplayout_register_user_birthday.setErrorEnabled(false);
                }
                if (TextUtils.isEmpty(Edt_email.getText().toString())) {
                    tiplayout_email.setError(getString(R.string.error_not_be_empty));
                }
                else {
                    tiplayout_email.setErrorEnabled(false);
                }
                if (TextUtils.isEmpty(Edt_password.getText().toString())) {
                    tiplayout_pass.setError(getString(R.string.error_not_be_empty));
                }
                else {
                    tiplayout_pass.setErrorEnabled(false);
                }
                if (TextUtils.isEmpty(Edt_confirm_password.getText().toString())) {
                    tiplayout_confirm_pass.setError(getString(R.string.error_not_be_empty));
                }
                else {
                    tiplayout_confirm_pass.setErrorEnabled(false);
                }
                if (!Cb_register_confirm.isChecked()){
                    Toast.makeText(RegisterActivity.this, R.string.error_agree, Toast.LENGTH_LONG).show();
                }

                String email = Edt_email.getText().toString().trim();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (!email.matches(emailPattern))
                {
                    tiplayout_email.setError(getString(R.string.error_email_1));
                }

                if (Edt_password.getText().toString().trim().length() >= 6){
                    if (Edt_email.getText().toString().trim().equalsIgnoreCase("") == false
                            && Edt_password.getText().toString().trim().equalsIgnoreCase("")  == false
                            && Edt_register_user_name.getText().toString().trim().equalsIgnoreCase("")  == false
                            && Edt_register_user_birthday.getText().toString().trim().equalsIgnoreCase("")  == false
                            && Edt_confirm_password.getText().toString().trim().equalsIgnoreCase("") == false
                            && email.matches(emailPattern)
                            && Cb_register_confirm.isChecked()){
                        if (Edt_password.getText().toString().trim().equals(Edt_confirm_password.getText().toString().trim())){
                            createAccount();
                        } else {
                            tiplayout_confirm_pass.setError(getString(R.string.error_password_1));
                        }
                    }
                } else {
                    tiplayout_pass.setError(getString(R.string.error_password_more_chr));
                }
            }
        });
    }


    private void createAccount() {

        final String strEmail = Edt_email.getText().toString().trim();
        String strPass = Edt_password.getText().toString().trim();
        final String strName = Edt_register_user_name.getText().toString().trim();
        final String strBirthday = Edt_register_user_birthday.getText().toString().trim();

        progressDialog.setMessage(getString(R.string.progressDialog_register_loading));
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(strEmail, strPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userId = mAuth.getCurrentUser().getUid();
                            writeNewUser(strName, strEmail, strBirthday);

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void writeNewUser(String name, String email , String birthday) {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String joindate = dateFormat.format(date);
        int admin = 0;

        String avatar = "https://firebasestorage.googleapis.com/v0/b/realphim-9e426.appspot.com/o/UsersAvatar%2FDefaulstAvatar%2Fpngtree.jpg?alt=media&token=bda859a2-a185-4e5e-b9cb-cd7a7c2589f3";
        User user = new User(name, email, birthday, joindate, admin, avatar);

        mDatabase.child("Users").child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, R.string.toast_register_succes, Toast.LENGTH_LONG).show();
                    Intent i1 = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(i1);
                    finishAffinity();
                }
            }
        });
    }

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
        Edt_register_user_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(RegisterActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        Edt_register_user_birthday.setText(dateFormat.format(myCalendar.getTime()));
    }

}
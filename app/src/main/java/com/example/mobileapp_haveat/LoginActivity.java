package com.example.mobileapp_haveat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button DonarLoginBtn, DonarRegisterBtn;
    private TextView DonarRegisterLink, DonarStatus, Donar_orSigninWith, DonarHaveAccount;
    private EditText DonarEmail, DonarPassword;
    private ProgressDialog loading;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DonarLoginBtn = findViewById(R.id.donar_login_button);
        DonarRegisterBtn = findViewById(R.id.donar_register_button);
        DonarRegisterLink = findViewById(R.id.donar_newAccountLink);
        DonarStatus = findViewById(R.id.donar_text);
        Donar_orSigninWith = findViewById(R.id.donar_orSigninWith);
        DonarEmail = findViewById(R.id.donar_login_email);
        DonarPassword = findViewById(R.id.donar_login_password);
        DonarHaveAccount = findViewById(R.id.donar_haveAccount);
        loading = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        DonarRegisterBtn.setVisibility(View.INVISIBLE);
        DonarHaveAccount.setVisibility(View.INVISIBLE);
        DonarRegisterBtn.setEnabled(false);

        DonarRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DonarLoginBtn.setVisibility(View.INVISIBLE);
                DonarRegisterLink.setVisibility(View.INVISIBLE);
                DonarStatus.setText("Register Donar");
                Donar_orSigninWith.setText("or, signup with");
                DonarHaveAccount.setVisibility(View.VISIBLE);
                DonarRegisterBtn.setVisibility(View.VISIBLE);
                DonarRegisterBtn.setEnabled(true);

            }
        });

        DonarLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = DonarEmail.getText().toString();
                String password = DonarPassword.getText().toString();

                LoginDonar(email, password);
            }
        });

        DonarRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = DonarEmail.getText().toString();
                String password = DonarPassword.getText().toString();

                RegisterDonar(email, password);
            }
        });

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser!=null){
//            SendUserToDonarMainActivity();
//        }
//    }

    private void LoginDonar(String email, String password) {

        if(TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this, "Please enter your email!", Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Please enter your password!", Toast.LENGTH_LONG).show();
        }
        else{
            loading.show();
            loading.setTitle("Donar Login");
            loading.setMessage("Please wait! Login is progress.");
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        loading.dismiss();
                        SendUserToDonarMainActivity();
                        Toast.makeText(LoginActivity.this, "You are Logged in!", Toast.LENGTH_LONG).show();

                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Error! Try again.", Toast.LENGTH_LONG).show();
                        loading.dismiss();
                    }
                }
            });
        }

    }

    private void SendUserToDonarMainActivity() {
        Intent sendToHome = new Intent(LoginActivity.this, UserHome.class);
        sendToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendToHome);
        finish();
    }


    private void RegisterDonar(String email, String password) {

        if(TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this, "Please enter your email!", Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Please enter your password!", Toast.LENGTH_LONG).show();
        }

        else{
            loading.setTitle("Donar Registration");
            loading.setMessage("Please wait! Registration is progress.");
            loading.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "You are Registered!", Toast.LENGTH_LONG).show();
                        loading.dismiss();
                        DonarLoginBtn.setVisibility(View.VISIBLE);
                        DonarRegisterLink.setVisibility(View.INVISIBLE);
                        DonarStatus.setText("Login Customer");
                        Donar_orSigninWith.setText("or, signin with");
                        DonarRegisterBtn.setVisibility(View.INVISIBLE);
                        DonarRegisterBtn.setEnabled(false);
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Error!", Toast.LENGTH_LONG).show();
                        loading.dismiss();
                    }
                }
            });
        }

    }
}
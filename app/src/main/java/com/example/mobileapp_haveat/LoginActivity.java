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

    private Button DonarLoginBtn, DonarRegisterBtn;//two buttons
    private TextView DonarRegisterLink, DonarStatus, Donar_orSigninWith, DonarHaveAccount;
    private EditText DonarEmail, DonarPassword;//input
    private ProgressDialog loading;
    private FirebaseAuth mAuth;//reference to the firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //setting variables to particular IDs
        DonarLoginBtn = findViewById(R.id.donar_login_button);
        DonarRegisterBtn = findViewById(R.id.donar_register_button);
        DonarRegisterLink = findViewById(R.id.donar_newAccountLink);
        DonarStatus = findViewById(R.id.donar_text);
        Donar_orSigninWith = findViewById(R.id.donar_orSigninWith);
        DonarEmail = findViewById(R.id.donar_login_email);
        DonarPassword = findViewById(R.id.donar_login_password);
        DonarHaveAccount = findViewById(R.id.donar_haveAccount);
        loading = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();//returns the current users instance

        DonarRegisterBtn.setVisibility(View.INVISIBLE);//setting register button invisible so user can only see login button.
        DonarHaveAccount.setVisibility(View.INVISIBLE);//"Already have an account" text should be invisible while login
        DonarRegisterBtn.setEnabled(false);//Register button won't function at this moment

        //When user want to create new account, it will enable register button and "Already have account" text
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

        //When login button is clicked,
        DonarLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = DonarEmail.getText().toString();
                String password = DonarPassword.getText().toString();

                //pass input email and password verify user
                LoginDonar(email, password);
            }
        });

        //when register button is clicked,
        DonarRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = DonarEmail.getText().toString();
                String password = DonarPassword.getText().toString();

                //pass input email and password verify user
                RegisterDonar(email, password);
            }
        });

    }

    //OnStart function will be called as the user enters this activity.
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){//We want to verify if the user is existing in our firebase
            SendUserToDonarMainActivity();//then no need to sign in again.
        }
    }

    private void LoginDonar(String email, String password) {

        if(TextUtils.isEmpty(email)){//If email is not entered, lt user know to enter it
            Toast.makeText(LoginActivity.this, "Please enter your email!", Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(password)){//If password is not entered, lt user know to enter it
            Toast.makeText(LoginActivity.this, "Please enter your password!", Toast.LENGTH_LONG).show();
        }
        else{//then verify if these exists in firebase
            loading.show();
            loading.setTitle("User Login");//This is progress bar, displayed when app is verifying the user
            loading.setMessage("Please wait! Login is progress.");
            //passing email and password to firebase
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {//after the verification is completed
                    if(task.isSuccessful()){//if successful, intent to home
                        loading.dismiss();
                        SendUserToDonarMainActivity();
                        Toast.makeText(LoginActivity.this, "You are Logged in!", Toast.LENGTH_LONG).show();

                    }
                    else{//if fail, user need to register first
                        Toast.makeText(LoginActivity.this, "Please check email and password.", Toast.LENGTH_LONG).show();
                        loading.dismiss();
                    }
                }
            });
        }

    }

    private void SendUserToDonarMainActivity() {//Intent to home activity
        Intent sendToHome = new Intent(LoginActivity.this, UserHome.class);
        sendToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendToHome);
        finish();
    }


    private void RegisterDonar(String email, String password) {//to register the user

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

            //generate a user by given email and password.
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){//after saving info, user needs to login.
                        Toast.makeText(LoginActivity.this, "You are Registered!", Toast.LENGTH_LONG).show();
                        loading.dismiss();
                        DonarLoginBtn.setVisibility(View.VISIBLE);
                        DonarRegisterLink.setVisibility(View.INVISIBLE);
                        DonarStatus.setText("Login Customer");
                        Donar_orSigninWith.setText("or, signin with");
                        DonarRegisterBtn.setVisibility(View.INVISIBLE);
                        DonarRegisterBtn.setEnabled(false);
                    }
                    else{//error if internet connection has problems.
                        Toast.makeText(LoginActivity.this, "Error!", Toast.LENGTH_LONG).show();
                        loading.dismiss();
                    }
                }
            });
        }

    }
}
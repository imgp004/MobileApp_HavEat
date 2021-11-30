package com.example.mobileapp_haveat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserHome extends AppCompatActivity {

    private NavigationView navigationView;
    private RecyclerView postList;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    private CircleImageView NavProfileImg;
    private TextView NavUsername;
    String userId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        toolbar = findViewById(R.id.main_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        firebaseAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userId = firebaseAuth.getCurrentUser().getUid();
        drawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(UserHome.this, drawerLayout,R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigationview);
        View navigation = navigationView.inflateHeaderView(R.layout.header);
        NavProfileImg = navigation.findViewById(R.id.header_profilepic);
        NavUsername = navigation.findViewById(R.id.header_username);
        
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });

        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.hasChild("profileImage")){
                        String img = snapshot.child("profileImage").getValue().toString();
                        Picasso.get().load(img).placeholder(R.drawable.profile).into(NavProfileImg);
                    }
                    if(snapshot.hasChild("fullName")) {
                        String name = snapshot.child("fullName").getValue().toString();
                        NavUsername.setText(name);
                    }
                    else{
                        Toast.makeText(UserHome.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser==null){
            SendUserToLogin();
        }
        else{
            CheckUserExistance();
        }
    }

    private void SendUserToLogin() {
        Intent loginIntent = new Intent(UserHome.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void CheckUserExistance() {
        final String userId = firebaseAuth.getCurrentUser().getUid();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(userId)){
                    SendUserToProfile();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendUserToProfile() {
        Intent profileIntent = new Intent(UserHome.this, UserProfile.class);
        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileIntent);
        finish();
    }

    //This function enables a button on toolbar to pull out the drawer on left.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {

        switch(item.getItemId()){
            case R.id.donar_menu_home:
                Toast.makeText(UserHome.this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.donar_menu_post:
                Toast.makeText(UserHome.this, "Post", Toast.LENGTH_SHORT).show();
                Intent sendToPost = new Intent(UserHome.this, UserPost.class);
                startActivity(sendToPost);
                finish();
                break;
            case R.id.donar_menu_profile:
                Toast.makeText(UserHome.this, "Profile", Toast.LENGTH_SHORT).show();
                SendUserToProfile();
                break;
            case R.id.donar_menu_settings:
                Toast.makeText(UserHome.this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.donar_menu_logout:
                firebaseAuth.signOut();
                SendUserToLogin();
                break;
        }

    }
}
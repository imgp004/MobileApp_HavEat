package com.example.mobileapp_haveat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    //declaring variables.
    private NavigationView navigationView;
    private RecyclerView postList;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;//The bar on top.
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth firebaseAuth;//firebase reference
    private DatabaseReference userRef;//database reference
    private CircleImageView NavProfileImg;
    private TextView NavUsername;
    String userId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        navigationView = findViewById(R.id.navigationview);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);//whenever any option is clicked in drawer, it will call this function
                return false;
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");//reference to user node in database
        userId = firebaseAuth.getCurrentUser().getUid();//getting current user id

        toolbar = findViewById(R.id.main_app_bar);//Binding the app_bar layout in home screen
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");//Giving title

        drawerLayout = findViewById(R.id.drawerLayout);//drawer layout to draw different options
        actionBarDrawerToggle = new ActionBarDrawerToggle(UserHome.this, drawerLayout,R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        //when button is pressed, drawer will pulled out
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View navigation = navigationView.inflateHeaderView(R.layout.header);//bonding the profile and name to drawer layout
        //setting the variables
        NavProfileImg = navigation.findViewById(R.id.header_profilepic);
        NavUsername = navigation.findViewById(R.id.header_username);

        //database reference to retrive the profile image and full name
        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override//retriving the profile picture and name from database
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.hasChild("profileImage")){
                        String img = snapshot.child("profileImage").getValue().toString();
                        //load the image in circular image view
                        Picasso.get().load(img).placeholder(R.drawable.profile).into(NavProfileImg);
                    }
                    if(snapshot.hasChild("fullName")) {
                        //setting the name to username in database
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
    protected void onStart() {//on start, verify user if they exist in firebase
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser==null){
            SendUserToLogin();//if not existing, send to login
        }
        else{
            CheckUserExistance();//else check if we have profile image, full name, town fo user
        }
    }

    private void SendUserToLogin() {//inent to login activity
        Intent loginIntent = new Intent(UserHome.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();//User cannot press back button to come on previous screen
    }

    private void CheckUserExistance() {//If user information is not existing in database, send to profile
        final String userId = firebaseAuth.getCurrentUser().getUid();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(userId)){
                    SendUserToProfile();//if no imformation, then intent to profile before anything else.
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendUserToProfile() {//intent to profile to get all user information
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

    private void UserMenuSelector(MenuItem item) {//tasks given when a particular option is selected in drawer layout

        switch(item.getItemId()){
            case R.id.donar_menu_home:
                Toast.makeText(UserHome.this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.donar_menu_post://intent to post activity
                Toast.makeText(UserHome.this, "Post", Toast.LENGTH_SHORT).show();
                Intent sendToPost = new Intent(UserHome.this, UserPost.class);
                startActivity(sendToPost);
                finish();
                break;
            case R.id.donar_menu_profile://intent to profile activity
                Toast.makeText(UserHome.this, "Profile", Toast.LENGTH_SHORT).show();
                SendUserToProfile();
                break;
            case R.id.donar_menu_settings://Still need to programmed
                Toast.makeText(UserHome.this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.donar_menu_logout://logout user
                firebaseAuth.signOut();
                SendUserToLogin();
                break;
        }

    }
}
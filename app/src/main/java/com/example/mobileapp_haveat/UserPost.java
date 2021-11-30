package com.example.mobileapp_haveat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UserPost extends AppCompatActivity {

    private Button upload;
    private Button searchInDrive;
    private EditText Description;
    private ImageView imageView;
    private Toolbar toolbar;
    private static int photo = 1;
    private Uri imgUri;
    String description, userId;
    private String saveDate, saveTime, RandomId, link;
    private FirebaseAuth firebaseAuth;
    private StorageReference postRef;
    private DatabaseReference userRef, postNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);

        toolbar = findViewById(R.id.post_appBarLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Upload Post");

        upload = findViewById(R.id.uploadBtn);
        imageView = findViewById(R.id.PostPicture);
        searchInDrive = findViewById(R.id.selectBtn);
        Description = findViewById(R.id.postDescription);
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        postRef = FirebaseStorage.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postNode = FirebaseDatabase.getInstance().getReference().child("Posts");

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent SendToHome = new Intent(UserPost.this, UserHome.class);
            SendToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(SendToHome);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void SelectFromDrive(View view) {
        Intent goToGallery = new Intent();
        goToGallery.setAction(Intent.ACTION_GET_CONTENT);
        goToGallery.setType("image/*");
        startActivityForResult(goToGallery, photo);
    }

    public void UploadPost(View view) {

        description = Description.getText().toString();
        if (imgUri == null) {
            Toast.makeText(UserPost.this, "Please select image.", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(description)) {
            Toast.makeText(UserPost.this, "Please write item description.", Toast.LENGTH_SHORT).show();
        } else {
            SaveImage();
            SavePostInfoInDatabase();
        }

    }

    private void SaveImage() {

        Calendar date = Calendar.getInstance();
        SimpleDateFormat currDate = new SimpleDateFormat("MMMM-dd-yyyy");
        saveDate = currDate.format(date.getTime());

        Calendar time = Calendar.getInstance();
        SimpleDateFormat currTime = new SimpleDateFormat("HH:mm");
        saveTime = currTime.format(time.getTime());
        RandomId = saveDate + saveTime;
        StorageReference path = postRef.child("postImages").child(imgUri.getLastPathSegment() + RandomId + ".jpg");
        path.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        link = uri.toString();
                        postNode.child(userId + RandomId).child("postImage").setValue(link).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(UserPost.this, "Saved in database", Toast.LENGTH_SHORT).show();
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(UserPost.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });

    }
    private void SavePostInfoInDatabase() {
        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = snapshot.child("fullName").getValue().toString();
                    String profileImg = snapshot.child("profileImage").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("fullName", name);
                    postsMap.put("profileImage", profileImg);
                    postsMap.put("userId", userId);
                    postsMap.put("date", saveDate);
                    postsMap.put("time", saveTime);
                    postsMap.put("description", description);
                    postsMap.put("postImage", link);
                    postNode.child(userId+RandomId).updateChildren(postsMap).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(UserPost.this, "Saved in Database!", Toast.LENGTH_SHORT).show();
                            SendToHome();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendToHome() {
        Intent sendToHome = new Intent(UserPost.this, UserHome.class);
        sendToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendToHome);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==photo && resultCode==RESULT_OK && data !=null){
            imgUri = data.getData();
            imageView.setImageURI(imgUri);
        }
    }

}
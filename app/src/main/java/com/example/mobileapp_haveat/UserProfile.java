package com.example.mobileapp_haveat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    private EditText userName, fullName, townName;
    private CircleImageView profileImage;
    private Button saveBtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    private StorageReference profileRef;
    final static int photo = 1;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userName = findViewById(R.id.donar_profile_username);
        fullName = findViewById(R.id.donar_profile_fullname);
        townName = findViewById(R.id.donar_profile_town);
        profileImage = findViewById(R.id.donar_profile_image);
        saveBtn = findViewById(R.id.donar_profile_button);
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        profileRef = FirebaseStorage.getInstance().getReference().child("profileImages");

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveUserInformation();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToGallery = new Intent();
                goToGallery.setAction(Intent.ACTION_GET_CONTENT);
                goToGallery.setType("image/*");
                startActivityForResult(goToGallery, photo);
            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.hasChild("profileImage")){
                        String img = snapshot.child("profileImage").getValue().toString();
                        Picasso.get().load(img).placeholder(R.drawable.profile).into(profileImage);
                    }

                    else{
                        Toast.makeText(UserProfile.this, "Select profile image first.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==photo && resultCode==RESULT_OK && data!=null){
            Uri imgUri = data.getData();
            CropImage.activity(imgUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                StorageReference link = profileRef.child(userId+".jpg");

                link.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        link.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String path = uri.toString();
                                userRef.child("profileImage").setValue(path).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(UserProfile.this, "Saved!", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            String error = task.getException().getMessage();
                                            Toast.makeText(UserProfile.this, "Error: "+error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
            else{
                Toast.makeText(UserProfile.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void SaveUserInformation() {
        String user = userName.getText().toString();
        String Fname = fullName.getText().toString();
        String town = townName.getText().toString();

        if(TextUtils.isEmpty(user)){
            Toast.makeText(this,"Please enter username", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(Fname)){
            Toast.makeText(this,"Please enter full name", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(town)){
            Toast.makeText(this,"Please enter town", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap userInfoMap = new HashMap();
            userInfoMap.put("username", user);
            userInfoMap.put("fullName" , Fname);
            userInfoMap.put("town", town);
            userRef.updateChildren(userInfoMap).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(UserProfile.this, "Information Saved. Welcome!", Toast.LENGTH_SHORT).show();
                    SendUserToHome();
                }
            });
        }
    }

    private void SendUserToHome() {
        Intent sendToHome = new Intent(UserProfile.this, UserHome.class);
        sendToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendToHome);
        finish();
    }


}
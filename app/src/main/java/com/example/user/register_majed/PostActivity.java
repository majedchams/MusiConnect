package com.example.user.register_majed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

//adding new post(item)
public class PostActivity extends AppCompatActivity {

    private ImageButton nSelectImage;
    private EditText nPostTitle;
    private EditText nPostDesc;
    private EditText nPostPrice;
    private EditText nPostLocation;
    private EditText nPostPhone;

    private Button nSubmitBtn;
    private Uri nImageUri = null;
    private static final int GALLERY_REQUEST = 1;

    private StorageReference nStorage;
    private DatabaseReference nDatabase;

    private ProgressDialog nProgress;

    private FirebaseAuth nAuth;
    private FirebaseUser nCurrentUser;

    private DatabaseReference nDatabaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        nAuth = FirebaseAuth.getInstance();
        nCurrentUser = nAuth.getCurrentUser();

        nStorage = FirebaseStorage.getInstance().getReference();
        nDatabase = FirebaseDatabase.getInstance().getReference().child("MusiConnect");
        nDatabaseUser = FirebaseDatabase.getInstance().getReference().child("user").child(nCurrentUser.getUid());

        nSelectImage = (ImageButton) findViewById(R.id.imageSelect);

        nPostTitle = (EditText) findViewById(R.id.titleField);
        nPostLocation = (EditText) findViewById(R.id.locationField);
        nPostPhone = (EditText) findViewById(R.id.phoneField);
        nPostDesc = (EditText) findViewById(R.id.descField);
        nPostPrice = (EditText) findViewById(R.id.priceField);
        //post data
        nSubmitBtn = (Button) findViewById(R.id.submitBtn);
        nProgress = new ProgressDialog(this);
            //select image from gallery to upload
        nSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");   //choose image from gallery to upload
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }


        });

        nSubmitBtn.setOnClickListener(new View.OnClickListener() {//when submit is
                                                                    // clicked start posting
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });
    }

    private void startPosting() {

        nProgress.setMessage("Posting to MusiConnect ...");//show progress while uploading data


        final String title_val = nPostTitle.getText().toString().trim();
        final String desc_val = nPostDesc.getText().toString().trim();
        final String price_val = nPostPrice.getText().toString().trim();
        final String location_val = nPostLocation.getText().toString().trim();
        final String phone_val = nPostPhone.getText().toString().trim();
        //getting data from input fields , put each of them in a string

        if (!TextUtils.isEmpty(title_val)&&!TextUtils.isEmpty(desc_val)&&nImageUri != null) {
            nProgress.show();
            StorageReference filepath = nStorage.child("MusiConnect_Images").child(nImageUri.getLastPathSegment());

            filepath.putFile(nImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost = nDatabase.push();//adding new post to database


                        //setting values of keys in a post
                    nDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("title").setValue(title_val);
                            newPost.child("price").setValue(price_val);
                            newPost.child("location").setValue(location_val);
                            newPost.child("phone").setValue(phone_val);
                            newPost.child("desc").setValue(desc_val);
                            newPost.child("image").setValue(downloadUrl.toString());
                            newPost.child("date").setValue(getCurrentDate());
                            newPost.child("uid").setValue(nCurrentUser.getUid());
                            newPost.child("username").setValue(dataSnapshot.child("username").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        startActivity(new Intent(PostActivity.this,ProfileActivity.class));
                                    }
                                    else {
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    nProgress.dismiss();//dismiss progress after task done


                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
              nImageUri = data.getData();
                nSelectImage.setImageURI(nImageUri);

         }
    }
    //current date, when post is submitted - stored in database
    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String strDate = mdformat.format(calendar.getTime());
        Log.v("mydate",strDate);
        return strDate;
    }
}

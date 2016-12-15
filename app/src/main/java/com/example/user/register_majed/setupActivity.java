package com.example.user.register_majed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class setupActivity extends AppCompatActivity {

    private ImageButton nSetupImageBtn;
    private Button nSubmitBtn;

    private Uri nImageUri = null;


    private  static final int GALLERY_REQUEST = 1;

    private FirebaseAuth nAuth;

    private DatabaseReference nDatabaseUsers;

    private StorageReference nStorageImage;

    private ProgressDialog nProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        nAuth = FirebaseAuth.getInstance();

        nStorageImage = FirebaseStorage.getInstance().getReference().child("Profile_images");

        nDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("user-profilePic");

        nProgress = new ProgressDialog(this);

        nSetupImageBtn = (ImageButton) findViewById(R.id.setupImageBtn);
        nSubmitBtn = (Button) findViewById(R.id.setupSubmitBtn);

        nSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startSetupAccount();

            }
        });

        nSetupImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });
    }

    private void startSetupAccount() {

      final String user_id = nAuth.getCurrentUser().getUid();
        if(nImageUri != null){

            nProgress.setMessage("Finishing Setup ...");
            nProgress.show();
            StorageReference filepath  = nStorageImage.child(nImageUri.getLastPathSegment());

            filepath.putFile(nImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    nDatabaseUsers.child(user_id).child("image").setValue(downloadUri);

                    nProgress.dismiss();

                    Intent mainIntent = new Intent(setupActivity.this, ProfileActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)//crop image (profile)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)//square image
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                nImageUri = result.getUri();
                nSetupImageBtn.setImageURI(nImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }
}

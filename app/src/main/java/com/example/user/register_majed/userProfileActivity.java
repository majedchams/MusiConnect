package com.example.user.register_majed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class userProfileActivity extends AppCompatActivity {


    private DatabaseReference nDatabase;
    private DatabaseReference nDatabaseUser;
    private ProgressDialog nProgress;
    private FirebaseAuth nAuth;
    private TextView nName;
    private TextView nProfession;
    private ImageView nUserImage;
    private TextView nUsername;
    private TextView nGender;
    private TextView nRegion;
    private TextView nEmail;
    private TextView nDOB;
    private TextView nPhoneNumber;
    private TextView userExperienceTV;
    private EditText userExperienceED;
    private Button DoneEdit;
    private Button callButton;

    private Button messageButton;
    private Button emailButton;
    private Button mapButton;
    private Button infoButton;
    private Button activityButton;
    private Button experienceButton;
    private Button EditButton;
    private LinearLayout linearInfo;
    private LinearLayout linearExperience;
    private LinearLayout linearActivity;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        nDatabaseUser = FirebaseDatabase.getInstance().getReference().child("user");
        nDatabase = FirebaseDatabase.getInstance().getReference().child("user-profilePic");
        nAuth = FirebaseAuth.getInstance();
        nUserImage= (ImageView) findViewById(R.id.profilepicture);
        nName = (TextView) findViewById(R.id.Name);
        nProfession = (TextView) findViewById(R.id.profession);
        nGender = (TextView) findViewById(R.id.Gender);
        nRegion = (TextView) findViewById(R.id.location);
        nDOB = (TextView) findViewById(R.id.DOB);
        nEmail = (TextView) findViewById(R.id.email);
        nUsername = (TextView) findViewById(R.id.username);
        nPhoneNumber = (TextView) findViewById(R.id.phonenumber);
        callButton = (Button) findViewById(R.id.buttoncall);
        messageButton = (Button) findViewById(R.id.buttonmessage);
        emailButton = (Button) findViewById(R.id.emailButton);
        mapButton = (Button) findViewById(R.id.mapButton);
        infoButton = (Button) findViewById(R.id.InfoButton);
        activityButton = (Button) findViewById(R.id.ActivityButton);
        experienceButton =(Button) findViewById(R.id.ExperienceButton);
        linearInfo = (LinearLayout) findViewById(R.id.LinearInfo);
        linearExperience = (LinearLayout) findViewById(R.id.LinearExperience);
        linearActivity = (LinearLayout) findViewById(R.id.LinearActivity);

        nProgress = new ProgressDialog(this);

        userExperienceED = (EditText) findViewById(R.id.userExperienceED);
        userExperienceTV = (TextView) findViewById(R.id.userExperienceTV);
        EditButton = (Button) findViewById(R.id.EditButton);
        DoneEdit = (Button) findViewById(R.id.DoneEdit);


        infoButton.performClick();
        nDatabaseUser.child(nAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user_FirstName = (String) dataSnapshot.child("first_Name").getValue();
                String user_LastName = (String) dataSnapshot.child("last_Name").getValue();
                String user_profession = (String) dataSnapshot.child("profession").getValue();
                String user_gender = (String) dataSnapshot.child("gender").getValue();
                String user_region= (String) dataSnapshot.child("region").getValue();
                String user_dob= (String) dataSnapshot.child("date_of_birth").getValue();
                String user_email= (String) dataSnapshot.child("email").getValue();
                String user_username= (String) dataSnapshot.child("username").getValue();
                String user_phoneNumber = (String) dataSnapshot.child("phone_Number").getValue();
                String user_experience = (String) dataSnapshot.child("experience").getValue();
                //setting data into the fields
                nGender.setText(user_gender);
                nRegion.setText(user_region);
                nDOB.setText(user_dob);
                nEmail.setText(user_email);
                nUsername.setText(user_username);
                nProfession.setText(user_profession);
                nName.setText(user_FirstName+" "+user_LastName);
                nProfession.setText(user_profession);
                nPhoneNumber.setText(user_phoneNumber);
                userExperienceTV.setText(user_experience);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        nDatabase.child(nAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String user_image = (String) dataSnapshot.child("image").getValue();
                Log.d(user_image,"majedddddddddd");


                Picasso.with(userProfileActivity.this).load(user_image).placeholder(R.drawable.progress_animation).into(nUserImage);
                //load user profile image into his account


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //send sms to a user
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
                        + nPhoneNumber.getText().toString())));
            }
        });
        callButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //dial number

                Intent i=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + nPhoneNumber.getText().toString()));
                Log.v("efas",nPhoneNumber.getText().toString());

                startActivity(i);

            }
        });
        //pressing on email button allows to send email to a user
        emailButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",nEmail.getText().toString(), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MusiConnect");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));

            }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+nRegion.getText().toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });


    }
    public void onClick(View v) {

        Drawable dr = getResources().getDrawable(R.drawable.button_pressed);
        dr.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);

        switch (v.getId()) {
            case R.id.ExperienceButton:

                if (button == null) {
                    button = (Button) findViewById(v.getId());
                } else {
                    button.setBackgroundResource(R.drawable.button_pressed);
                    button = (Button) findViewById(v.getId());
                    linearInfo.setVisibility(View.GONE);
                    linearExperience.setVisibility(View.VISIBLE);
                    linearActivity.setVisibility(View.GONE);
                    userExperienceTV.setVisibility(View.VISIBLE);
                    userExperienceED.setVisibility(View.GONE);
                    DoneEdit.setVisibility(View.GONE);
                    //user experience can be editied when clicking on edit button
                    EditButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userExperienceTV.setVisibility(View.GONE);
                            DoneEdit.setVisibility(View.VISIBLE);
                            userExperienceED.setVisibility(View.VISIBLE);
                            DoneEdit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startPosting();
                                    userExperienceED.setVisibility(View.GONE);
                                    userExperienceTV.setVisibility(View.VISIBLE);
                                    DoneEdit.setVisibility(View.GONE);//edit user experience
                                }
                            });
                        }
                    });


                }
                button.setBackgroundDrawable(dr);

                break;

            case R.id.InfoButton:
                if (button == null) {
                    button = (Button) findViewById(v.getId());
                } else {
                    button.setBackgroundResource(R.drawable.button_pressed);
                    button = (Button) findViewById(v.getId());
                    linearInfo.setVisibility(View.VISIBLE);
                    linearExperience.setVisibility(View.GONE);
                    linearActivity.setVisibility(View.GONE);
                    //show info layout onlu
                }
                button.setBackgroundDrawable(dr);

                break;
            case R.id.ActivityButton:
                if (button == null) {
                    button = (Button) findViewById(v.getId());
                } else {
                    button.setBackgroundResource(R.drawable.button_pressed);
                    button = (Button) findViewById(v.getId());
                    linearInfo.setVisibility(View.GONE);
                    linearExperience.setVisibility(View.GONE);
                    linearActivity.setVisibility(View.VISIBLE);//show linear activity only

                }
                button.setBackgroundDrawable(dr);

                break;


            default:
                break;
        }
    }
    private void startPosting() {

        nProgress.setMessage("Adding your Information ...");

        final String experience_val = userExperienceED.getText().toString().trim();
        //getting the data typed into edit text and place them in string experience_val


        final String user_id = nAuth.getCurrentUser().getUid();

        if (!TextUtils.isEmpty(experience_val)) {
            nProgress.show();

            nDatabaseUser.child(user_id).child("experience").setValue(experience_val);
                //user is able to edit the experience in his own profile
            //data is inserted into database


            nProgress.dismiss();



        }


    }

}

package com.example.user.register_majed;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
//clicking on a username, should take us to the profile
//that corresponds to this username
public class usersProfiles extends AppCompatActivity {

    private String nUser_key = null;
    private DatabaseReference nDatabase;
    private DatabaseReference nDatabaseUser;
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
    private Button callButton;
    private Button messageButton;
    private Button emailButton;
    private Button mapButton;
    private Button infoButton;
    private Button activityButton;
    private Button experienceButton;

    private TextView userExperienceTV;
    private LinearLayout linearInfo;    //3 linearlayouts that change on click
    private LinearLayout linearExperience;
    private LinearLayout linearActivity;

    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profiles);

        nUser_key = getIntent().getExtras().getString("user_id");//getting the extra data which is
                                                                    //the id of user account
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
        userExperienceTV = (TextView) findViewById(R.id.userExperienceTV);

        infoButton.performClick();//by default ,info is shown


        nDatabaseUser.child(nUser_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //getting data from firebase database
                //of specific user using his own key
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

                //insert data into specific fields, gender, region , dob, email..
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
        nDatabase.child(nUser_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String user_image = (String) dataSnapshot.child("image").getValue();



                Picasso.with(usersProfiles.this).load(user_image).placeholder(R.drawable.progress_animation).into(nUserImage);
                //loading profile pic of user


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
                        + nPhoneNumber.getText().toString())));//sending sms to a phone number
            }
        });
        callButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent i=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + nPhoneNumber.getText().toString()));
                Log.v("efas",nPhoneNumber.getText().toString());
                    //phone call - dial action
                startActivity(i);

            }
        });
        emailButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",nEmail.getText().toString(), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MusiConnect");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");//send email to user
                startActivity(Intent.createChooser(emailIntent, "Send email..."));

            }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+nRegion.getText().toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");//show location in maps
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
                    linearInfo.setVisibility(View.GONE);//show only experience layout, hide others
                    linearExperience.setVisibility(View.VISIBLE);
                    linearActivity.setVisibility(View.GONE);
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
                    linearExperience.setVisibility(View.GONE);//show only info layout
                    linearActivity.setVisibility(View.GONE);

                }
                button.setBackgroundDrawable(dr);

                break;
            case R.id.ActivityButton:
                if (button == null) {
                    button = (Button) findViewById(v.getId());
                } else {
                    button.setBackgroundResource(R.drawable.button_pressed);
                    button = (Button) findViewById(v.getId());
                    linearInfo.setVisibility(View.GONE);//hide layout
                    linearExperience.setVisibility(View.GONE);//hide layout
                    linearActivity.setVisibility(View.VISIBLE);//show activity linearlayout
                }
                button.setBackgroundDrawable(dr);

                break;


            default:
                break;
        }
    }
}

package com.example.user.register_majed;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
//activity for a single view of a specific post
public class PostSingleActivity extends AppCompatActivity {

    private String nPost_key = null;
    private DatabaseReference nDatabase;
    private DatabaseReference nDatabase2;

    private ImageView nBlogSingleImage;
    private TextView nBlogSingleTitle;
    private TextView nBlogSingleDesc;
    private TextView nBlogSingleUsername;
    private TextView nBlogPrice;
    private TextView nBlogLocation;
    private TextView nBlogPhone;
    private ImageView nBlogUserImage;

    private Button nSingleRemoveBtn;
    private Button nSend_btn;
    private FirebaseAuth nAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_single);

        nDatabase = FirebaseDatabase.getInstance().getReference().child("MusiConnect");
        nDatabase2 = FirebaseDatabase.getInstance().getReference().child("user-profilePic");


        nAuth = FirebaseAuth.getInstance();


        nPost_key = getIntent().getExtras().getString("blog_id");

        nBlogLocation = (TextView) findViewById(R.id.post_location);
        nBlogPhone = (TextView) findViewById(R.id.post_phone);
        nBlogPrice= (TextView) findViewById(R.id.post_price);

        nBlogSingleDesc = (TextView) findViewById(R.id.singleBlogDesc);
        nBlogSingleImage = (ImageView) findViewById(R.id.singleBlogImage);
        nBlogSingleTitle = (TextView) findViewById(R.id.singleBlogTitle);
        nBlogSingleUsername = (TextView) findViewById(R.id.firstNameLastName);
        nBlogUserImage = (ImageView) findViewById(R.id.thisuserimage);


        nSingleRemoveBtn = (Button) findViewById(R.id.SingleRemoveBtn);
        nSend_btn = (Button) findViewById(R.id.send_message);
        nDatabase.child(nPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //retrieve all data specific to a single post key
                String post_price = (String) dataSnapshot.child("price").getValue();
                String post_phone = (String) dataSnapshot.child("phone").getValue();
                String post_location = (String) dataSnapshot.child("location").getValue();
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();
                String post_username = (String) dataSnapshot.child("username").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();
                if(post_uid!=null) {
                    nDatabase2.child(post_uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String imageUrl = (String) dataSnapshot.child("image").getValue();
                            Picasso.with(PostSingleActivity.this).load(imageUrl).placeholder(R.drawable.progress_animation).into(nBlogUserImage);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else{


                }
                nBlogSingleTitle.setText(post_title);
                nBlogSingleDesc.setText(post_desc);
                nBlogSingleUsername.setText(post_username);
                nBlogPrice.setText("$"+post_price);
                nBlogPhone.setText(post_phone);
                nBlogLocation.setText(post_location);

                Picasso.with(PostSingleActivity.this).load(post_image).into(nBlogSingleImage);
                if (nAuth.getCurrentUser().getUid().equals(post_uid)){

                    nSingleRemoveBtn.setVisibility(View.VISIBLE);
                    //if user views his own post. he can also remove the post

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //dialog for confirmation to remove a post of a user

        nSingleRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Delete post")
                        .setMessage("Are you sure you want to delete this post?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                nDatabase.child(nPost_key).removeValue();
                                Intent mainIntent = new Intent(PostSingleActivity.this, ProfileActivity.class);
                                startActivity(mainIntent);

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();



            }
        });

        nSend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(PostSingleActivity.this,chatActivity.class);
                i.putExtra("username", nBlogSingleUsername.getText().toString());
                    //send a message to the user
                startActivity(i);

            }
        });
    }
}

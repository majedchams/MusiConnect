package com.example.user.register_majed;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EventActivity extends AppCompatActivity {

    private FirebaseAuth nAuth;
    private DatabaseReference nDatabase;
    private RecyclerView nBlogList;
    private FirebaseUser nCurrentUser;
    private FirebaseAuth firebaseAuth;

    private boolean nProcessAttend = false;
    private DatabaseReference nDatabaseEventAttend;
    private DatabaseReference nDatabaseApproved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);


        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        nDatabaseEventAttend = FirebaseDatabase.getInstance().getReference().child("EventAttend");
        nDatabaseApproved = FirebaseDatabase.getInstance().getReference().child("approved");
        nDatabase = FirebaseDatabase.getInstance().getReference().child("Events");
        nCurrentUser = firebaseAuth.getCurrentUser();
        nBlogList = (RecyclerView) findViewById(R.id.blog_list);
        nBlogList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        nBlogList.setLayoutManager(layoutManager);

        nBlogList = (RecyclerView) findViewById(R.id.blog_list);

    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseRecyclerAdapter<event, BlogViewHolder> firebaseRecyclerAdapter =new FirebaseRecyclerAdapter<event, BlogViewHolder>(
                event.class,
                R.layout.event_row,
                BlogViewHolder.class,
                nDatabase

        ) {
            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, final event model, int position) {


                final String post_key = getRef(position).getKey();


                viewHolder.setName(model.getName());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setlocation(model.getlocation());
                viewHolder.setendDate(model.getendDate());
                viewHolder.setstartDate(model.getstartDate());
                viewHolder.setstartTime(model.getstartTime());
                viewHolder.setEndTime(model.getEndTime());
                viewHolder.setHostedBy(model.getHostedBy());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setAttendBtn(post_key);
                viewHolder.setApproved(post_key);
                viewHolder.setDeclined(post_key);

                viewHolder.nAttendbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nProcessAttend = true;
                        Log.v("sf","clicked");
                        String tkn = FirebaseInstanceId.getInstance().getToken();
                        //Toast.makeText(EventActivity.this, "Current Token ["+tkn+"]",Toast.LENGTH_LONG).show();
                        Log.v("postId",post_key);
                        nDatabaseEventAttend.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (nProcessAttend){
                                    if (dataSnapshot.child(post_key).hasChild(firebaseAuth.getCurrentUser().getUid())){
                                        nDatabaseEventAttend.child(post_key).child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                                        nProcessAttend = false;

                                    }

                                else {
                                    nDatabaseEventAttend.child(post_key).child(firebaseAuth.getCurrentUser().getUid()).setValue("request sent");
                                    nProcessAttend = true;//sending requests to perform at an event
                                }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }


        };


        nBlogList.setAdapter(firebaseRecyclerAdapter);
    }
        //didn't use this
        //this was for notifications
    public class FireIDService extends FirebaseInstanceIdService {
        @Override
        public void onTokenRefresh() {
            String tkn = FirebaseInstanceId.getInstance().getToken();
            //Log.d("Not","Token ["+tkn+"]");

        }
    }



    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View nView;
        ImageButton nAttendbtn;

        private DatabaseReference nDatabaseUser;
        private FirebaseUser nCurrentUser;
        FirebaseAuth nAuth;
        DatabaseReference nDatabaseEventAttend;
        TextView post_username;
        TextView request;
        TextView user_id;
        FirebaseAuth firebaseAuth;
        DatabaseReference nDatabaseApproved;
        DatabaseReference nDatabaseDeclined;
        public BlogViewHolder(View itemView) {
            super(itemView);



            nView = itemView;
            //onclick listener for username- takes us to profile page of a user
            user_id = (TextView) nView.findViewById(R.id.user_id);
            post_username = (TextView) nView.findViewById(R.id.post_username);


            request = (TextView) nView.findViewById(R.id.request);
            nAuth = FirebaseAuth.getInstance().getInstance();
            nCurrentUser = nAuth.getCurrentUser();
            nAttendbtn = (ImageButton) nView.findViewById(R.id.attend_btn);
            nDatabaseApproved = FirebaseDatabase.getInstance().getReference().child("approved");
            nDatabaseDeclined = FirebaseDatabase.getInstance().getReference().child("declined");
            nDatabaseEventAttend = FirebaseDatabase.getInstance().getReference().child("EventAttend");
            nAuth = FirebaseAuth.getInstance().getInstance();
            firebaseAuth = FirebaseAuth.getInstance().getInstance();
            nCurrentUser = nAuth.getCurrentUser();
        }

        public void setApproved(final  String post_key){
            nDatabaseApproved.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(post_key).hasChild(firebaseAuth.getCurrentUser().getUid())){
                        request.setText("You request was approved");
                        nAttendbtn.setVisibility(View.INVISIBLE);

                    }else{


                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }

        public void setDeclined(final  String post_key){
            nDatabaseDeclined.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(post_key).hasChild(firebaseAuth.getCurrentUser().getUid())){
                        request.setText("You request was declined");//if request was declined
                        nAttendbtn.setVisibility(View.INVISIBLE);

                    }else{


                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }
        public void setAttendBtn(final String post_key){
            nDatabaseEventAttend.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(post_key).hasChild(firebaseAuth.getCurrentUser().getUid())){
                        request.setText("Request Sent");
                        nAttendbtn.setImageResource(R.mipmap.ic_bookmark_black_24dp);

                    }else{
                        nAttendbtn.setImageResource(R.mipmap.ic_bookmark_border_black_24dp);
                        request.setText("Send request to attend");

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });


        }


        public void setName(String name){

            TextView post_name = (TextView) nView.findViewById(R.id.eventName);
            post_name.setText(name);
        }

        public void setHostedBy(String HostedBy){
            TextView hosted_by = (TextView) nView.findViewById(R.id.HostedBy);
            hosted_by.setText(HostedBy);

        }


        public void setDesc(String desc){
            TextView post_desc = (TextView) nView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }

        public void setUsername(String username){
            TextView post_username = (TextView) nView.findViewById(R.id.post_username);
            post_username.setText(username);
        }


        public void setstartTime(String startTime) {
            TextView start_Time = (TextView) nView.findViewById(R.id.startTime);
            start_Time.setText(startTime);

        }


        public void setstartDate(String startDate) {
            TextView start_Date = (TextView) nView.findViewById(R.id.startDate);
            start_Date.setText(startDate);

        }

        public void setendDate(String endDate) {
            TextView end_Date = (TextView) nView.findViewById(R.id.endDate);
            end_Date.setText(endDate);
        }


        public void setEndTime(String endTime) {
            TextView end_Time = (TextView) nView.findViewById(R.id.endTime);
            end_Time.setText(endTime);

        }


        public void setlocation(String location) {
            TextView post_location = (TextView) nView.findViewById(R.id.location);
            post_location.setText(location);

        }







    }




}

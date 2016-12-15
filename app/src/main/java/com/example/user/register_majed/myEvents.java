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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//this activity shows only the events created by a user who's logged in
public class myEvents extends AppCompatActivity {

    private FirebaseAuth nAuth;
    private DatabaseReference nDatabase;
    private RecyclerView nBlogList;
    private FirebaseUser nCurrentUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference nDatabaseCurrentuser;
    private DatabaseReference nDatabase3;
    private DatabaseReference nDatabase4;
    private DatabaseReference nDatabasePerform;
    private DatabaseReference nDatabaseUsers;
    private Query nQueryCurrentUser;//query to get only user's events

    private boolean nProcessAttend = false;
    private DatabaseReference nDatabaseEventAttend;
    private LinearLayout linearRequests;


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
        nDatabase = FirebaseDatabase.getInstance().getReference().child("Events");
        nCurrentUser = firebaseAuth.getCurrentUser();
        String currentUserId = firebaseAuth.getCurrentUser().getUid();
        nDatabasePerform = FirebaseDatabase.getInstance().getReference().child("EventAttend");
        nDatabaseCurrentuser = FirebaseDatabase.getInstance().getReference().child("Events");
        nQueryCurrentUser = nDatabaseCurrentuser.orderByChild("uid").equalTo(currentUserId);
        nBlogList = (RecyclerView) findViewById(R.id.blog_list);
        nBlogList.setHasFixedSize(true);
        nDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("user");

        nDatabase3 = FirebaseDatabase.getInstance().getReference().child("approved");
        nDatabase4 = FirebaseDatabase.getInstance().getReference().child("declined");
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
                R.layout.myevent_row,
                BlogViewHolder.class,
                nQueryCurrentUser

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

                nDatabasePerform.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final DataSnapshot performRequest = dataSnapshot.child(post_key);
//                                Log.v("asdasd",peopleLiked1);
//
                        List<String> lst = new ArrayList<String>();

                        for (final DataSnapshot dsp : performRequest.getChildren()) {
                            //nDatabaseUsers.child(String.valueOf(dsp.getKey())).child("username").setValue();

                            lst.add(String.valueOf(dsp.getKey())); //add result into array list
                            Log.v("keyyyyyyyy", dsp.getKey());

                        String j = "";
                        final Iterator<String> peoplewhoLiked = lst.iterator();
                        while (peoplewhoLiked.hasNext()) {

                                nDatabaseUsers.child(peoplewhoLiked.next()).child("username").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {

                                        final String a = dataSnapshot.getValue().toString();
                                        Log.v("sdfdsfsdMAJED", a);
                                        final TextView tv_request_name = (TextView) findViewById(R.id.tv_request_name);
                                        tv_request_name.setText(a + " requested to perform at your event");


                                        Button btn_dec = (Button) findViewById(R.id.btn_decline);
                                        Button btn_approve = (Button) findViewById(R.id.btn_approve);

                                        //approve
                                        btn_approve.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                String b = performRequest.getKey();
                                                Log.v("asdasd", b);
                                                String c = a;
                                                Log.v("peoople", c);
                                                //  nDatabasePerform.child(b).child("asdas").setValue("hi");
                                                nDatabasePerform.child(b).child(dsp.getKey()).setValue("approved");
                                                nDatabase3.child(b).child(dsp.getKey()).setValue("approved");
                                                if (peoplewhoLiked.hasNext()){
                                                    peoplewhoLiked.next();
                                                }
                                                nDatabasePerform.child(b).child(dsp.getKey()).removeValue();
                                                tv_request_name.setText(a + " Approved ");
                                            }

                                        });
                                        btn_dec.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //if declined
                                                String b = performRequest.getKey();
                                                nDatabasePerform.child(b).child(dsp.getKey()).setValue("declined");
                                                nDatabase4.child(b).child(dsp.getKey()).setValue("declined");
                                                if (peoplewhoLiked.hasNext()){
                                                    peoplewhoLiked.next();
                                                }
                                                nDatabasePerform.child(b).child(dsp.getKey()).removeValue();
                                                tv_request_name.setText(a + " Declined ");
                                            }

                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                //   j  = j + "\n" +peoplewhoLiked.next();


                        }


                        //  Log.v("jjjjjjjjjjg",j);
                    }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }


        };


        nBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public class FireIDService extends FirebaseInstanceIdService {
        @Override
        public void onTokenRefresh() {
            String tkn = FirebaseInstanceId.getInstance().getToken();
            Log.d("Not","Token ["+tkn+"]");

        }
    }



    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View nView;



        private DatabaseReference nDatabaseUser;
        private FirebaseUser nCurrentUser;
        FirebaseAuth nAuth;
        DatabaseReference nDatabaseEventAttend;
        TextView post_username;
        TextView request;
        TextView user_id;
        FirebaseAuth firebaseAuth;
        public BlogViewHolder(View itemView) {
            super(itemView);



            nView = itemView;
            //onclick listener for username- takes us to profile page of a user
            user_id = (TextView) nView.findViewById(R.id.user_id);
            post_username = (TextView) nView.findViewById(R.id.post_username);


            request = (TextView) nView.findViewById(R.id.request);
            nAuth = FirebaseAuth.getInstance().getInstance();
            nCurrentUser = nAuth.getCurrentUser();


            nDatabaseEventAttend = FirebaseDatabase.getInstance().getReference().child("EventAttend");
            nAuth = FirebaseAuth.getInstance().getInstance();
            firebaseAuth = FirebaseAuth.getInstance().getInstance();
            nCurrentUser = nAuth.getCurrentUser();
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

package com.example.user.register_majed;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.SimpleResource;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.example.user.register_majed.R.id.post_phone;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout nDrawerLayout;
    private ActionBarDrawerToggle nToggle;
    private Toolbar nToolbar;

    private RecyclerView nBlogList;
    private FirebaseAuth nAuth;

    private DatabaseReference nDatabase;
    private DatabaseReference nDatabaseUser;
    private DatabaseReference nDatabase2;
    private FirebaseUser nCurrentUser;
    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail;
    private Button buttonLogout;
    private Button buttonProfile;

    private ImageView user_image1;
    private NavigationView navigationView;

    private ImageView profilepic;

    private  boolean nProcessLike = false;
    Button btnClosePopup;
    private DatabaseReference nDatabaseLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);  //main activity xml
                                                    //shows all posts in
                                                    //buy and sell forum

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();//get info of user logged in

        nDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        nToggle = new ActionBarDrawerToggle(this, nDrawerLayout, R.string.open,R.string.close);//drawertoggle
        nToolbar = (Toolbar) findViewById(R.id.nav_action);
        setSupportActionBar(nToolbar);


        nDrawerLayout.addDrawerListener(nToggle);
        nToggle.syncState();
        user_image1 = (ImageView) findViewById(R.id.user_image1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView =(NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        nAuth = FirebaseAuth.getInstance();
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText("Welcome "+ user.getEmail());//show email of user logged in


        nCurrentUser = firebaseAuth.getCurrentUser();
        nDatabaseUser = FirebaseDatabase.getInstance().getReference().child("user-profilePic");
        //reference to user-profilepic table in database


        nBlogList = (RecyclerView) findViewById(R.id.blog_list);//recyclerview
        nBlogList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);//show new posts first
        layoutManager.setStackFromEnd(true);


        nBlogList.setLayoutManager(layoutManager);

        nDatabase2 = FirebaseDatabase.getInstance().getReference().child("user-profilePic");
        nDatabase = FirebaseDatabase.getInstance().getReference().child("MusiConnect");//posts
        nDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");//likes table

        //firebaseRecyclerAdapter
        final FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter =new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                nDatabase   //reference stated before


        ) {
            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, final Blog model, int position) {

                //populate each post(viewholder)
                    //getting the key of each single post in order to retrieve its data
                final String post_key = getRef(position).getKey();


                    //data is title
                    //description
                    //price
                    //phone...
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setlocation(model.getlocation());
                viewHolder.setPhone(model.getPhone());
                viewHolder.setDate(model.getDate());

                viewHolder.setUsername(model.getUsername());
                viewHolder.setUid(model.getUid());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setProfileImage(getApplicationContext(),model.getImage());
                viewHolder.setLikeBtn(post_key);
//

                //when clicking on a single post
                //you can see the post on a single activity with more
                //info about the post
                //(opens activity postSingleActivity)
                viewHolder.nView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent singleBlogIntent = new Intent(ProfileActivity.this, PostSingleActivity.class);
                        singleBlogIntent.putExtra("blog_id", post_key);
                        startActivity(singleBlogIntent);
                    }
                });

                    //view people who like each post when clicking on numberoflikes
                viewHolder.number_likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //reference to Likes table in our database
                        //which contains all the posts and
                        //id's of people who like each post
                        nDatabaseLike.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                DataSnapshot peopleLiked = dataSnapshot.child(post_key);
                                //arraylist containing all people that like each post
                                List<String> lst = new ArrayList<String>();
                                for(DataSnapshot dsp : peopleLiked.getChildren()){
                                    lst.add(String.valueOf(dsp.getValue())); //add result into array list
                                }
                                String j ="";
                                Iterator<String> peoplewhoLiked = lst.iterator();
                                while(peoplewhoLiked.hasNext()){

                                    j  = j + "\n" +peoplewhoLiked.next();
                                }

                                initiatePopupWindow(j);//popup with people that like a specific post
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }
                });

                //when clicking on phone icon, you cal call a person
                viewHolder.post_phone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        nDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String phoneNumber = (String) dataSnapshot.child(post_key).child("phone").getValue();
                                Intent i=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                                // Log.v("efas",nPhoneNumber.getText().toString());

                                startActivity(i);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }
                });


                    //when clicking on post location icon, you can
                    //view the location of the post item on google maps.
                viewHolder.post_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        nDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String postLocation = (String) dataSnapshot.child(post_key).child("location").getValue();

                                Uri gmmIntentUri = Uri.parse("google.navigation:q="+postLocation);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");//google maps
                                startActivity(mapIntent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }
                });



                //pressing like button on a post
                viewHolder.nLikebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        nProcessLike = true;//like = true. initially it's set to false


                        nDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (nProcessLike) {
                                    if (dataSnapshot.child(post_key).hasChild(firebaseAuth.getCurrentUser().getUid())) {

                                        nDatabaseLike.child(post_key).child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                                        nProcessLike = false;

                                    } else {

                                        nDatabaseLike.child(post_key).child(firebaseAuth.getCurrentUser().getUid()).setValue(firebaseAuth.getCurrentUser().getEmail().toString());
                                        nProcessLike = false;
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
            private PopupWindow pwindo;//popup for people liking a post
            private TextView peopleLiked;
            private void initiatePopupWindow(String a){
                try {
                    if(a!="") {
                        LayoutInflater inflater = (LayoutInflater) ProfileActivity.this
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.screen_popup,
                                (ViewGroup) findViewById(R.id.popup_element));

                        pwindo = new PopupWindow(layout, 1000, 500, true);//properties for popup
                        pwindo.showAtLocation(layout, Gravity.CENTER, 10, 100);

                        peopleLiked = (TextView) layout.findViewById(R.id.peopleLiked);
                        peopleLiked.setText(a);//get string of people that like post

                        btnClosePopup = (Button) layout.findViewById(R.id.btn_close_popup);
                        btnClosePopup.setOnClickListener(cancel_button_click_listener);
                        // x button to close popup

                    }
                    else {


                    }



                }catch (Exception e){
                    pwindo.dismiss();
                }

            }
            private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.dismiss();//closing the popup
                }
            };

        };

        nBlogList.setAdapter(firebaseRecyclerAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_account) {
           // Toast.makeText(this,"account",Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, userProfileActivity.class));
        }

        else if (id == R.id.nav_eventadd)
        {
            // Toast.makeText(this,"settings",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, EventAdd.class));

        }
        else if (id == R.id.nav_events)
        {
            // Toast.makeText(this,"settings",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, EventActivity.class));

        }
        else if (id == R.id.nav_myevents)
        {
            // Toast.makeText(this,"settings",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, myEvents.class));

        }
        else if (id == R.id.nav_logout)
        {
           // Toast.makeText(this,"logout",Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));

        }

        return true;
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View nView;

        ImageButton nLikebtn;
        TextView post_phone;
        TextView post_location;

        ImageView profilepic;
        private DatabaseReference nDatabaseUser;
        private FirebaseUser nCurrentUser;
        TextView number_likes;
        DatabaseReference nDatabaseLikes;
        FirebaseAuth nAuth;
        String url;
        TextView post_username;
        TextView user_id;

        public BlogViewHolder(View itemView) {
            super(itemView);




            nView = itemView;
        //onclick listener for username- takes us to profile page of a user
            user_id = (TextView) nView.findViewById(R.id.user_id);
            post_location = (TextView) nView.findViewById(R.id.post_location);
            post_phone = (TextView)nView.findViewById(R.id.post_phone);

            post_username = (TextView) nView.findViewById(R.id.post_username);
            post_username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("MainActivity","someText");
                    Intent i = new Intent(view.getContext(),usersProfiles.class);
                   i.putExtra("user_id", user_id.getText().toString());


                    Log.v("MainActivity",user_id.getText().toString());
                    view.getContext().startActivity(i);



                }
            });



            nLikebtn = (ImageButton) nView.findViewById(R.id.like_btn);
            number_likes = (TextView) nView.findViewById(R.id.number_likes);

            nDatabaseLikes = FirebaseDatabase.getInstance().getReference().child("Likes");
            nAuth = FirebaseAuth.getInstance().getInstance();

            nCurrentUser = nAuth.getCurrentUser();
            nDatabaseUser = FirebaseDatabase.getInstance().getReference().child("user-profilePic");

        }





        public void setLikeBtn(final String post_key){
            nDatabaseLikes.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long number_of_likes = dataSnapshot.child(post_key).getChildrenCount();
                    int nums = (int)number_of_likes;    //number of likes (count)
                    number_likes.setText(Integer.toString(nums) + " likes");
                    if(dataSnapshot.child(post_key).hasChild(nAuth.getCurrentUser().getUid())){

                        nLikebtn.setImageResource(R.mipmap.ic_favorite_black_24dp);// setting
                                                                                // icon if liked

                    }else{
                        nLikebtn.setImageResource(R.mipmap.ic_favorite_border_black_24dp);//if
                                                                                    // not liked

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });


        }
        public void setDate(String date){

            TextView post_date = (TextView) nView.findViewById(R.id.post_date);
            post_date.setText(date);//post date
        }

        public void setPhone(String phone){

            TextView post_phone = (TextView) nView.findViewById(R.id.post_phone);
            post_phone.setText(phone);
        }

        public void setlocation(String location){

            TextView post_location = (TextView) nView.findViewById(R.id.post_location);
            post_location.setText(location);
        }

        public void setPrice(String price){

            TextView post_price = (TextView) nView.findViewById(R.id.post_price);
            post_price.setText("$ "+price);
        }



        public void setTitle(String title){

            TextView post_title = (TextView) nView.findViewById(R.id.post_title);
             post_title.setText(title);
        }
        public void setDesc(String desc){
            TextView post_desc = (TextView) nView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }

        public void setUsername(String username){
           // TextView post_username = (TextView) nView.findViewById(R.id.post_username);
            post_username.setText(username);
        }

        public void setUid(String uid) {
            user_id.setText(uid);
        }

        public void setImage(Context ctx, String image){

            ImageView post_image = (ImageView) nView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).placeholder(R.drawable.progress_animation).into(post_image);
            //show progress while loading image (rotating circle)

        }
        public void setProfileImage(Context ctx, String image){

           ImageView profile_image = (ImageView) nView.findViewById(R.id.profile_image);
            Picasso.with(ctx).load(image).resize(400,400).into(profile_image);
        }

        public void setUserimage(Context context, String imageUrl) {
           // ImageView profilepic = (ImageView) nView.findViewById(R.id.profilepic);
            Picasso.with(context).load(imageUrl).into(profilepic);
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            startActivity(new Intent(ProfileActivity.this, PostActivity.class));
        }
        if (nToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);


    }

//    @Override
//    public void onClick(View view) {
//
//        if(view == buttonLogout){
//            firebaseAuth.signOut();
//            finish();
//            startActivity(new Intent(this, LoginActivity.class));
//        }else if (view == buttonProfile){
//
//            finish();
//            startActivity(new Intent(this, userProfileActivity.class));
//
//        }
//    }


}

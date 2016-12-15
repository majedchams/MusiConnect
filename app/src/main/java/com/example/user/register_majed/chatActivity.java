package com.example.user.register_majed;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.register_majed.Chat_Room;
import com.example.user.register_majed.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class chatActivity extends AppCompatActivity {
    private String nUser_name = null;
    private Button  add_room;
    private EditText room_name;
    private FirebaseAuth nAuth;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<>();
    private String name;
    private String a;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("messages");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        a="";

        room_name = (EditText) findViewById(R.id.room_name_edittext);
        listView = (ListView) findViewById(R.id.listView);
        nUser_name = getIntent().getExtras().getString("username");
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_rooms);

        nAuth = FirebaseAuth.getInstance();
        listView.setAdapter(arrayAdapter);

        request_user_name();



                Map<String,Object> map = new HashMap<String, Object>();
                map.put(nUser_name,"");
                root.updateChildren(map);


        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }

                list_of_rooms.clear();
                list_of_rooms.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent1 = new Intent(getApplicationContext(),Chat_Room.class);
                intent1.putExtra("room_name",((TextView)view).getText().toString() );
                intent1.putExtra("user_name",name);
                startActivity(intent1);
            }
        });

    }

    private void request_user_name() {

                a = nAuth.getCurrentUser().getEmail().toString();
                int b = a.indexOf("@");
                 name= a.substring(0 , b);

    }
}
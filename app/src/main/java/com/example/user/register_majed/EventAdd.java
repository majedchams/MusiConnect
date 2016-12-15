package com.example.user.register_majed;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

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

import java.util.Calendar;

//creating an event
public class EventAdd extends AppCompatActivity {

    private EditText nEventName;
    private EditText nHostedBy;
    private EditText nstartDate;
    private EditText nstartTime;
    private EditText nEndDate;
    private EditText nEndTime;
    private EditText nEventLocation;
    private EditText nEventDescription;

//data input needed for an event



    private Button nSubmitBtn;
    private StorageReference nStorage;
    private DatabaseReference nDatabase;
    private ProgressDialog nProgress;
    private FirebaseAuth nAuth;
    private FirebaseUser nCurrentUser;
    Calendar c = Calendar.getInstance();
    EditText display;
    EditText display2;
    int cday, cmonth, cyear;
    EditText display3;
    EditText display4;
    int tHourofDay, tminute;
    private DatabaseReference nDatabaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);

        nEventName = (EditText) findViewById(R.id.eventName);
        nHostedBy = (EditText) findViewById(R.id.HostedBy);
        nstartDate =(EditText) findViewById(R.id.startDate);
        nstartTime =(EditText) findViewById(R.id.startTime);
        nEndDate =(EditText) findViewById(R.id.endDate);
        nEndTime =(EditText) findViewById(R.id.EndTime);
        nEventLocation = (EditText) findViewById(R.id.EventLocation);
        nEventDescription = (EditText) findViewById(R.id.EventDescription);


        display = (EditText) findViewById(R.id.startDate);
        display2 = (EditText) findViewById(R.id.endDate);
        display3 = (EditText) findViewById(R.id.startTime);
        display4 = (EditText) findViewById(R.id.EndTime);

        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EventAdd.this, d,
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        display2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EventAdd.this, d2,
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        display3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new TimePickerDialog(EventAdd.this,t,c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show();

            }
        });
        display4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(EventAdd.this,t2,c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show();
            }
        });

        nAuth = FirebaseAuth.getInstance();
        nCurrentUser = nAuth.getCurrentUser();

        nStorage = FirebaseStorage.getInstance().getReference();
        nDatabase = FirebaseDatabase.getInstance().getReference().child("Events");
        nDatabaseUser = FirebaseDatabase.getInstance().getReference().child("user").child(nCurrentUser.getUid());

        nSubmitBtn = (Button) findViewById(R.id.submitBtn);
        nSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });


    }
    //timepickerdialog
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            tHourofDay = hourOfDay;
            tminute = minute;
            display3.setText(tHourofDay+" "+tminute);
        }
    };

    TimePickerDialog.OnTimeSetListener t2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            tHourofDay = hourOfDay;
            tminute = minute;

            display4.setText(tHourofDay+" "+tminute);
        }
    };
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            cday = dayOfMonth;
            cmonth = monthOfYear + 1;
            cyear = year;

            display.setText(cday + " " + cmonth + " " + cyear);

        }
    };
    //datapickerdialog
    DatePickerDialog.OnDateSetListener d2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            cday = dayOfMonth;
            cmonth = monthOfYear + 1;
            cyear = year;

            display2.setText(cday + " " + cmonth + " " + cyear);

        }
    };
    private void startPosting(){


        final String name_val = nEventName.getText().toString().trim();
        final String host_val = nHostedBy.getText().toString().trim();
        final String startdate_val = nstartDate.getText().toString().trim();
        final String startTime_val = nstartTime.getText().toString().trim();
        final String endDate_val = nEndDate.getText().toString().trim();
        final String endTime_val = nEndTime.getText().toString().trim();
        final String location_val = nEventLocation.getText().toString().trim();
        final String desc_val =nEventDescription.getText().toString().trim();

        if (!TextUtils.isEmpty(name_val)&&!TextUtils.isEmpty(host_val)) {



                    final DatabaseReference newEvent = nDatabase.push();


                //inserting data to database
                    nDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newEvent.child("name").setValue(name_val);
                            newEvent.child("hostedBy").setValue(host_val);
                            newEvent.child("location").setValue(location_val);
                            newEvent.child("startDate").setValue(startdate_val);
                            newEvent.child("startTime").setValue("at "+startTime_val);
                            newEvent.child("endDate").setValue(endDate_val);
                            newEvent.child("endTime").setValue(endTime_val);
                            newEvent.child("desc").setValue(desc_val);
                            newEvent.child("uid").setValue(nCurrentUser.getUid());
                            newEvent.child("username").setValue(dataSnapshot.child("username").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        startActivity(new Intent(EventAdd.this,EventActivity.class));
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





        }




    }
}

package com.example.user.register_majed;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
//adding info of user after signup
public class InfoActivity extends AppCompatActivity {
    private EditText nFirstName;
    private EditText nLastName;
    private EditText nUsername;
    private EditText nPhoneNumber;
    private Button nSubmitBtn;
    private Spinner spinner1;
    private Spinner spinner2;
    private DatabaseReference nDatabase;
    private FirebaseAuth nAuth;
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;

    Calendar c = Calendar.getInstance();
    TextView display;

    int cday, cmonth, cyear;


    private ProgressDialog nProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();

        TextView changeDate = (TextView) findViewById(R.id.displatext);
        display = (TextView) findViewById(R.id.displatext);

        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(InfoActivity.this, d,
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        nAuth = FirebaseAuth.getInstance();
        nDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        nFirstName = (EditText) findViewById(R.id.editTextPersonName);
        nLastName = (EditText) findViewById(R.id.editTextPersonFamilyName);
        nUsername = (EditText) findViewById(R.id.editTextUsername);
        nPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        nProgress = new ProgressDialog(this);
        nSubmitBtn = (Button) findViewById(R.id.buttonDone);
        nSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedId = radioSexGroup.getCheckedRadioButtonId();
                radioSexButton = (RadioButton) findViewById(selectedId);


                startPosting();

            }
        });
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            cday = dayOfMonth;
            cmonth = monthOfYear + 1;
            cyear = year;

            display.setText(cday + "/" + cmonth + "/" + cyear);

        }
    };

        //spinner for profession & region
    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
        //radio button for gender
    public void addListenerOnButton() {

        radioSexGroup = (RadioGroup) findViewById(R.id.radioGrp);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
    }

        //function to add data to database
    private void startPosting() {

        nProgress.setMessage("Adding your Information ...");
        final String date_val = cday + "/" + cmonth + "/" + cyear;
        final String gender_val = radioSexButton.getText().toString();
        final String profession_val = String.valueOf(spinner2.getSelectedItem());
        final String region_val = String.valueOf(spinner1.getSelectedItem());
        final String firstName_val = nFirstName.getText().toString().trim();
        final String lastName_val = nLastName.getText().toString().trim();
        final String username_val = nUsername.getText().toString().trim();
        final String phoneNumber_val = nPhoneNumber.getText().toString().trim();

        final String user_id = nAuth.getCurrentUser().getUid();

        if (!TextUtils.isEmpty(firstName_val) && !TextUtils.isEmpty(date_val) && !TextUtils.isEmpty(phoneNumber_val) && !TextUtils.isEmpty(lastName_val) && !TextUtils.isEmpty(username_val)) {
            nProgress.show();

            nDatabase.child(user_id).child("gender").setValue(gender_val);


            nDatabase.child(user_id).child("gender").setValue(gender_val);
            nDatabase.child(user_id).child("first_Name").setValue(firstName_val);
            nDatabase.child(user_id).child("last_Name").setValue(lastName_val);
            nDatabase.child(user_id).child("date_of_birth").setValue(date_val);
            nDatabase.child(user_id).child("region").setValue(region_val);
            nDatabase.child(user_id).child("profession").setValue(profession_val);
            nDatabase.child(user_id).child("phone_Number").setValue(phoneNumber_val);
            nDatabase.child(user_id).child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
            nDatabase.child(user_id).child("username").setValue(username_val);
            nDatabase.child(user_id).child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            nProgress.dismiss();

            startActivity(new Intent(InfoActivity.this, setupActivity.class));

        }


    }


}
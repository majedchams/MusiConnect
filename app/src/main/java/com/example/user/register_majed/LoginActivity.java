package com.example.user.register_majed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{



    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!= null){
            //user is already logged in
            //start profile activity
            finish();
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));

        }

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignin);
        textViewSignup = (TextView) findViewById(R.id.textViewSignup);

        progressDialog = new ProgressDialog(this);

        buttonSignIn.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
    }
    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
            //stopping the function
            return;
        }
        if(TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this, "Please enter Password",Toast.LENGTH_SHORT).show();
            //stop funciton execution

            return;
        }
        //if validation is ok
        //user will be registered
        progressDialog.setMessage("Logging in..");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if(task.isSuccessful()){
                            //start profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        }
                    }
                });
    }
    @Override
    public void onClick(View view) {
        if (view == buttonSignIn) {
            userLogin();
        }
        if (view == textViewSignup) {
            finish();
            startActivity(new Intent(this,MainActivity.class));

        }
    }
}

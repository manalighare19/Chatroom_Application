package com.example.manalighare.inclass09;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth mAuth;
    private Button Cancel;
    private Button SignUp;
    private EditText fname, lname, email_signup, password_signup, repeat_password;
    private String first_name, last_name, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");

        mAuth = FirebaseAuth.getInstance();

        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        email_signup = (EditText) findViewById(R.id.email);
        password_signup = (EditText) findViewById(R.id.password);
        repeat_password = (EditText) findViewById(R.id.repeat_password);

        SignUp = (Button) findViewById(R.id.signup_btn);
        Cancel = (Button) findViewById(R.id.cancel_btn);

        SignUp.setOnClickListener(this);
        Cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_btn:

                if(isEverythingFilledAndValid()){

                    String email=email_signup.getText().toString();
                    String password=password_signup.getText().toString();
                    final String fullname=fname.getText().toString()+" "+lname.getText().toString();



                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Toast.makeText(SignUp.this, "User Has Been Created", Toast.LENGTH_SHORT).show();
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            adduser_name(user, fullname);
                                        } /*else {

                                            Log.w("demo", "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(SignUp.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                                        }*/


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUp.this, ""+e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });


                }


                break;


            case R.id.cancel_btn:
                Intent cancel_intent=new Intent(SignUp.this,MainActivity.class);
                startActivity(cancel_intent);
                break;
        }
    }

    private void adduser_name(final FirebaseUser user, String fullname) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullname)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("demo", "User profile updated. New Name : "+user.getDisplayName());

                            Intent chatroom_intent=new Intent(SignUp.this,ChatRoom.class);
                            startActivity(chatroom_intent);
                        }
                    }
                });

    }

    public boolean isEmailValid(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isEverythingFilledAndValid(){
        int i=0;

        if(email_signup.getText().toString().equals("")){
            email_signup.setError("Please enter email");
            i=1;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email_signup.getText().toString()).matches()){
            email_signup.setError("Invalid Email ID");
            i=1;
        }


        if (password_signup.getText().toString().equals("")){
            password_signup.setError("Please enter password");
            i=1;
        }

        if (fname.getText().toString().equals("")){
            fname.setError("Please enter first name");
            i=1;
        }
        if (lname.getText().toString().equals("")){
            lname.setError("Please enter first name");
            i=1;
        }
        if (repeat_password.getText().toString().equals("")){
            repeat_password.setError("Please enter password");
            i=1;
        }

        if(!password_signup.getText().toString().equals(repeat_password.getText().toString())){
            repeat_password.setError("Passwords do not match, please retype");
            i=1;
        }



        if(i==0){
            return true;
        }else{
            return false;
        }

    }

}
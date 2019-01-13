package com.example.manalighare.inclass09;

import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseStorage Storage=FirebaseStorage.getInstance();
    private FirebaseAuth mAuth;
    private Button Login;
    private Button SignUp;
    private EditText Email;
    private EditText Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Login");

        mAuth = FirebaseAuth.getInstance();

        Login=(Button)findViewById(R.id.login_btn);
        SignUp=(Button)findViewById(R.id.signup_btn);
        Email=(EditText)findViewById(R.id.email);
        Password=(EditText)findViewById(R.id.password);


        Login.setOnClickListener(this);
        SignUp.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            Intent chatroom_intent=new Intent(MainActivity.this,ChatRoom.class);
            startActivity(chatroom_intent);
        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn:

                if(isEverythingFilled()){

                    String email=Email.getText().toString();
                    String password=Password.getText().toString();

                    if(isEmailValid(email)){

                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            Log.d("demo", "signInWithEmail:success");
                                            Intent login_intent=new Intent(MainActivity.this,ChatRoom.class);
                                            startActivity(login_intent);

                                        } else {
                                            Log.d("demo", "signInWithEmail:failure", task.getException());
                                            Toast.makeText(MainActivity.this, "Login Not Successful",Toast.LENGTH_SHORT).show();

                                        }


                                    }
                                });
                    }else{
                        Email.setError("Invalid Email");
                    }
                }



                break;


            case R.id.signup_btn:
                Intent signup_intent=new Intent(MainActivity.this,SignUp.class);
                startActivity(signup_intent);
                break;
        }
    }

    public boolean isEmailValid(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isEverythingFilled(){
        int i=0;

        if(Email.getText().toString().equals("")){
            Email.setError("Please enter email");
            i=1;
        }
        if (Password.getText().toString().equals("")){
            Password.setError("Please enter password");
            i=1;
        }

        if(i==0){
            return true;
        }else{
            return false;
        }

    }

}

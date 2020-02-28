package com.example.cropmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {
    //views
    private Button signUpBtn;
    EditText emailEt,passwordEt, firstNameEt, lastNameEt;

    // Progression Dialog
    ProgressDialog progressDialog;

    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //init
        signUpBtn = findViewById(R.id.signUpBtn);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        firstNameEt = findViewById(R.id.firstNameEt);
        lastNameEt = findViewById(R.id.lastNameEt);

        //initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        //Handling sign up button click
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // input email , password
                String email = emailEt.getText().toString().trim();
                String password = passwordEt.getText().toString().trim();
                String firstName = firstNameEt.getText().toString().trim();
                String lastName = lastNameEt.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty()) {

                    // Validate
                    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        //set error and forcus to email edittext
                        emailEt.setError("Invalid Email");
                        emailEt.setFocusable(true);
                    }
                    else if (password.length() < 6){
                        //set error and forcus to password edittext
                        passwordEt.setError("Password length at least 6 characters");
                        passwordEt.setFocusable(true);
                    }
                    else {
                        registerUser(email,password,firstName,lastName); // register the user
                    }

                }
                else {
                    Toast.makeText(RegistrationActivity.this, "All Fields are required", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void registerUser(String email, String password, final String firstName, final String lastName) {
        //Email and password is valid ,show progression dialog and start registering
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, dismiss progress dialog and start registration
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();

                            //get user email and user id from auth
                            String email = user.getEmail();
                            String uid = user.getUid();
                            // when user is registered store user info in firebase realtime database too
                            //using HashMap
                            HashMap<Object, String> hashMap = new HashMap<>();
                            //put info in hasmap
                            hashMap.put("email",email);
                            hashMap.put("uid",uid);
                            hashMap.put("firstName",firstName);
                            hashMap.put("lastName",lastName);
                            //Firebase database instant
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            // path to store user data named "Users"
                            DatabaseReference reference = database.getReference("Users");
                            //put data within hashMap in database
                            reference.child(uid).setValue(hashMap);



                            Toast.makeText(RegistrationActivity.this,"Registered Successfully...",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegistrationActivity.this,DashboardActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //error , dismiss progress dialog and get and show the error message
                progressDialog.dismiss();
                Toast.makeText(RegistrationActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go previous activity
        return super.onSupportNavigateUp();
    }
}

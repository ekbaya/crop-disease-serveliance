package com.example.cropmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    //views
    private Button loginBtn;
    private Button registerBtn;
    EditText emailET,passwordET;
    TextView forgetPasswordTV;

    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    //Progression dialog
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        forgetPasswordTV = findViewById(R.id.forgetPasswordTV);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);

        mAuth = FirebaseAuth.getInstance();

        checkUserstatus();

        //init prograssion dialog
        progressDialog = new ProgressDialog(MainActivity.this);

        //Register button clicked
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });

        //Handling Login Onclick Events
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // input data
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()){
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        // Invalid email pattern set error
                        emailET.setError("Invalid Email");
                        emailET.setFocusable(true);
                    }
                    else {
                        // valid email pattern
                        loginUser(email,password);
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "All Fields are required!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //recovery password Textview click
        forgetPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoveryPasswordDialog();
            }
        });
    }

    private void showRecoveryPasswordDialog() {
        //AlertDialog
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        //set Layout Linear Layout
        LinearLayout linearLayout = new LinearLayout(this);
        // Views to set in dialog
        final EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        /*sets the main width of EditView to fit a text of n 'M' letters regardless of the actual
        text extension and text size*/
        emailEt.setMinEms(16);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        //buttons recover
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input email
                String email = emailEt.getText().toString().trim();
                beginRecovery(email);
            }
        });
        //buttons cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();
            }
        });

        //show dialog
        builder.create().show();

    }

    private void beginRecovery(String email) {
        // show progress dialog
        progressDialog.setMessage("Sending Email...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this,"Email sent",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                //get and show proper error message
                Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loginUser(String email, String password) {
        // show progress dialog
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //dismiss progress dialog
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            //user is logged in, so start Profile activity
                            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            //dismiss progress dialog
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //dismiss progress dialog
                progressDialog.dismiss();
                // error get and show error message
                Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
    protected void checkUserstatus() {
        // get current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // user is signed in ...
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish();
        } else {
            // user is not signed in...stay here
        }

    }

    @Override
    protected void onResume() {
        checkUserstatus();
        super.onResume();
    }

    @Override
    protected void onStart() {
        checkUserstatus();
        super.onStart();
    }

}

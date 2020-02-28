package com.example.cropmonitor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class DashboardActivity extends AppCompatActivity {

    //views
    private CardView logoutCard;
    private CardView cameraCard;
    private CardView locatioAgrovetCard;
    private CollapsingToolbarLayout toolBarTitle;

    //fire base
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolBarTitle = findViewById(R.id.toolBarTitle);


        //init
        logoutCard = findViewById(R.id.logoutCard);
        cameraCard = findViewById(R.id.cameraCard);
        locatioAgrovetCard = findViewById(R.id.locatioAgrovetCard);

        //init fire base
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        // Check user status
        checkUserstatus();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");

        logoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUserstatus();
            }
        });

        cameraCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, LivePreviewActivity.class));

            }
        });

        locatioAgrovetCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, PermissionActivity.class));
            }
        });

        Query query = databaseReference.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // check until required data is found
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    // get data
                    String name = "" + ds.child("firstName").getValue();

                    // set data
                    toolBarTitle.setTitle("Welcome "+name+"!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected void checkUserstatus() {
        // get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // user is signed in ... stay here
            uid = user.getUid();
        } else {
            // user is not signed in...
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }

    }
}

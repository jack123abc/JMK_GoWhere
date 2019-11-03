package com.example.jmk2018.jmk_gowhere;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.net.sip.SipErrorCode.TIME_OUT;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LogoActivity extends FrontPage {

    WebView webview;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private DatabaseReference mUsers;

    private static int TIME_OUT = 7500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_logo);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        webview = findViewById(R.id.webViewLogo);
        webview.loadUrl("file:///android_asset/index.html");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(LogoActivity.this, MainPage.class);
                startActivity(i);
                finish();
            }
        }, TIME_OUT);

        //Intent intentToMainPage = new Intent();
        //intentToMainPage.setClass(LogoActivity.this,MainPage.class);
        //startActivity(intentToMainPage);


    }

    // [START on_start_check_user]
    /*@Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser;
        currentUser = mAuth.getCurrentUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUI(currentUser);
                finish();
            }
        }, TIME_OUT);
    }*/
    // [END on_start_check_user]

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {

            mUsers = FirebaseDatabase.getInstance().getReference().child("Users");
            mUsers.child(user.getUid()).child("Email").setValue(user.getEmail());

            if (user.isEmailVerified()==true){

                Intent intentToMainPage = new Intent();
                intentToMainPage.setClass(LogoActivity.this,MainPage.class);
                startActivity(intentToMainPage);

            } else {

                Intent intentToFrontPage = new Intent();
                intentToFrontPage.setClass(LogoActivity.this,FrontPage.class);
                startActivity(intentToFrontPage);

            }

        } else {

            Intent intentToFrontPage = new Intent();
            intentToFrontPage.setClass(LogoActivity.this,FrontPage.class);
            startActivity(intentToFrontPage);

        }
    }
}

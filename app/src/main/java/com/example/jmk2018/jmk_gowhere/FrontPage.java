package com.example.jmk2018.jmk_gowhere;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FrontPage extends FirebaseUIActivity implements
        View.OnClickListener{

    private static final String TAG = "EmailPassword";
    private EditText mEmailField;
    private EditText mPasswordField;
    private Integer counter = 0;

    private Button emailCreateAccount;
    private TextView textLoginLater;

    private DatabaseReference mUsers;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private String username = "";

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        ImageView backgroundImage = (ImageView) findViewById(R.id.backgroundImage);
        emailCreateAccount = findViewById(R.id.emailCreateAccountButton2);
        emailCreateAccount.setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.signInWithEmailButton).setOnClickListener(this);
        findViewById(R.id.signInWithGmailButton).setOnClickListener(this);
        findViewById(R.id.textLoginLater).setOnClickListener(this);

        textLoginLater = findViewById(R.id.textLoginLater);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        if (ActivityCompat.checkSelfPermission(FrontPage.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(FrontPage.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FrontPage.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        textLoginLater.setPaintFlags(textLoginLater.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

    }




    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUI(user);

                            sendEmailVerification();

                            Toast.makeText(FrontPage.this, "Please sign in after you verify your email address.",
                                    Toast.LENGTH_SHORT).show();

                            emailCreateAccount.setText("Sign In");

                            emailCreateAccount.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    signIn(email, password);

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(FrontPage.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();

        if (i == R.id.signInWithEmailButton) {

            Intent intent = new Intent();
            intent.putExtra("signin",1);
            intent.setClass(FrontPage.this, CreateAccountActivity.class);
            startActivity(intent);
            //findViewById(R.id.signInWithGmailButton).setVisibility(View.INVISIBLE);

        } else if (i == R.id.signInWithGmailButton) {

            Intent intentToGoogleLogin = new Intent();
            intentToGoogleLogin.setClass(FrontPage.this, GoogleSignInActivity.class);
            startActivity(intentToGoogleLogin);
            //findViewById(R.id.signInWithEmailButton).setVisibility(View.INVISIBLE);

        } else if (i == R.id.signOutButton) {

            signOut();
            findViewById(R.id.signInWithEmailButton).setVisibility(View.VISIBLE);
            findViewById(R.id.signInWithGmailButton).setVisibility(View.VISIBLE);

        } else if (i == R.id.emailCreateAccountButton2) {

            Intent intent = new Intent();
            intent.putExtra("signin",0);
            intent.setClass(this,CreateAccountActivity.class);
            startActivity(intent);
            //showAlertDialog();
            //createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
            //showAlertDialog();

        } else if (i == R.id.textLoginLater) {

            Intent intentToMainPage = new Intent();
            intentToMainPage.setClass(FrontPage.this, MainPage.class);
            intentToMainPage.putExtra("notSignIn",true);
            startActivity(intentToMainPage);

        }


    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if (counter == 1){

            emailCreateAccount.setVisibility(View.VISIBLE);
            findViewById(R.id.textView3).setVisibility(View.VISIBLE);
            findViewById(R.id.textLoginLater).setVisibility(View.VISIBLE);
            counter = 0;

        } else {

            finishAffinity();

        }
        //finish();
        //finishAffinity();
    }

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            mUsers = FirebaseDatabase.getInstance().getReference().child("Users");

            mUsers.child(user.getUid()).child("Email").setValue(user.getEmail());

            if (user.isEmailVerified()==true){

                Intent intentToMainPage = new Intent();
                intentToMainPage.setClass(FrontPage.this,MainPage.class);
                startActivity(intentToMainPage);

            } else {

                Toast.makeText(FrontPage.this,
                        "Please verify your account first.",
                        Toast.LENGTH_SHORT).show();

            }



        } else {
            //mStatusTextView.setText(R.string.signed_out);
            //mDetailTextView.setText(null);
        }
    }

    private void sendEmailVerification() {
        // Disable button
        //findViewById(R.id.verifyEmailButton).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        //findViewById(R.id.verifyEmailButton).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(FrontPage.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(FrontPage.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            //updateUserProfileName(username);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(FrontPage.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            //mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    public void showAlertDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Type In Your Username");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) ->
                username = input.getText().toString());

        builder.setOnDismissListener(dialogInterface -> {
            if (username == ""){
                Toast.makeText(FrontPage.this,"Please type in your username",Toast.LENGTH_LONG).show();
                showAlertDialog();
            }
        });

        builder.show();

    }

    /*public void updateUserProfileName(String name){

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });

    }*/



}

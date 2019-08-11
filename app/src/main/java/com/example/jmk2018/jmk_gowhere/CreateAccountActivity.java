package com.example.jmk2018.jmk_gowhere;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccountActivity extends FirebaseUIActivity implements
        View.OnClickListener{

    private static final String TAG = "UsernameEmailPassword";

    private EditText mUsernameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mCreateAccount;
    private TextView mTitle;

    private DatabaseReference mUsers;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private String username = "";

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private Integer counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mUsernameField = findViewById(R.id.editTextUsername);
        mEmailField = findViewById(R.id.editTextEmail);
        mPasswordField = findViewById(R.id.editTextPassword);
        mCreateAccount = findViewById(R.id.buttonCreateAccount);
        mTitle = findViewById(R.id.textViewTitle);

        mAuth = FirebaseAuth.getInstance();

        mCreateAccount.setOnClickListener(this);

        if (ActivityCompat.checkSelfPermission(CreateAccountActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(CreateAccountActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateAccountActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        counter = getIntent().getIntExtra("signin",0);

        if (counter == 0){


        } else if (counter == 1){

            mTitle.setText("Sign In");
            mUsernameField.setVisibility(View.GONE);
            mCreateAccount.setText("Sign In");

        }


    }

    private boolean validateForm() {
        boolean valid = true;

        if (counter == 0){

            String username = mUsernameField.getText().toString();
            if (TextUtils.isEmpty(username)) {
                mUsernameField.setError("Required.");
                valid = false;
            } else {
                mUsernameField.setError(null);
            }

        } else if (counter == 1){

        }

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (counter == 0 && TextUtils.isEmpty(password)) {
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
                intentToMainPage.setClass(CreateAccountActivity.this,MainPage.class);
                startActivity(intentToMainPage);

            } else {

                Toast.makeText(CreateAccountActivity.this,
                        "Please verify your account first.",
                        Toast.LENGTH_SHORT).show();

            }

        } else {

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
                            Toast.makeText(CreateAccountActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(CreateAccountActivity.this,
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
                            Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
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

    private void createAccount(String username, String email, String password) {
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

                            Toast.makeText(CreateAccountActivity.this, "Please sign in after you verify your email address.",
                                    Toast.LENGTH_SHORT).show();

                            mCreateAccount.setText("Sign In");

                            mCreateAccount.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    mUsers = FirebaseDatabase.getInstance().getReference().child("Users");

                                    mUsers.child(user.getUid()).child("Username").setValue(username);

                                    signIn(email, password);


                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
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
    public void onClick(View view) {

        int i = view.getId();

        if (i == R.id.buttonCreateAccount){

            if (counter == 0) {

                //Toast.makeText(CreateAccountActivity.this,"0",Toast.LENGTH_LONG).show();
                createAccount(mUsernameField.getText().toString(), mEmailField.getText().toString(), mPasswordField.getText().toString());

            } else if (counter == 1){

                //Toast.makeText(CreateAccountActivity.this,"1",Toast.LENGTH_LONG).show();
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());

            }
            //Toast.makeText(CreateAccountActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();


        }

    }
}

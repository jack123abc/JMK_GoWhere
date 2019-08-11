package com.example.jmk2018.jmk_gowhere;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.instantsearch.helpers.InstantSearch;
import com.algolia.instantsearch.helpers.Searcher;
import com.algolia.instantsearch.ui.views.SearchBox;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class MainPage extends FirebaseUIActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private DrawerLayout mDrawerlayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mToggle;
    private Menu menu;

    private TextView navHeaderName;
    private TextView navHeaderEmail;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsers;
    private FirebaseUser currentUser;

    private Integer setGoogleName;
    private String username = "";

    //private Boolean notSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>GoWhere</font>"));

        mDrawerlayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.Left_Navigation);
        View headerView = navigationView.getHeaderView(0);
        navHeaderName = headerView.findViewById(R.id.nav_header_name);
        navHeaderEmail = headerView.findViewById(R.id.nav_header_email);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        setGoogleName = getIntent().getIntExtra("setGoogleName",0);

        //notSignIn = getIntent().getBooleanExtra("notSignIn",false);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        //-------------


        //navigationView.setItemIconTintList(null);
        //navigationView.setItemTextColor(null);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                        menuItem.setChecked(true);

                        int id = menuItem.getItemId();

                        if(id == R.id.nav_explore){
                            mViewPager.setCurrentItem(0);
                        } else if (id == R.id.nav_promotion){
                            mViewPager.setCurrentItem(1);
                        } else if (id == R.id.nav_settings){

                        } else if (id == R.id.nav_signout){
                            if (isSignIn() == true){
                                signOut();
                            }
                            Intent intent = new Intent(MainPage.this, FrontPage.class);
                            startActivity(intent);
                        } else if (id == R.id.nav_share){

                        } else if (id == R.id.nav_send){

                        }


                        mDrawerlayout.closeDrawer(GravityCompat.START);

                        return true;

                    }
                }
        );

        mDrawerlayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {
                // Respond when the drawer's position changes
            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                // Respond when the drawer is opened
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                clearMenuItem();
                // Respond when the drawer is closed
            }

            @Override
            public void onDrawerStateChanged(int i) {
                // Respond when the drawer motion state changes
            }
        });

        int tab = getIntent().getIntExtra("Tab",0);
        switch (tab) {
            case 0:
                mViewPager.setCurrentItem(0);
                break;
            case 1:
                mViewPager.setCurrentItem(1);
                break;
            default:
                mViewPager.setCurrentItem(0);
                break;
        }

        if (updateUI(currentUser) && isGoogleSignedIn() == false){
            //navHeaderName.setText(dataSnapshot.child("Username").getValue().toString());
            mUsers = FirebaseDatabase.getInstance().getReference().child("Users");

            mUsers.child(currentUser.getUid()).child("Username").addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String username = dataSnapshot.getValue().toString();

                            navHeaderName.setText(username);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );

            mUsers.child(currentUser.getUid()).child("Email").addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String email = dataSnapshot.getValue().toString();

                            navHeaderEmail.setText(email);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );
        } else if (updateUI(currentUser) && isGoogleSignedIn() == true){
            username = currentUser.getDisplayName();
            navHeaderName.setText(username);
            navHeaderEmail.setText(currentUser.getEmail());
            //mUsers.child(mAuth.getCurrentUser().getUid()).child("Username").setValue(username);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_page, menu);
        this.menu = menu;

        MenuItem searchViewItem = menu.findItem(R.id.action_search);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerlayout.openDrawer(GravityCompat.START);
                break;

            case R.id.action_search:
                Intent intent = new Intent();
                intent.setClass(MainPage.this, SearchItemActivity.class);
                intent.putExtra("key",2);
                //SoftKeyboardHelper.hide(MainPage.this,item.getActionView());
                startActivity(intent);
                break;

            default:
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a tab which is defined in the other class
            switch (position){
                case 0:
                    TabExplore tabExplore = new TabExplore();
                    return tabExplore;
                case 1:
                    TabPromotion tabPromotion = new TabPromotion();
                    return tabPromotion;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position){
            switch (position){
                case 0:
                    return getString(R.string.tab_text_1);
                case 1:
                    return getString(R.string.tab_text_2);

            }
            return null;
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();

            /*Intent intentToFrontPage = new Intent();
            intentToFrontPage.setClass(MainPage.this,FrontPage.class);
            startActivity(intentToFrontPage);*/
            finishAffinity();

        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit this application", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private boolean updateUI(FirebaseUser user) {

        if (user != null) {

            mUsers = FirebaseDatabase.getInstance().getReference().child("Users");
            mUsers.child(user.getUid()).child("Email").setValue(user.getEmail());

            if (user.isEmailVerified()==true){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isSignIn(){
        FirebaseUser currentUser;
        currentUser = mAuth.getCurrentUser();

        return updateUI(currentUser);

    }

    private void clearMenuItem(){

        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }

    }

    public void showAlertDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Type In Your Username");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                username = input.getText().toString();
                mUsers.child(mAuth.getCurrentUser().getUid()).child("Username").setValue(username);
                //Toast.makeText(MainPage.this,username,Toast.LENGTH_LONG).show();
            }
        });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (username == ""){
                    Toast.makeText(MainPage.this,"Please type in your username",Toast.LENGTH_LONG).show();
                    showAlertDialog();
                }
            }
        });

        builder.show();

    }

    private boolean isGoogleSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(MainPage.this) != null;
    }


}

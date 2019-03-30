package com.example.jmk2018.jmk_gowhere;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.instantsearch.helpers.InstantSearch;
import com.algolia.instantsearch.helpers.Searcher;
import com.algolia.instantsearch.ui.views.SearchBox;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class MainPage extends AppCompatActivity {

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

    //LinearLayout linHotSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        //linHotSearch = (LinearLayout) findViewById(R.id.linHotSearch);



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

        mDrawerlayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.Left_Navigation);

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
                        } else if (id == R.id.nav_frontpage){
                            Intent intent = new Intent(MainPage.this, FrontPage.class);
                            startActivity(intent);
                        } else if (id == R.id.nav_settings){

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


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_page, menu);

        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        //SearchView searchView = (SearchView) searchViewItem.getActionView();
        //searchView.clearFocus();
        //searchView.setIconifiedByDefault(true);

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

            Intent intentToFrontPage = new Intent();
            intentToFrontPage.setClass(MainPage.this,FrontPage.class);
            startActivity(intentToFrontPage);

        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to Front Page", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


}

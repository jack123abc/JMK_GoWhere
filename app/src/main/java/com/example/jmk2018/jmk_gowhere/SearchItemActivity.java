package com.example.jmk2018.jmk_gowhere;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.Update;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.instantsearch.helpers.InstantSearch;
import com.algolia.instantsearch.helpers.Searcher;
import com.algolia.instantsearch.model.SearchResults;
import com.algolia.instantsearch.ui.views.Hits;
import com.algolia.instantsearch.ui.views.SearchBox;
import com.algolia.instantsearch.utils.ItemClickSupport;
import com.algolia.search.saas.Query;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import io.paperdb.Paper;
import io.reactivex.Flowable;
import jp.wasabeef.picasso.transformations.ColorFilterTransformation;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class SearchItemActivity extends AppCompatActivity {

    private static final String ALGOLIA_APP_ID = "SAA6306HQC";
    private static final String ALGOLIA_SEARCH_API_KEY = "9291eea64bd5e57769451aa5f72f560f";
    private static final String ALGOLIA_INDEX_NAME = "Database";

    private Searcher searcher;
    private Hits hits;
    private SearchBox searchBox;

    private ConstraintLayout searchWithoutHits;
    private FloatingActionButton fab;
    private ImageView hotsearch1;
    private ImageView hotsearch2;
    private ImageView hotsearch3;
    private TextView hotsearchtxt1;
    private TextView hotsearchtxt2;
    private TextView hotsearchtxt3;

    private Boolean counter;
    private Integer key;
    private String txt;

    private DatabaseReference mDatabaseHotSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_item);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        final String TAG = "hihi";

        searchBox = findViewById(R.id.searchBox);
        hits = findViewById(R.id.hits);
        searchWithoutHits = findViewById(R.id.searchWithoutHits);
        fab = findViewById(R.id.fab);
        hotsearch1 = findViewById(R.id.hotsearch1);
        hotsearch2 = findViewById(R.id.hotsearch2);
        hotsearch3 = findViewById(R.id.hotsearch3);
        hotsearchtxt1 = findViewById(R.id.hotsearchtxt1);
        hotsearchtxt2 = findViewById(R.id.hotsearchtxt2);
        hotsearchtxt3 = findViewById(R.id.hotsearchtxt3);
        key = getIntent().getIntExtra("key",0);
        txt = getIntent().getStringExtra("txt");

        mDatabaseHotSearch = FirebaseDatabase.getInstance().getReference().child("HotSearch");
        mDatabaseHotSearch.keepSynced(true);

        searcher = Searcher.create(ALGOLIA_APP_ID, ALGOLIA_SEARCH_API_KEY, ALGOLIA_INDEX_NAME);
        final InstantSearch helper = new InstantSearch(SearchItemActivity.this, searcher);

        ImageView searchClose = (ImageView) searchBox.findViewById(android.support.v7.appcompat.R.id.search_close_btn);

        if (key == 1){

            //searchView.setVisibility(View.GONE);
            searchBox.setVisibility(View.VISIBLE);
            searchWithoutHits.setVisibility(View.GONE);
            hits.setVisibility(View.VISIBLE);
            searcher.search(txt);

        } else if (key == 2){

            hideKeyboard();

        }

        searchBox.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.d(TAG, "onQueryTextSumbit = " + query);

                helper.search(query);

                searchBox.clearFocus();
                return true;

            }

            @Override
            public boolean onQueryTextChange(String s) {

                Log.d(TAG, "onQueryTextChange = " + s);

                if(s == null || s.length()==0){

                    searchWithoutHits.setVisibility(View.VISIBLE);
                    hits.setVisibility(View.INVISIBLE);

                }else {

                    searchWithoutHits.setVisibility(View.INVISIBLE);
                    hits.setVisibility(View.VISIBLE);
                    helper.search(s);

                }

                return true;
            }

        });

        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {

                SoftKeyboardHelper.hide(SearchItemActivity.this,view);
                fab.setVisibility(View.GONE);

                Log.d(TAG,Paper.book().getAllKeys().toString());
                //Toast.makeText(SearchItemActivity.this,,Toast.LENGTH_LONG).show();

            }
        });

        searchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hits.setVisibility(View.GONE);
                searchWithoutHits.setVisibility(View.VISIBLE);
                searchBox.setQuery("",false);
                searchBox.requestFocus();

            }
        });

        mDatabaseHotSearch.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String imgUrl1 = dataSnapshot.child("9").getValue(String.class);
                String imgUrl2 = dataSnapshot.child("10").getValue(String.class);
                String imgUrl3 = dataSnapshot.child("11").getValue(String.class);

                String name1 = dataSnapshot.child("12").getValue(String.class);
                String name2 = dataSnapshot.child("13").getValue(String.class);
                String name3 = dataSnapshot.child("14").getValue(String.class);

                hotsearchtxt1.setText(name1);
                hotsearchtxt2.setText(name2);
                hotsearchtxt3.setText(name3);

                Picasso.get().load(imgUrl1).
                        //transform(new CropCircleTransformation()).
                        transform(new ColorFilterTransformation(Color.argb(60, 100, 100, 100))).
                        into(hotsearch1);
                Picasso.get().load(imgUrl2).
                        //transform(new CropCircleTransformation()).
                        transform(new ColorFilterTransformation(Color.argb(60, 100, 100, 100))).
                        into(hotsearch2);
                Picasso.get().load(imgUrl3).
                        //transform(new CropCircleTransformation()).
                        transform(new ColorFilterTransformation(Color.argb(60, 100, 100, 100))).
                        into(hotsearch3);

                hotsearch1.setOnClickListener(view -> {

                    //searchView.setVisibility(View.GONE);
                    searchBox.setVisibility(View.VISIBLE);
                    //searchIcon.setVisibility(View.GONE);
                    searchWithoutHits.setVisibility(View.GONE);
                    hits.setVisibility(View.VISIBLE);
                    searcher.search(name1);
                    SoftKeyboardHelper.hide(SearchItemActivity.this,view);

                });

                hotsearch2.setOnClickListener(view -> {

                    //searchView.setVisibility(View.GONE);
                    searchBox.setVisibility(View.VISIBLE);
                    //searchIcon.setVisibility(View.GONE);
                    searchWithoutHits.setVisibility(View.GONE);
                    hits.setVisibility(View.VISIBLE);
                    searcher.search(name2);
                    SoftKeyboardHelper.hide(SearchItemActivity.this,view);

                });

                hotsearch3.setOnClickListener(view -> {

                    //searchView.setVisibility(View.GONE);
                    searchBox.setVisibility(View.VISIBLE);
                    //searchIcon.setVisibility(View.GONE);
                    searchWithoutHits.setVisibility(View.GONE);
                    hits.setVisibility(View.VISIBLE);
                    searcher.search(name3);
                    SoftKeyboardHelper.hide(SearchItemActivity.this,view);

                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });



        hits.setOnItemClickListener((recyclerView, position, v) -> {
            JSONObject hit = hits.get(position);

            String name = null;// Or "foo" if you want the "foo" attribute
            String location = null;
            String category = null;
            String imageUrl = null;
            String phoneNumber = null;
            String link = null;
            String address= null;
            Double latitude = null;
            Double longitude = null;
            String post_key = null;
            String keyword = null;

            try {
                name = hit.getString("name");
                location = hit.getString("location");
                category = hit.getString("category");
                imageUrl = hit.getString("imageUrl");
                phoneNumber = hit.getString("phone");
                link = hit.getString("link");
                address = hit.getString("address");
                latitude = hit.getDouble("latitude");
                longitude = hit.getDouble("longitude");
                post_key = hit.getString("objectID");
                keyword  = hit.getString("keywords");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent hitsTabbedIntent = new Intent (SearchItemActivity.this, CardViewTabbed.class);
            hitsTabbedIntent.putExtra("Name",name);
            hitsTabbedIntent.putExtra("Location",location);
            hitsTabbedIntent.putExtra("Category",category);
            hitsTabbedIntent.putExtra("ImageUrl",imageUrl);
            hitsTabbedIntent.putExtra("Phone Number",phoneNumber);
            hitsTabbedIntent.putExtra("Link",link);
            hitsTabbedIntent.putExtra("Address",address);
            hitsTabbedIntent.putExtra("Latitude",latitude);
            hitsTabbedIntent.putExtra("Longitude",longitude);
            hitsTabbedIntent.putExtra("search_post_key",post_key);
            hitsTabbedIntent.putExtra("key",1);
            hitsTabbedIntent.putExtra("Tag",keyword);
            startActivity(hitsTabbedIntent);
        });

    }

    @Override
    public void onBackPressed() {

        counter = true;

        if (searchBox.getQuery().length() >= 1){

            searchBox.setQuery("", false);
            hits.setVisibility(View.GONE);
            searchWithoutHits.setVisibility(View.VISIBLE);
            //Toast.makeText(SearchItemActivity.this,searchBox.getQuery().toString(),Toast.LENGTH_LONG).show();
            counter = false;

        }

        if (searchBox.getQuery().length() == 0 && counter){

            super.onBackPressed();

        }



    }

    @Override
    protected void onDestroy() {

        searcher.destroy();

        //Paper.book().destroy();
        super.onDestroy();
    }

    @Override
    public void onStart() {

        //Toast.makeText(SearchItemActivity.this,"gg",Toast.LENGTH_LONG).show();

        super.onStart();

    }

    private void hideKeyboard() {
        searchBox.clearFocus();
    }


}

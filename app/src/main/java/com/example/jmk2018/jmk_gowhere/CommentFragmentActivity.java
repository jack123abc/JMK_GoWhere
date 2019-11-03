package com.example.jmk2018.jmk_gowhere;

import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;

import com.example.jmk2018.jmk_gowhere.ui.commentfragment2.CommentFragment2;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CommentFragmentActivity extends AppCompatActivity {

    private DatabaseReference mComments;
    DatabaseReference mUsers;

    private RecyclerView allCommentsRecyclerView;
    private String post_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, CommentFragment2.newInstance())
                    .commitNow();
        }

        post_key = getIntent().getStringExtra("post_key");

        mUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mComments = FirebaseDatabase.getInstance().getReference().child("Comments").child(post_key);

        allCommentsRecyclerView = findViewById(R.id.allCommentsRecyclerView);
        allCommentsRecyclerView.setHasFixedSize(false);
        allCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Comments</font>"));

        toolbar.setNavigationOnClickListener(view -> finish());

        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> adapter = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>
                (Comments.class, R.layout.comments_cardview, CommentsViewHolder.class, mComments){

            @Override
            protected void populateViewHolder(final CommentsViewHolder viewHolder, final Comments model, int position){

                mUsers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String username = dataSnapshot.child(model.getPublisherId()).child("Username").getValue(String.class);
                        viewHolder.setPublisher(username);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                viewHolder.setContent(model.getComment());

            }
        };

        allCommentsRecyclerView.setAdapter(adapter);

    }
}

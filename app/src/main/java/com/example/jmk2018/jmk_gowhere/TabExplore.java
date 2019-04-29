package com.example.jmk2018.jmk_gowhere;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class TabExplore extends Fragment {

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabaseHotSearch;

    private RecyclerView exploreRecyclerview;

    private boolean mProcessLike = false;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsers;
    private FirebaseUser currentUser;

    private ImageView imgHotSearch1;
    private ImageView imgHotSearch2;
    private ImageView imgHotSearch3;
    private ImageView imgHotSearch4;

    private TextView txtHotSearch1;
    private TextView txtHotSearch2;
    private TextView txtHotSearch3;
    private TextView txtHotSearch4;

    private Integer signOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.explore_main_page, container, false);
        exploreRecyclerview = rootView.findViewById(R.id.exploreRecyclerview);
        exploreRecyclerview.setHasFixedSize(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Database");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseHotSearch = FirebaseDatabase.getInstance().getReference().child("HotSearch");

        mDatabase.keepSynced(true);
        mDatabaseLike.keepSynced(true);
        mDatabaseHotSearch.keepSynced(true);

        LinearLayoutManager exploreLLM = new LinearLayoutManager(getActivity());
        exploreRecyclerview.setLayoutManager(exploreLLM);
        exploreRecyclerview.setNestedScrollingEnabled(false);
        exploreLLM.setAutoMeasureEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        imgHotSearch1 = rootView.findViewById(R.id.imgHotSearch1);
        imgHotSearch2 = rootView.findViewById(R.id.imgHotSearch2);
        imgHotSearch3 = rootView.findViewById(R.id.imgHotSearch3);
        imgHotSearch4 = rootView.findViewById(R.id.imgHotSearch4);

        txtHotSearch1 = rootView.findViewById(R.id.txtHotSearch1);
        txtHotSearch2 = rootView.findViewById(R.id.txtHotSearch2);
        txtHotSearch3 = rootView.findViewById(R.id.txtHotSearch3);
        txtHotSearch4 = rootView.findViewById(R.id.txtHotSearch4);

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseRecyclerAdapter<Database, CardviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Database, CardviewHolder>
                (Database.class, R.layout.database_cardview_new, CardviewHolder.class, mDatabase){

            @Override
            protected void populateViewHolder(final CardviewHolder viewHolder, final Database model, int position){

                final String post_key = getRef(position).getKey();

                viewHolder.setCategory(model.getCategory());
                viewHolder.setName(model.getName());
                viewHolder.setLocation(model.getLocation());
                viewHolder.setPhone(model.getPhone());
                viewHolder.setLink(model.getLink());
                viewHolder.setAddress(model.getAddress());
                viewHolder.setImage(getContext(), model.getImageUrl());
                viewHolder.setLatitude(model.getLatitude());
                viewHolder.setLongitude(model.getLongitude());
                viewHolder.setKeywords(model.getKeywords());
                viewHolder.setLikeButton(post_key);
                viewHolder.setNumOfLikes(post_key);

                viewHolder.mView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){

                        Intent intent = new Intent(view.getContext(),CardViewTabbed.class);
                        intent.putExtra("post_key",post_key);
                        intent.putExtra("ImageUrl",model.getImageUrl());
                        intent.putExtra("Category",model.getCategory());
                        intent.putExtra("Name",model.getName());
                        intent.putExtra("Phone Number",model.getPhone());
                        intent.putExtra("Link",model.getLink());
                        intent.putExtra("Address",model.getAddress());
                        intent.putExtra("Latitude", model.getLatitude());
                        intent.putExtra("Longitude", model.getLongitude());
                        intent.putExtra("Tag",model.getKeywords());
                        intent.putExtra("key",0);

                        view.getContext().startActivity(intent);

                    }
                });



                viewHolder.likeButton.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view){

                        mProcessLike = true;

                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (updateUI(currentUser) == true){

                                        if (mProcessLike) {

                                            if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                                mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();

                                                mProcessLike = false;

                                            } else {

                                                mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("Hi");

                                                mProcessLike = false;

                                            }
                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });



                    }


                });




            }
        };

        exploreRecyclerview.setAdapter(firebaseRecyclerAdapter);

        mDatabaseHotSearch.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String imgUrl1 = dataSnapshot.child("1").getValue(String.class);
                String imgUrl2 = dataSnapshot.child("2").getValue(String.class);
                String imgUrl3 = dataSnapshot.child("3").getValue(String.class);
                String imgUrl4 = dataSnapshot.child("4").getValue(String.class);
                String txt1 = dataSnapshot.child("5").getValue(String.class);
                String txt2 = dataSnapshot.child("6").getValue(String.class);
                String txt3 = dataSnapshot.child("7").getValue(String.class);
                String txt4 = dataSnapshot.child("8").getValue(String.class);

                Picasso.get().load(imgUrl1).
                        transform(new RoundCornersTransformation_not_used(20, 1, true, true)).
                        into(imgHotSearch1);
                Picasso.get().load(imgUrl2).
                        transform(new RoundCornersTransformation_not_used(20, 1, true, true)).
                        into(imgHotSearch2);
                Picasso.get().load(imgUrl3).
                        transform(new RoundCornersTransformation_not_used(20, 1, true, true)).
                        into(imgHotSearch3);
                Picasso.get().load(imgUrl4).
                        transform(new RoundCornersTransformation_not_used(20, 1, true, true)).
                        into(imgHotSearch4);

                txtHotSearch1.setText(txt1);
                txtHotSearch2.setText(txt2);
                txtHotSearch3.setText(txt3);
                txtHotSearch4.setText(txt4);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

        imgHotSearch1.setOnClickListener(view -> {

            Intent intent = new Intent(view.getContext(),SearchItemActivity.class);
            intent.putExtra("key",1);
            intent.putExtra("txt",txtHotSearch1.getText().toString());
            startActivity(intent);

        });

        imgHotSearch2.setOnClickListener(view -> {

            Intent intent = new Intent(view.getContext(),SearchItemActivity.class);
            intent.putExtra("key",1);
            intent.putExtra("txt",txtHotSearch2.getText().toString());
            startActivity(intent);

        });

        imgHotSearch3.setOnClickListener(view -> {

            Intent intent = new Intent(view.getContext(),SearchItemActivity.class);
            intent.putExtra("key",1);
            intent.putExtra("txt",txtHotSearch3.getText().toString());
            startActivity(intent);

        });

        imgHotSearch4.setOnClickListener(view -> {

            Intent intent = new Intent(view.getContext(),SearchItemActivity.class);
            intent.putExtra("key",1);
            intent.putExtra("txt",txtHotSearch4.getText().toString());
            startActivity(intent);

        });
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

}

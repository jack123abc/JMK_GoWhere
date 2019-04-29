package com.example.jmk2018.jmk_gowhere;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class TabPromotion extends Fragment{

    private DatabaseReference mPromotion;
    private DatabaseReference mDatabase;
    private RecyclerView promotionRecyclerview;
    /*String pAddress;
    String pLink;
    String pCategory;
    String pImageUrl;
    Double pLatitude;
    Double pLongitude;
    String pName;
    String pPhone;*/


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.promotion_main_page, container, false);
        promotionRecyclerview = rootView.findViewById(R.id.promotionRecyclerview);
        promotionRecyclerview.setHasFixedSize(true);

        mPromotion = FirebaseDatabase.getInstance().getReference().child("Promotion");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Database");

        LinearLayoutManager promotionLLM = new LinearLayoutManager(getActivity());
        promotionRecyclerview.setLayoutManager(promotionLLM);

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseRecyclerAdapter<PromotionDatabase, PromotionCardviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PromotionDatabase, PromotionCardviewHolder>
                (PromotionDatabase.class, R.layout.promotion_database_cardview, PromotionCardviewHolder.class, mPromotion){
            @Override
            protected void populateViewHolder(PromotionCardviewHolder viewHolder, final PromotionDatabase model, int position){
                viewHolder.setCategory(model.getCategory());
                viewHolder.setName(model.getName());
                viewHolder.setImage(getContext(), model.getImageUrl());
                viewHolder.setLogo(getContext(),model.getLogoUrl());
                String pPostKey = viewHolder.setPostKey(model.getPostKey());

                mDatabase.child(pPostKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String pAddress = dataSnapshot.child("address").getValue(String.class);
                        String pCategory = dataSnapshot.child("category").getValue(String.class);
                        String pImageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                        String pName = dataSnapshot.child("name").getValue(String.class);
                        String pPhone = dataSnapshot.child("phone").getValue(String.class);
                        String pLink = dataSnapshot.child("link").getValue(String.class);
                        Double pLatitude = dataSnapshot.child("latitude").getValue(Double.TYPE);
                        Double pLongitude = dataSnapshot.child("longitude").getValue(Double.TYPE);

                        viewHolder.mView.setOnClickListener(view -> {
                            Intent intent = new Intent(view.getContext(),CardViewTabbed.class);
                            intent.putExtra("post_key",pPostKey);
                            intent.putExtra("ImageUrl",pImageUrl);
                            intent.putExtra("Category",pCategory);
                            intent.putExtra("Name",pName);
                            intent.putExtra("Phone Number",pPhone);
                            intent.putExtra("Link",pLink);
                            intent.putExtra("Address",pAddress);
                            intent.putExtra("Latitude", pLatitude);
                            intent.putExtra("Longitude", pLongitude);
                            intent.putExtra("key",0);

                            view.getContext().startActivity(intent);


                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }
        };

        promotionRecyclerview.setAdapter(firebaseRecyclerAdapter);

    }





}

package com.example.jmk2018.jmk_gowhere;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Preconditions;

public class CardviewHolder extends RecyclerView.ViewHolder{

    View mView;
    String cCategory;
    String cName;
    String cLocation;
    String cImage;
    String cPhoneNumber;
    String cAddress;
    Double cLatitude;
    Double cLongitude;
    String cKeywords;
    String cLink;
    Long cNumOfLikes;
    Integer cBooking;
    Integer cLike;
    String cGroup;

    ImageView likeButton;
    DatabaseReference mDatabaseLike;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    DatabaseReference mUsers;
    FirebaseUser currentUser;

    public Integer getcLike() {
        return cLike;
    }

    public void setcLike(Integer cLike) {
        this.cLike = cLike;
    }

    public String getcGroup() {
        return cGroup;
    }

    public void setcGroup(String cGroup) {
        this.cGroup = cGroup;
    }

    public CardviewHolder(View itemView){
        super(itemView);

        mView = itemView;

        likeButton = (ImageView) mView.findViewById(R.id.likeButton);

        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Database");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        mDatabaseLike.keepSynced(true);

        /*itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), CardViewTabbed.class);
                intent.putExtra("ImageUrl",cImage);
                intent.putExtra("Category",cCategory);
                intent.putExtra("Name",cName);
                //intent.putExtra("Location",cLocation);
                intent.putExtra("Phone Number",cPhoneNumber);
                intent.putExtra("Link",cLink);
                intent.putExtra("Address",cAddress);
                intent.putExtra("Latitude", cLatitude);
                intent.putExtra("Longitude", cLongitude);

                //intent.putExtra("Tab",0);

                v.getContext().startActivity(intent);
            }
        });*/



    }
    public void setCategory(String category){
        cCategory = category;
        TextView card_category = mView.findViewById(R.id.cardviewCategory);
        card_category.setText(category);
    }
    public void setName(String name){
        cName = name;
        TextView card_name = mView.findViewById(R.id.cardviewName);
        card_name.setText(name);
    }
    public void setLocation(String location){
        cLocation = location;
        TextView card_location = mView.findViewById(R.id.cardviewLocation);
        card_location.setText(location);
    }
    public void setImage(Context context, String imageUrl){
        cImage = imageUrl;
        ImageView card_image = mView.findViewById(R.id.cardviewImage);
        //Picasso.with(context).load(imageUrl).fit().noFade().into(card_image);
        Glide.with(context).load(imageUrl).into(card_image);

    }
    public void setPhone(String phone){
        cPhoneNumber = phone;
    }
    public void setAddress(String address){
        cAddress = address;
    }
    public void setLatitude(Double lat){
        cLatitude = lat;
    }
    public void setLongitude(Double lng){
        cLongitude = lng;
    }
    public void setKeywords(String key){
        cKeywords = key;
    }
    public void setLink(String link){
        cLink = link;
        //TextView card_link = mView.findViewById(R.id.link);
        //card_link.setText(link);
    }
    public void setLikeButton(final String post_key){
        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (updateUI(mAuth.getCurrentUser())==true) {

                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){

                        likeButton.setImageResource(R.drawable.ic_favorite_black_24dp);


                    } else {

                        likeButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);


                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void setNumOfLikes(String post_key){

        mDatabaseLike.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cNumOfLikes = dataSnapshot.getChildrenCount();

                mDatabase.child(post_key).child("like").setValue(cNumOfLikes);

                TextView card_numOfLikes = (TextView) mView.findViewById(R.id.numOfLikes);
                card_numOfLikes.setText(String.valueOf(cNumOfLikes));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
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

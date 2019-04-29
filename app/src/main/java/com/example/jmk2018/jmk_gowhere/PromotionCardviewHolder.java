package com.example.jmk2018.jmk_gowhere;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PromotionCardviewHolder extends RecyclerView.ViewHolder {

    View mView;
    String pCategory;
    String pImage;
    String pLogo;
    String pName;
    String pPostKey;

    DatabaseReference pDatabase;


    public PromotionCardviewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;
    }

    public void setCategory(String category){
        pCategory = category;
        TextView promotion_category = mView.findViewById(R.id.promotionCategory);
        promotion_category.setText(category);
    }

    public void setName(String name){
        pName = name;
        TextView promotion_name = mView.findViewById(R.id.promotionName);
        promotion_name.setText(name);
    }

    public void setImage(Context context, String imageUrl){
        pImage = imageUrl;
        ImageView promotion_image = mView.findViewById(R.id.promotionImage);
        //Picasso.with(context).load(imageUrl).fit().noFade().into(card_image);

        Glide.with(context).load(imageUrl).into(promotion_image);
    }

    public void setLogo(Context context, String imageUrl){
        pLogo = imageUrl;
        ImageView promotion_logo = mView.findViewById(R.id.promotionLogo);
        //Picasso.with(context).load(imageUrl).fit().noFade().into(card_image);
        //Glide.with(context).load(imageUrl).into(promotion_logo);
        Picasso.get().load(imageUrl).
                transform(new RoundCornersTransformation_not_used(100, 1, true, true)).
                into(promotion_logo);
    }

    public String setPostKey(String postkey){
        pPostKey = postkey;
        return pPostKey;
    }

}

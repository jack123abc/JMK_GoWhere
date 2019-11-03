package com.example.jmk2018.jmk_gowhere;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class CommentsViewHolder extends RecyclerView.ViewHolder{

    String cPublisher;
    String cContent;

    public CommentsViewHolder(View itemView) {
        super(itemView);

    }

    public void setPublisher(String user){
        cPublisher = user;
        TextView card_publisher = (TextView) itemView.findViewById(R.id.commentsPublisher);
        card_publisher.setText(user);
    }

    public void setContent(String comment){
        cContent = comment;
        TextView card_content = (TextView) itemView.findViewById(R.id.commentsContent);
        card_content.setText(comment);
    }

}

package com.example.jmk2018.jmk_gowhere;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CommentFragment extends Fragment {

    private DatabaseReference mComments;
    private String postid = "";
    private String username = "";
    private RecyclerView allCommentsRecyclerView;

    public CommentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_comment, container, false);
        allCommentsRecyclerView = rootView.findViewById(R.id.allCommentsRecyclerView);
        mComments = FirebaseDatabase.getInstance().getReference().child("Comments").child("1");

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(Comments.class, R.layout.comments_cardview, CommentsViewHolder.class, mComments) {
            @Override
            protected void populateViewHolder(CommentsViewHolder viewHolder, Comments model, int position) {

                viewHolder.setContent(model.getComment());
                viewHolder.setPublisher(model.getPublisherId());

            }
        };

        allCommentsRecyclerView.setAdapter(firebaseRecyclerAdapter);


    }

}

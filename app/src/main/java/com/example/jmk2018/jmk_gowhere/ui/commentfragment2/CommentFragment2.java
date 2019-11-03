package com.example.jmk2018.jmk_gowhere.ui.commentfragment2;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jmk2018.jmk_gowhere.Comments;
import com.example.jmk2018.jmk_gowhere.CommentsViewHolder;
import com.example.jmk2018.jmk_gowhere.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CommentFragment2 extends Fragment {

    private DatabaseReference mComments;
    private String postid = "";
    private String username = "";
    private RecyclerView allCommentsRecyclerView;
    private TextView textMessage;

    private CommentFragment2ViewModel mViewModel;

    public static CommentFragment2 newInstance() {
        return new CommentFragment2();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.comment_fragment2_fragment, container, false);
        allCommentsRecyclerView = rootView.findViewById(R.id.allCommentsRecyclerView);
        textMessage = rootView.findViewById(R.id.message);
        mComments = FirebaseDatabase.getInstance().getReference().child("Comments");

        mComments.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String text = dataSnapshot.child("1").child("1").child("comment").getValue(String.class);
                textMessage.setText(text);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CommentFragment2ViewModel.class);
        // TODO: Use the ViewModel
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

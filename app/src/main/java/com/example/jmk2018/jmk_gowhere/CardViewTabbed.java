package com.example.jmk2018.jmk_gowhere;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.JetPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.sax.StartElementListener;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTargetFactory;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import org.w3c.dom.Text;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static android.widget.LinearLayout.VERTICAL;
import static java.security.AccessController.getContext;
import static jp.wasabeef.picasso.transformations.RoundedCornersTransformation.CornerType.ALL;

public class CardViewTabbed extends FirebaseUIActivity {

    private String cardCategory;
    private String cardName;
    private String cardImageUrl;
    private String cardPhoneNumber;
    private String cardLink;
    private String cardAddress;
    private Double cardLatitude;
    private Double cardLongitude;
    private String cardTag;

    private DatabaseReference mDatabasePhotos;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private DatabaseReference mComments;
    DatabaseReference mDatabaseLike;
    FirebaseAuth mAuth;
    DatabaseReference mUsers;
    FirebaseUser currentUser;
    private String username = "";

    private ImageView photo1;
    private ImageView photo2;
    private ImageView photo3;
    private ImageView photo4;
    private ImageView likeButton;

    private TextView allPhotos;
    private TextView numOfLikes;

    private static final int REQUEST_PHONE_CALL = 1;
    private static final int GALLERY_INTENT = 2;
    private static final int CAMERA_INTENT = 3;

    private String post_key;
    private String search_post_key;
    private Integer key;

    private Button uploadPhoto;

    private TextView allComments;
    private EditText editTextComment;
    private Button postComment;
    private Long numOfComments;

    private DrawerLayout drawer;
    private TextView navHeaderName;
    private TextView navHeaderEmail;

    private boolean mProcessLike = false;
    NavigationView navigationView;

    private RecyclerView commentsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_tabbed_new);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        cardCategory = getIntent().getStringExtra("Category");
        cardName = getIntent().getStringExtra("Name");
        cardAddress = getIntent().getStringExtra("Address");
        cardPhoneNumber = getIntent().getStringExtra("Phone Number");
        cardLink = getIntent().getStringExtra("Link");
        cardLatitude = getIntent().getDoubleExtra("Latitude", 0.00);
        cardLongitude = getIntent().getDoubleExtra("Longitude", 0.00);
        cardTag = getIntent().getStringExtra("Tag");
        key = getIntent().getIntExtra("key",0);
        if (key == 0){

            post_key = getIntent().getStringExtra("post_key");

        } else if (key == 1){

            post_key = getIntent().getStringExtra("search_post_key");

        }

        mDatabasePhotos = FirebaseDatabase.getInstance().getReference().child("Photos");
        mStorage = FirebaseStorage.getInstance().getReference();
        mUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Database");
        mComments = FirebaseDatabase.getInstance().getReference().child("Comments");
        Query recentComments = mComments.child(post_key).limitToLast(3);
        recentComments.keepSynced(true);

        allPhotos = (TextView) findViewById(R.id.allPhotos);
        allPhotos.setPaintFlags(allPhotos.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        allPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CardViewTabbed.this,PhotoGallery.class);
                intent.putExtra("post_key",post_key);
                startActivity(intent);

            }
        });

        ImageView imgImage = (ImageView) findViewById(R.id.image);
        cardImageUrl = getIntent().getStringExtra("ImageUrl");
        Glide.with(this).load(cardImageUrl).into(imgImage);

        TextView txtName = (TextView) findViewById(R.id.name);
        TextView txtCategory = (TextView) findViewById(R.id.category);
        TextView txtPhoneNumber = (TextView) findViewById(R.id.phoneNumber);
        TextView txtLink = (TextView) findViewById(R.id.link);
        TextView txtAddress = (TextView) findViewById(R.id.address);
        TextView txtKeywords = findViewById(R.id.tag);

        photo1 = (ImageView) findViewById(R.id.photo1);
        photo2 = (ImageView) findViewById(R.id.photo2);
        photo3 = (ImageView) findViewById(R.id.photo3);
        photo4 = (ImageView) findViewById(R.id.photo4);
        likeButton = (ImageView) findViewById(R.id.likeButton2);

        uploadPhoto = (Button) findViewById(R.id.uploadPhoto);

        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.cardviewtabbed_content);

        drawer = findViewById(R.id.drawer_layout);

        txtLink.setPaintFlags(txtLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        allComments = findViewById(R.id.allComments);
        postComment = findViewById(R.id.buttonPostComment);
        editTextComment = findViewById(R.id.editTextAddComment);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setHasFixedSize(false);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> adapter = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>
                (Comments.class, R.layout.comments_cardview, CommentsViewHolder.class, recentComments){

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

        commentsRecyclerView.setAdapter(adapter);

        mComments.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                numOfComments = dataSnapshot.getChildrenCount();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        allComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(CardViewTabbed.this,CommentFragmentActivity.class);
                intent.putExtra("post_key",post_key);
                startActivity(intent);
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>GoWhere</font>"));

        toolbar.setNavigationOnClickListener(view -> {

            drawer.openDrawer(GravityCompat.START);

        });

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navHeaderName = headerView.findViewById(R.id.nav_header_name);
        navHeaderEmail = headerView.findViewById(R.id.nav_header_email);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(
                item -> {
                    int id = item.getItemId();

                    if(id == R.id.nav_explore){
                        Intent intent = new Intent(CardViewTabbed.this, MainPage.class);
                        intent.putExtra("Tab",0);
                        startActivity(intent);
                        //mViewPager.setCurrentItem(0);
                    } else if (id == R.id.nav_promotion){
                        Intent intent = new Intent(CardViewTabbed.this, MainPage.class);
                        intent.putExtra("Tab",1);
                        startActivity(intent);
                        //mViewPager.setCurrentItem(1);
                    } else if (id == R.id.nav_settings){

                    } else if (id == R.id.nav_signout){
                        if (isSignIn() ==true){
                            signOut();
                        }
                        Intent intent = new Intent(CardViewTabbed.this, FrontPage.class);
                        startActivity(intent);
                    }

                    DrawerLayout drawer1 = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer1.closeDrawer(GravityCompat.START);
                    return true;

                }
        );

        if (updateUI(currentUser)){

            postComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(editTextComment.getText().toString().equals("")){

                        Toast.makeText(CardViewTabbed.this,"You can't post an empty comment.",Toast.LENGTH_SHORT).show();

                    } else {

                        mUsers.child(currentUser.getUid()).child("Username").addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        username = dataSnapshot.getValue().toString();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                }
                        );

                        //addComment(editTextComment.getText().toString(),currentUser.getUid(), username);

                        addComment();

                        Intent intent = new Intent();
                        intent.setClass(CardViewTabbed.this,CommentFragmentActivity.class);
                        intent.putExtra("postid",currentUser.getUid());
                        intent.putExtra("username",username);
                        intent.putExtra("post_key", post_key);
                        startActivity(intent);



                    }
                }
            });

            uploadPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PickImageDialog.build(new PickSetup()
                            .setButtonOrientation(LinearLayout.HORIZONTAL))
                            .setOnPickResult(new IPickResult() {
                                @Override
                                public void onPickResult(PickResult r) {
                                    //TODO: do what you have to...
                                    r.getBitmap();
                                    r.getError();
                                    r.getUri();

                                    Uri uri = r.getUri();

                                    final StorageReference filepath = mStorage.child("uploadPhoto").child(uri.getLastPathSegment());

                                    showProgressDialog();

                                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    final Uri downloadUrl = uri;
                                                    //Do what you want with the url

                                                    mDatabasePhotos.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            long numOfChild = dataSnapshot.child(post_key).getChildrenCount();

                                                            //Toast.makeText(CardViewTabbed.this, String.valueOf(numOfChild), Toast.LENGTH_LONG).show();

                                                            if (numOfChild > 0){

                                                                mDatabasePhotos.child(post_key).child(String.valueOf(numOfChild+1)).child("imageUrl").setValue(downloadUrl.toString());

                                                            }else{

                                                                mDatabasePhotos.child(post_key).child("1").child("imageUrl").setValue(downloadUrl.toString());

                                                            }


                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });



                                                    Toast.makeText(CardViewTabbed.this, "Upload Done!", Toast.LENGTH_LONG).show();

                                                    hideProgressDialog();

                                                }

                                            });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(CardViewTabbed.this, "Failed ", Toast.LENGTH_LONG).show();

                                            hideProgressDialog();
                                        }
                                    });

                                }
                            })
                            .setOnPickCancel(new IPickCancel() {
                                @Override
                                public void onCancelClick() {
                                    //TODO: do what you have to if user clicked cancel
                                }
                            }).show(getSupportFragmentManager());

                    //Intent pickPhoto = new Intent(Intent.ACTION_PICK);
                    //pickPhoto.setType("image/*");

                    //startActivityForResult(pickPhoto,GALLERY_INTENT);


                }
            });

            if (isGoogleSignedIn() == false){

                mUsers.child(currentUser.getUid()).child("Username").addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String username = dataSnapshot.getValue().toString();
                                navHeaderName.setText(username);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );

                mUsers.child(currentUser.getUid()).child("Email").addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String email = dataSnapshot.getValue().toString();
                                navHeaderEmail.setText(email);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );

            } else {

                username = currentUser.getDisplayName();
                navHeaderName.setText(username);
                navHeaderEmail.setText(currentUser.getEmail());

            }

        } else {

            postComment.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    Toast.makeText(CardViewTabbed.this,"Please sign in first.",Toast.LENGTH_SHORT).show();

                }
            });

            uploadPhoto.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    Toast.makeText(CardViewTabbed.this,"Please sign in first.",Toast.LENGTH_SHORT).show();

                }
            });

            navHeaderName.setText("");
            navHeaderEmail.setText("");

        }

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
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
                clearMenuItem();
                // Respond when the drawer is closed
            }

            @Override
            public void onDrawerStateChanged(int i) {
                // Respond when the drawer motion state changes
            }
        });

        txtCategory.setText(cardCategory);
        txtName.setText(cardName);
        txtPhoneNumber.setText(cardPhoneNumber);
        txtLink.setText(cardLink);
        txtAddress.setText(cardAddress);
        txtKeywords.setText(cardTag);

        mDatabasePhotos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //if (dataSnapshot.child(post_key).exists()){

                    final String imgUrl1 = dataSnapshot.child(post_key).child("1").child("imageUrl").getValue(String.class);
                    final String imgUrl2 = dataSnapshot.child(post_key).child("2").child("imageUrl").getValue(String.class);
                    final String imgUrl3 = dataSnapshot.child(post_key).child("3").child("imageUrl").getValue(String.class);
                    final String imgUrl4 = dataSnapshot.child(post_key).child("4").child("imageUrl").getValue(String.class);

                    int rotate1 = getCameraPhotoOrientation(imgUrl1);
                    int rotate2 = getCameraPhotoOrientation(imgUrl2);
                    int rotate3 = getCameraPhotoOrientation(imgUrl3);
                    int rotate4 = getCameraPhotoOrientation(imgUrl4);

                    Picasso.get().load(imgUrl1).centerCrop().resize(300,300).rotate(rotate1).
                            //transform(new CropSquareTransformation()).
                                    transform(new RoundedCornersTransformation(30,1,ALL)).
                            into(photo1);
                    Picasso.get().load(imgUrl2).centerCrop().resize(300,300).rotate(rotate2 ).
                            //transform(new CropSquareTransformation()).
                                    transform(new RoundedCornersTransformation(30,1,ALL)).
                            into(photo2);
                    Picasso.get().load(imgUrl3).centerCrop().resize(300,300).rotate(rotate3).
                            //transform(new CropSquareTransformation()).
                                    transform(new RoundedCornersTransformation(30,1,ALL)).
                            into(photo3);
                    Picasso.get().load(imgUrl4).centerCrop().resize(300,300).rotate(rotate4).
                            //transform(new CropSquareTransformation()).
                                    transform(new RoundedCornersTransformation(30,1,ALL)).
                            into(photo4);


                    photo1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(view.getContext(), FullScreenImageActivity.class);
                            intent.putExtra("ImageUrl", imgUrl1);

                            view.getContext().startActivity(intent);

                        }
                    });

                    photo2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(view.getContext(), FullScreenImageActivity.class);
                            intent.putExtra("ImageUrl", imgUrl2);

                            view.getContext().startActivity(intent);

                        }
                    });

                    photo3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(view.getContext(), FullScreenImageActivity.class);
                            intent.putExtra("ImageUrl", imgUrl3);

                            view.getContext().startActivity(intent);

                        }
                    });

                    photo4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(view.getContext(), FullScreenImageActivity.class);
                            intent.putExtra("ImageUrl", imgUrl4);

                            view.getContext().startActivity(intent);

                        }
                    });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        txtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapsActivity.class);

                intent.putExtra("Latitude", cardLatitude);
                intent.putExtra("Longitude", cardLongitude);

                v.getContext().startActivity(intent);
            }
        });

        txtPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri phoneNumber = Uri.parse("tel:" + cardPhoneNumber);
                Intent callIntent = new Intent(Intent.ACTION_CALL, phoneNumber);

                if (ContextCompat.checkSelfPermission(CardViewTabbed.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                  ActivityCompat.requestPermissions(CardViewTabbed.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);

                }
                else
                {
                    //ActivityCompat.requestPermissions(CardViewTabbed.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                     v.getContext().startActivity(callIntent);
                }

            }
        });

        txtLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Uri link = Uri.parse(cardLink);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, link);

                view.getContext().startActivity(browserIntent);


            }
        });

        numOfLikes = findViewById(R.id.numberOfLikes);

        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Long numberOfLikes = dataSnapshot.child("like").getValue(Long.TYPE);

                numOfLikes.setText(String.valueOf(numberOfLikes));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");

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

        likeButton.setOnClickListener(new View.OnClickListener(){

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

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK){

            Uri uri = data.getData();

            StorageReference filepath = mStorage.child("uploadPhoto").child(uri.getLastPathSegment());

            showProgressDialog();

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(CardViewTabbed.this, "Upload Done!", Toast.LENGTH_LONG).show();

                    hideProgressDialog();
                }
            });




        }

    }*/

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.card_view_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static int getCameraPhotoOrientation(String imageFilePath) {
        int rotate = 0;

        if (imageFilePath == null){

            return rotate;

        }else {

            try {

                ExifInterface exif;

                exif = new ExifInterface(imageFilePath);
                String exifOrientation = exif.getAttribute(android.support.media.ExifInterface.TAG_ORIENTATION);
                Log.d("exifOrientation", exifOrientation);
                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                Log.d(CardViewTabbed.class.getSimpleName(), "orientation :" + orientation);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return rotate;

        }
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

    private boolean isSignIn(){
        FirebaseUser currentUser;
        currentUser = mAuth.getCurrentUser();

        return updateUI(currentUser);

    }

    private void clearMenuItem(){

        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }

    }

    private boolean isGoogleSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(CardViewTabbed.this) != null;
    }

    private void addComment(){

        long counter;

        counter = numOfComments + 1;

        mComments = FirebaseDatabase.getInstance().getReference().child("Comments");

        mComments.child(post_key).child(String.valueOf(counter)).child("comment").setValue(editTextComment.getText().toString());
        mComments.child(post_key).child(String.valueOf(counter)).child("publisherId").setValue(currentUser.getUid());

        editTextComment.setText("");

        /*HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", editTextComment.getText().toString());
        hashMap.put("publisher", currentUser.getUid());

        mComments.child(post_key).push().setValue(hashMap);*/

        /*if (numOfComments == 0){

            mComments.child(post_key).child("1").child("comment").setValue(editTextComment.getText().toString());
            mComments.child(post_key).child("1").child("publisherId").setValue(currentUser.getUid());
            //mComments.child(post_key).child("1").child("publisher").setValue(username);

        } else {
        }*/



    }

    public void onStart() {
        super.onStart();

    }


    /*private String randomStringGenerator(int length){

        String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random RANDOM = new Random();

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();

    }*/

}

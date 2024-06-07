package com.bypriyan.infomate.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bypriyan.infomate.Adapter.AdapterComment;
import com.bypriyan.infomate.Constant;
import com.bypriyan.infomate.Model.ModelComment;
import com.bypriyan.infomate.R;
import com.bypriyan.infomate.databinding.ActivityAddPostBinding;
import com.bypriyan.infomate.databinding.ActivityPostDetailBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostDetail extends AppCompatActivity {

    private ActivityPostDetailBinding binding;
    String myUid, myEmail, myName, myDp, postId, postLikes, hisDp, hisName;
    boolean mProcessComment = false;
    private boolean mProcessLike = false;

    private ProgressDialog pd;
    List<ModelComment> commentList;
    AdapterComment adapterComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.background));
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        postId = getIntent().getStringExtra(Constant.KEY_POST_ID);

        myInfo();

        loadPostInfo();

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!binding.commentTxt.getText().toString().isEmpty()){
                    postComment();

                }else{
                    Toast.makeText(PostDetail.this, "Comment is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


        setLikes();
        loadComments();
    }

    private void loadComments() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        binding.recyclearView.setLayoutManager(layoutManager);

        commentList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_POSTS).child(postId)
                .child(Constant.KEY_COMMENT);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelComment modelComment = ds.getValue(ModelComment.class);
                    commentList.add(modelComment);

                    adapterComment = new AdapterComment(PostDetail.this, commentList, myUid, postId);
                    adapterComment.notifyDataSetChanged();
                    binding.recyclearView.setAdapter(adapterComment);
                }



            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    private void setLikes() {

        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child(Constant.KEY_LIKES);
        likeRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if(snapshot.child(postId).hasChild(myUid)){
                    binding.likeBtn.setImageResource(R.drawable.liked);
                }
//            else{
//                holder.LikeBtn.setColorFilter(R.color.green);
//                holder.LikeBtn.setBackgroundColor(R.color.red);
//            }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    private void postComment() {
        pd = new ProgressDialog(PostDetail.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Adding Comment");

        String comment = binding.commentTxt.getText().toString();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_POSTS)
                .child(postId).child(Constant.KEY_COMMENT);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_COMMENT_ID, timeStamp);
        hashMap.put(Constant.KEY_USERS_COMMENT, comment);
        hashMap.put(Constant.KEY_TIMESTAMP, timeStamp);
        hashMap.put(Constant.KEY_UID, myUid);
        hashMap.put(Constant.KEY_EMAIL, myEmail);
        hashMap.put(Constant.KEY_IMAGE, myDp);
        hashMap.put(Constant.KEY_NAME, myName);

        reference.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pd.dismiss();
                        Toast.makeText(PostDetail.this, "Comment added successfully", Toast.LENGTH_SHORT).show();
                        binding.commentTxt.setText("");
                        updateCommentCount();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                pd.dismiss();
                Toast.makeText(PostDetail.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        binding.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likePost();
            }
        });



    }

    private void likePost() {

       DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child(Constant.KEY_LIKES);
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child(Constant.KEY_POSTS);
        mProcessLike = true;
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(mProcessLike){
                    if(snapshot.child(postId).hasChild(myUid)){
                        postsRef.child(postId).child(Constant.KEY_POST_LIKES).setValue(""+(Integer.parseInt(postLikes)-1));
                        likeRef.child(postId).child(myUid).removeValue();
                        mProcessLike = false;
                    }else{
                        postsRef.child(postId).child(Constant.KEY_POST_LIKES).setValue(""+(Integer.parseInt(postLikes)+1));
                        likeRef.child(postId).child(myUid).setValue("Liked");
                        mProcessLike = false;

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }


    private void updateCommentCount() {
        mProcessComment = true;
       DatabaseReference reference =  FirebaseDatabase.getInstance().getReference(Constant.KEY_POSTS).child(postId);
       reference.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               if(mProcessComment){
                   String comments = "" + snapshot.child(Constant.KEY_POST_COMMENTS).getValue();
                   int newCommentBal = Integer.parseInt(comments) +1;
                   reference.child(Constant.KEY_POST_COMMENTS).setValue(""+newCommentBal);
                   mProcessComment = false;
               }
           }

           @Override
           public void onCancelled(@NonNull @NotNull DatabaseError error) {

           }
       });
    }

    private void myInfo() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        myUid = user.getUid();
        myEmail = user.getEmail();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);

        Query query = ref.orderByChild(Constant.KEY_UID).equalTo(myUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){

                    myDp = ""+ds.child(Constant.KEY_IMAGE).getValue();
                    myName = ""+ds.child(Constant.KEY_NAME).getValue();

                    try{
                        Glide
                                .with(PostDetail.this)
                                .load(myDp)
                                .centerCrop()
                                .placeholder(R.drawable.ic_person)
                                .into(binding.cProfile);

                    }catch (Exception e){

                    }




                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }


    private void loadPostInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constant.KEY_POSTS);

        Query query = ref.orderByChild(Constant.KEY_POST_ID).equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String pTitle = ""+ds.child(Constant.KEY_POST_TITLE).getValue();
                    String pDescr = ""+ds.child(Constant.KEY_POST_DESCRIPTION).getValue();
                    postLikes = ""+ds.child(Constant.KEY_POST_LIKES).getValue();
                    String postTimeStamp = ""+ds.child(Constant.KEY_POST_TIME).getValue();
                    String postImage = ""+ds.child(Constant.KEY_POST_IMAGE).getValue();
                    hisDp = ""+ds.child(Constant.KEY_IMAGE).getValue();
                    String uid = ""+ds.child(Constant.KEY_UID).getValue();
                    String uEmail = ""+ds.child(Constant.KEY_EMAIL).getValue();
                    hisName = ""+ds.child(Constant.KEY_NAME).getValue();
                    String commentCount = ""+ds.child(Constant.KEY_POST_COMMENTS).getValue();

                    Calendar cal = Calendar.getInstance(Locale.getDefault());
                    cal.setTimeInMillis(Long.parseLong(postTimeStamp));
                    String postTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

                    try{
                        Glide
                                .with(PostDetail.this)
                                .load(hisDp)
                                .centerCrop()
                                .placeholder(R.drawable.ic_person)
                                .into(binding.pProfilePic);
                    }catch (Exception e){

                    }


                    binding.Name.setText(hisName);
                    binding.pTime.setText(postTime);
                    binding.pTitle.setText(pTitle);
                    binding.pDescription.setText(pDescr);
                    binding.pLikes.setText(postLikes+ " Likes");
                    binding.pComments.setText(commentCount+" Comment");

                    if(postImage.equals("noImage")){

                        binding.pImage.setVisibility(View.GONE);
                        binding.imageBg.setVisibility(View.GONE);

                    }else{

                        binding.pImage.setVisibility(View.VISIBLE);
                        binding.imageBg.setVisibility(View.VISIBLE);

                        try{
                            Glide
                                    .with(PostDetail.this)
                                    .load(postImage)
                                    .centerCrop()
                                    .placeholder(R.drawable.logo)
                                    .into(binding.pImage);
                        }catch (Exception e){

                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}
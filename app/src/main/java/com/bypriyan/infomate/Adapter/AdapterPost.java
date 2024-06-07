package com.bypriyan.infomate.Adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.infomate.Constant;
import com.bypriyan.infomate.Model.ModelPost;
import com.bypriyan.infomate.R;
import com.bypriyan.infomate.activity.ContectUs;
import com.bypriyan.infomate.activity.EditProfile;
import com.bypriyan.infomate.activity.FullimageView;
import com.bypriyan.infomate.activity.PostDetail;
import com.bypriyan.infomate.activity.ThereProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyHolder> {

    Context context;
    List<ModelPost>postList;
    String myUid;
    private DatabaseReference likeRef;
    private DatabaseReference postsRef;

    private boolean mProcessLike = false;

    public AdapterPost(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likeRef = FirebaseDatabase.getInstance().getReference().child(Constant.KEY_LIKES);
        postsRef = FirebaseDatabase.getInstance().getReference().child(Constant.KEY_POSTS);
    }

    @NonNull
    @NotNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterPost.MyHolder holder, int position) {

        final String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getEmail();
        String uName = postList.get(position).getName();
        String uDp = postList.get(position).getImage();
        String pId = postList.get(position).getPostId();
        String pTitle = postList.get(position).getPostTitle();
        String pDescriptio = postList.get(position).getPostDescription();
        String pTime = postList.get(position).getPostTime();
        String pImage = postList.get(position).getPostImage();
        String pLiked = postList.get(position).getPostLikes();
        String commentCount = postList.get(position).getPostComments();

        //

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(Long.parseLong(pTime));
        String postTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        //

        holder.name.setText(uName);
        holder.pTime.setText(postTime);
        holder.pTitle.setText(pTitle);
        holder.pDescription.setText(pDescriptio);
        holder.pLikes.setText(pLiked +" Like");
        holder.pComments.setText(commentCount+" Comments");

        setLikes(holder, pId);

        Glide
                .with(context)
                .load(uDp)
                .centerCrop()
                .placeholder(R.drawable.ic_person)
                .into(holder.profileImage);


        if(pImage.equals("noImage")){

            holder.postImage.setVisibility(View.GONE);
            holder.imageBg.setVisibility(View.GONE);

        }else{

            holder.postImage.setVisibility(View.VISIBLE);
            holder.imageBg.setVisibility(View.VISIBLE);
            Glide
                    .with(context)
                    .load(pImage)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image)
                    .into(holder.postImage);
        }


        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullimageView.class);
                intent.putExtra("image", pImage);
                context.startActivity(intent);
            }
        });



        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreOptions(holder.moreBtn, uid, myUid, pId, pImage);
            }
        });

        holder.LikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pLikes = Integer.parseInt(postList.get(position).getPostLikes());
                mProcessLike = true;
                String postIde = postList.get(position).getPostId();
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(mProcessLike){
                            if(snapshot.child(postIde).hasChild(myUid)){
                                postsRef.child(postIde).child(Constant.KEY_POST_LIKES).setValue(""+(pLikes-1));
                                likeRef.child(postIde).child(myUid).removeValue();
                                mProcessLike = false;
                            }else{
                                postsRef.child(postIde).child(Constant.KEY_POST_LIKES).setValue(""+(pLikes+1));
                                likeRef.child(postIde).child(myUid).setValue("Liked");
                                mProcessLike = false;

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            }
        });

        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostDetail.class);
                intent.putExtra(Constant.KEY_POST_ID, pId);


                context.startActivity(intent);
            }
        });

        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BitmapDrawable bitmapDrawable = (BitmapDrawable)holder.postImage.getDrawable();
                if(bitmapDrawable == null){
                        shareTextOnly(pTitle, pDescriptio);
                }else{
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText(pTitle, pDescriptio, bitmap);

                }

            }
        });

        holder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ThereProfile.class);
                intent.putExtra("uid", uid);
                intent.putExtra("uName", uName);
                intent.putExtra("uEmail", uEmail);
                intent.putExtra("uDp", uDp);

                context.startActivity(intent);

            }
        });


    }

    private void shareImageAndText(String pTitle, String pDescriptio, Bitmap bitmap) {

        String shareBody = pTitle+"\n"+pDescriptio;
        Uri uri = shaveImageToShare(bitmap);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        shareIntent.setType("image/png");
        context.startActivity(Intent.createChooser(shareIntent,"Share Via"));

    }

    private Uri shaveImageToShare(Bitmap bitmap) {

        File imageFolder = new File(context.getCacheDir(),"images");
        Uri uri = null;

        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, "shared_images.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context, "com.bypriyan.infomate.fileprovider", file);

        }catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        return uri;


    }

    private void shareTextOnly(String pTitle, String pDescriptio) {

        String shareBody = pTitle+"\n"+pDescriptio;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(shareIntent,"Share Via"));

    }

    private void setLikes(MyHolder holder, String postKey) {

    likeRef.addValueEventListener(new ValueEventListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

            if(snapshot.child(postKey).hasChild(myUid)){
                holder.LikeBtn.setImageResource(R.drawable.liked);
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

    private void showMoreOptions(ImageView moreBtn, String uid, String myUid, String pId, String pImage) {

        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);

        if(uid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE, 0, 0 ,"Delete");
        }
        popupMenu.getMenu().add(Menu.NONE, 1 ,0 ,"View Details");
        popupMenu.getMenu().add(Menu.NONE, 2 ,0 ,"Report");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();
                if(id==0){

                    beginDelete(pId, pImage);
                }
                else if(id==1){
                    Intent intent = new Intent(context, PostDetail.class);
                    intent.putExtra(Constant.KEY_POST_ID, pId);
                    context.startActivity(intent);
                }else if(id==2){
                    Intent intent = new Intent(context, ContectUs.class);
                    context.startActivity(intent);
                }
                return false;
            }
        });
        popupMenu.show();

    }

    private void beginDelete(String pId, String pImage) {

        if(pImage.equals("noImage")){
            deleteWithoutImage(pId);
        }else{

            deleteWithImage(pId, pImage);
        }

    }

    private void deleteWithImage(String pId, String pImage) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Query fQuery = FirebaseDatabase.getInstance().getReference(Constant.KEY_POSTS);
                fQuery.orderByChild(Constant.KEY_POST_ID).equalTo(pId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(context, "Post Deleted successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
            progressDialog.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deleteWithoutImage(String pId) {

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");

        Query fQuery = FirebaseDatabase.getInstance().getReference(Constant.KEY_POSTS);
        fQuery.orderByChild(Constant.KEY_POST_ID).equalTo(pId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                Toast.makeText(context, "Post Deleted successfully", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImage;
        TextView name, pTime, pTitle, pDescription, pLikes, pComments;
        ImageView moreBtn, LikeBtn, commentBtn, shareBtn, postImage;
        MaterialCardView imageBg;
        LinearLayout profileLayout;

      public MyHolder(@NonNull @NotNull View itemView) {
          super(itemView);
          profileImage = itemView.findViewById(R.id.pProfilePic);
          name = itemView.findViewById(R.id.Name);
          pTime = itemView.findViewById(R.id.pTime);
          pTitle = itemView.findViewById(R.id.pTitle);
          pLikes = itemView.findViewById(R.id.pLikes);
          pDescription = itemView.findViewById(R.id.pDescription);
          moreBtn = itemView.findViewById(R.id.moreBtn);
          LikeBtn = itemView.findViewById(R.id.likeBtn);
          commentBtn = itemView.findViewById(R.id.commentBtn);
          shareBtn = itemView.findViewById(R.id.shareBtn);
          postImage = itemView.findViewById(R.id.pImage);

          imageBg = itemView.findViewById(R.id.imageBg);
          pComments = itemView.findViewById(R.id.pComments);
          profileLayout = itemView.findViewById(R.id.profileLayout);

      }
  }

}

package com.bypriyan.infomate.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.infomate.Constant;
import com.bypriyan.infomate.Model.ModelComment;
import com.bypriyan.infomate.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.MyHolder> {

    Context context;
    List<ModelComment> commentList;
    String myUid, postId;

    public AdapterComment(Context context, List<ModelComment> commentList, String myUid, String postId) {
        this.context = context;
        this.commentList = commentList;
        this.myUid = myUid;
        this.postId = postId;
    }

    @NonNull
    @NotNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_comments, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterComment.MyHolder holder, int position) {

        String uid = commentList.get(position).getUid();
        String email = commentList.get(position).getEmail();
        String name = commentList.get(position).getName();
        String image = commentList.get(position).getImage();
        String cId = commentList.get(position).getCommentId();
        String comment = commentList.get(position).getComment();
        String timestamp = commentList.get(position).getTimestamp();

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String postTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        holder.cName.setText(name);
        holder.cComment.setText(comment);
        holder.cTime.setText(postTime);

        try{
            Glide
                    .with(context)
                    .load(image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(holder.cProfile);

        }catch (Exception e){

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myUid.equals(uid)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                    builder.setTitle("Delete");
                    builder.setMessage("Are you sure to delete this comment?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_POSTS);
                            reference.child(postId).child(Constant.KEY_COMMENT).child(cId).removeValue();

                            //

                            DatabaseReference creference =  FirebaseDatabase.getInstance().getReference(Constant.KEY_POSTS).child(postId);

                            creference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    String comments = "" + snapshot.child(Constant.KEY_POST_COMMENTS).getValue();

                                        int newCommentBal = Integer.parseInt(comments) - 1;
                                        creference.child(Constant.KEY_POST_COMMENTS).setValue(""+newCommentBal);

                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();

                }else{

                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        CircleImageView cProfile;
        TextView cName, cComment, cTime;

        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            cProfile = itemView.findViewById(R.id.cProfileImage);
            cName = itemView.findViewById(R.id.cName);
            cComment = itemView.findViewById(R.id.cComment);
            cTime = itemView.findViewById(R.id.cTime);
        }
    }

}

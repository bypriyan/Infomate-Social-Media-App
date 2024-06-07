package com.bypriyan.infomate.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bypriyan.infomate.Constant;
import com.bypriyan.infomate.R;
import com.bypriyan.infomate.databinding.ActivityAddPostBinding;
import com.bypriyan.infomate.databinding.ActivityUserProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
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

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {

    private ActivityAddPostBinding binding;

    private MaterialCardView addImage;

    private final int REQ = 1;
    private Bitmap bitmap;
    private DatabaseReference reference, dbRef;
    private StorageReference storageReference;
    String DownloadUrl="noImage";
    private ProgressDialog pd;
    String name, emai, uid, dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.background));
        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);

        loadData();

        //CLICK LISTENER
        binding.addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        binding.uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.postTitle.getEditText().getText().toString().isEmpty()){
                    binding.postTitle.setError("Empty");
                    binding.postTitle.requestFocus();
                }else if(binding.postDescription.getText().toString().isEmpty()){
                    binding.postDescription.setError("Empty");
                    binding.postDescription.requestFocus();
                }else if(bitmap==null){
                    uploadData();

                }else{
                    uploadImg();
                }
            }
        });

    }

    private void loadData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String  myUid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);

        Query userQuery = reference.orderByChild(Constant.KEY_UID).equalTo(myUid);

        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    name = ""+ds.child(Constant.KEY_NAME).getValue();
                    emai = ""+ds.child(Constant.KEY_EMAIL).getValue();
                    uid = ""+ds.child(Constant.KEY_UID).getValue();
                    dp = ""+ds.child(Constant.KEY_IMAGE).getValue();

                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void uploadImg() {

        pd.setMessage("Uploading..");
        pd.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImg = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("Postes/").child(finalImg +"jpeg");
        final UploadTask uploadTask = filePath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(AddPostActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DownloadUrl = String.valueOf(uri);
                                    uploadData();
                                }
                            });
                        }
                    });
                }else{
                    pd.dismiss();
                    uploadNoImageData();
                }
            }
        });

    }

    private void uploadNoImageData() {
        String TimeStamp= String.valueOf(System.currentTimeMillis());

        String title = binding.postTitle.getEditText().getText().toString();
        String description = binding.postDescription.getText().toString();

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_UID,uid );
        hashMap.put(Constant.KEY_NAME, name );
        hashMap.put(Constant.KEY_EMAIL, emai);
        hashMap.put(Constant.KEY_IMAGE, dp );
        hashMap.put(Constant.KEY_POST_LIKES, "0");
        hashMap.put(Constant.KEY_POST_COMMENTS, "0");
        hashMap.put(Constant.KEY_POST_ID, TimeStamp);
        hashMap.put(Constant.KEY_POST_TITLE, title);
        hashMap.put(Constant.KEY_POST_DESCRIPTION,description );
        hashMap.put(Constant.KEY_POST_IMAGE, Constant.KEY_NO_IMAGE);
        hashMap.put(Constant.KEY_POST_TIME, TimeStamp );

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_POSTS);
        reference.child(TimeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pd.dismiss();
                        Toast.makeText(AddPostActivity.this, " Text Post Publish Successfully", Toast.LENGTH_SHORT).show();
                        binding.postTitle.getEditText().setText(null);
                        binding.postDescription.setText(null);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddPostActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadData() {

        String TimeStamp= String.valueOf(System.currentTimeMillis());

        String title = binding.postTitle.getEditText().getText().toString();
        String description = binding.postDescription.getText().toString();

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_UID,uid );
        hashMap.put(Constant.KEY_NAME, name );
        hashMap.put(Constant.KEY_EMAIL, emai);
        hashMap.put(Constant.KEY_IMAGE, dp );
        hashMap.put(Constant.KEY_POST_LIKES, "0");
        hashMap.put(Constant.KEY_POST_COMMENTS, "0");
        hashMap.put(Constant.KEY_POST_ID, TimeStamp);
        hashMap.put(Constant.KEY_POST_TITLE, title);
        hashMap.put(Constant.KEY_POST_DESCRIPTION,description );
        hashMap.put(Constant.KEY_POST_IMAGE, DownloadUrl);
        hashMap.put(Constant.KEY_POST_TIME, TimeStamp );

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_POSTS);
        reference.child(TimeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pd.dismiss();
                        Toast.makeText(AddPostActivity.this, " Post Publish Successfully", Toast.LENGTH_SHORT).show();
                        binding.postTitle.getEditText().setText(null);
                        binding.postDescription.setText(null);
                        binding.noticeImageView.setImageURI(null);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddPostActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ && resultCode == RESULT_OK){
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            binding.noticeImageView.setImageBitmap(bitmap);
        }
    }
}
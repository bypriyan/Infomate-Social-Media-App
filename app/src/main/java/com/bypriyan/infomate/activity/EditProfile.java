package com.bypriyan.infomate.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bypriyan.infomate.Constant;
import com.bypriyan.infomate.R;
import com.bypriyan.infomate.databinding.ActivityEditProfileBinding;
import com.bypriyan.infomate.databinding.ActivityUserProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class EditProfile extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private String Category;
    private final int REQ = 1;
    private Bitmap bitmap;
    String DownloadUrl="";
    private ProgressDialog pd;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference();

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);

        DownloadUrl = getIntent().getStringExtra(Constant.KEY_IMAGE);
        Category = getIntent().getStringExtra(Constant.KEY_CATEGORY);
        binding.branch.getEditText().setText(Category);
        binding.TeacherName.getEditText().setText(getIntent().getStringExtra(Constant.KEY_NAME));

        try{
            Glide
                    .with(EditProfile.this)
                    .load(getIntent().getStringExtra(Constant.KEY_IMAGE))
                    .centerCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(binding.addTeacherImg);

        }catch (Exception e){

        }
        binding.branch.getEditText().setText(Category);

        Category = binding.branch.getEditText().getText().toString();


        binding.addTeacherImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        binding.addTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.TeacherName.getEditText().getText().toString().isEmpty()){
                    binding.TeacherName.setError("Empty");
                    binding.TeacherName.requestFocus();
                }else if(binding.branch.getEditText().getText().toString().isEmpty()){
                    Toast.makeText(EditProfile.this, "Enter Branch", Toast.LENGTH_SHORT).show();
                }else if(bitmap==null){
                    uploadData();

                }else{
                    uploadImg();
                }
            }
        });

    }

    private void uploadData() {
        pd.setMessage("Updating your info..");
        pd.show();
        String TimeStamp= String.valueOf(System.currentTimeMillis());

        String name = binding.TeacherName.getEditText().getText().toString();
        String description = Category;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_NAME, name );
        hashMap.put(Constant.KEY_CATEGORY, binding.branch.getEditText().getText().toString());
        hashMap.put(Constant.KEY_IMAGE, DownloadUrl );

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pd.dismiss();
                        Toast.makeText(EditProfile.this, "Your info updated...", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                pd.dismiss();
                Toast.makeText(EditProfile.this, "Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void uploadImg() {

        pd.setMessage("Updating Your info..");
        pd.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImg = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("Group_Imgs/").child(finalImg +"jpeg");
        final UploadTask uploadTask = filePath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(EditProfile.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                }
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
            binding.addTeacherImg.setImageBitmap(bitmap);
        }
    }
}
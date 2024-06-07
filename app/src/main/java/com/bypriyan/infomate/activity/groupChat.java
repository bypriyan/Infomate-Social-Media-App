package com.bypriyan.infomate.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bypriyan.infomate.Adapter.AdapterGroupChate;
import com.bypriyan.infomate.Constant;
import com.bypriyan.infomate.Model.ModelGroupChate;
import com.bypriyan.infomate.Model.ModelUsers;
import com.bypriyan.infomate.R;
import com.bypriyan.infomate.databinding.ActivityGroupChatBinding;
import com.bypriyan.infomate.notifications.APIService;
import com.bypriyan.infomate.notifications.Client;
import com.bypriyan.infomate.notifications.Data;
import com.bypriyan.infomate.notifications.Response;
import com.bypriyan.infomate.notifications.Sender;
import com.bypriyan.infomate.notifications.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

public class groupChat extends AppCompatActivity {

    private ActivityGroupChatBinding binding;
    String groupId, groupIcon, groupName, myGroupRole="";
    private FirebaseAuth firebaseAuth;
    private Toolbar toolbar;
    private final int REQ = 1;
    private Bitmap bitmap;
    String DownloadUrl="noImage";
    private ArrayList<ModelGroupChate> groupChateList;
    private AdapterGroupChate adapterGroupChate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.background));
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        groupId = getIntent().getStringExtra("groupId");
        groupIcon = getIntent().getStringExtra("groupIcon");
        groupName = getIntent().getStringExtra("groupName");

        toolbar = findViewById(R.id.toolbars);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        loadGroupInfo();
        loadGroupMessages();

        loadMyGroupRole();

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.messageEt.getText().toString();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(groupChat.this, "Cannot send the empty message..", Toast.LENGTH_SHORT).show();
                }else {
                    sendMessage(message);
                }
                binding.messageEt.setText("");
            }
        });

        binding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();

            }
        });



    }

    private void loadMyGroupRole() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.child(groupId).child(Constant.KEY_PARTICIPENTS)
                .orderByChild(Constant.KEY_UID).equalTo(firebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        for(DataSnapshot ds: snapshot.getChildren()){
                            myGroupRole = ""+ds.child(Constant.KEY_ROLE).getValue();
                            invalidateOptionsMenu();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

    }

    private void loadGroupMessages() {

        groupChateList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.child(groupId).child(Constant.KEY_MESSAGE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                groupChateList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelGroupChate modelGroupChate = ds.getValue(ModelGroupChate.class);
                    groupChateList.add(modelGroupChate);
                }
                adapterGroupChate = new AdapterGroupChate(groupChat.this, groupChateList);
                binding.chatrecyclear.setAdapter(adapterGroupChate);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void sendMessage(String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);

        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_MESSAGE, message);
        hashMap.put(Constant.KEY_SENDER, firebaseAuth.getCurrentUser().getUid());
        hashMap.put(Constant.KEY_TIMESTAMP, timeStamp);
        hashMap.put("type", "text");

        reference.child(groupId).child(Constant.KEY_MESSAGE).child(timeStamp).setValue(hashMap)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });
//        // list
//
//        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHATLIST)
//                .child(myUid).child(hisUid);
//
//        chatRef1.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                if(!snapshot.exists()){
//                    chatRef1.child("id").setValue(hisUid);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//            }
//        });
//
//        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHATLIST)
//                .child(hisUid).child(myUid);
//
//        chatRef2.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                if(!snapshot.exists()){
//                    chatRef2.child("id").setValue(myUid);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//            }
//        });
    }

    private void loadGroupInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.orderByChild(Constant.KEY_GROUP_ID).equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    String timeStamp = ""+ds.child(Constant.KEY_TIMESTAMP).getValue();
                    String createdBy = ""+ds.child(Constant.KEY_CREATED_BY).getValue();

                    binding.groupName.setText(groupName);
                    try{
                        Glide
                                .with(groupChat.this)
                                .load(groupIcon)
                                .centerCrop()
                                .placeholder(R.drawable.ic_user)
                                .into(binding.imageProfile);
                    }catch (Exception e){

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

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
            sendImageMessage(uri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            binding.noticeImageView.setImageBitmap(bitmap);
        }
    }

    private void sendImageMessage(Uri uri) {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending image..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String timestamp = String.valueOf(System.currentTimeMillis());

        String fileNameAndPath ="ChatImages/"+"post_"+timestamp;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte [] data = baos.toByteArray();

            StorageReference reference = FirebaseStorage.getInstance().getReference().child(fileNameAndPath);
            reference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            DownloadUrl = uriTask.getResult().toString();
                            if(uriTask.isSuccessful()){
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);

                                String timeStamp = String.valueOf(System.currentTimeMillis());

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put(Constant.KEY_MESSAGE, DownloadUrl);
                                hashMap.put(Constant.KEY_SENDER, firebaseAuth.getCurrentUser().getUid());
                                hashMap.put(Constant.KEY_TIMESTAMP, timeStamp);
                                hashMap.put("type", "image");

                                reference.child(groupId).child(Constant.KEY_MESSAGE).child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                binding.messageEt.setText("");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(groupChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(groupChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sidedotmenu, menu);
        menu.findItem(R.id.LogOut).setVisible(false);
        menu.findItem(R.id.setting).setVisible(false);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.addPost).setVisible(false);
        menu.findItem(R.id.addGroup).setVisible(false);
        menu.findItem(R.id.bottomSheet).setVisible(false);

        if(myGroupRole.equals("creator") || myGroupRole.equals("admin")){
            menu.findItem(R.id.addParticipant).setVisible(true);
        }else{
            menu.findItem(R.id.addParticipant).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.addParticipant:
                Intent intent = new Intent(groupChat.this, groupParticipents.class);
                intent.putExtra(Constant.KEY_GROUP_ID, groupId);
                startActivity(intent);
                break;

        }

        switch (item.getItemId()){

            case R.id.groupInfo:
                Intent intent = new Intent(groupChat.this, GroupInfo.class);
                intent.putExtra(Constant.KEY_GROUP_ID, groupId);
                startActivity(intent);
                break;

        }
        switch (item.getItemId()){

            case R.id.videoCall:
                Intent intent = new Intent(groupChat.this, videoCall.class);
                intent.putExtra(Constant.KEY_GROUP_ID, groupId);
                startActivity(intent);
                break;

        }
        return true;
    }
}
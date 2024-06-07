package com.bypriyan.infomate.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bypriyan.infomate.Adapter.AdapterPost;
import com.bypriyan.infomate.Constant;
import com.bypriyan.infomate.Model.ModelPost;
import com.bypriyan.infomate.R;
import com.bypriyan.infomate.databinding.ActivityMainBinding;
import com.bypriyan.infomate.databinding.ActivityUserProfileBinding;
import com.bypriyan.infomate.register.LogIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserProfile extends AppCompatActivity {

    private ActivityUserProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    List<ModelPost> postList;
    AdapterPost adapterPost;
    String uid, email,downloadUrl , myName , myBranch;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        email = user.getEmail();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(email);
        setSupportActionBar(toolbar);


        uid = user.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance();
        postList = new ArrayList<>();

        databaseReference = firebaseDatabase.getReference(Constant.KEY_USERS);

        loadData();
        loadPost();
        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this, EditProfile.class);
                intent.putExtra(Constant.KEY_IMAGE, downloadUrl);
                intent.putExtra(Constant.KEY_NAME, myName);
                intent.putExtra(Constant.KEY_CATEGORY, myBranch);
                startActivity(intent);
            }
        });

    }

    private void loadPost() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(UserProfile.this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        binding.MyPostRecyclear.setLayoutManager(layoutManager);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_POSTS);
        Query query = reference.orderByChild(Constant.KEY_UID).equalTo(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                postList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){

                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    postList.add(modelPost);
                    adapterPost = new AdapterPost(UserProfile.this, postList);
                    binding.MyPostRecyclear.setAdapter(adapterPost);

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                Toast.makeText(UserProfile.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void SearchMyPost(String searchQuery) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(UserProfile.this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        binding.MyPostRecyclear.setLayoutManager(layoutManager);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_POSTS);
        Query query = reference.orderByChild(Constant.KEY_UID).equalTo(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                postList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){

                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    if(modelPost.getPostTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            modelPost.getPostDescription().toLowerCase().contains(searchQuery.toLowerCase())){

                        postList.add(modelPost);
                        
                    }

                    adapterPost = new AdapterPost(UserProfile.this, postList);
                    binding.MyPostRecyclear.setAdapter(adapterPost);

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                Toast.makeText(UserProfile.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadData() {

        Query query = databaseReference.orderByChild(Constant.KEY_EMAIL).equalTo(user.getEmail());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    String name  = ""+ds.child(Constant.KEY_NAME).getValue();
                    String email  = ""+ds.child(Constant.KEY_EMAIL).getValue();
                    String branch  = ""+ds.child(Constant.KEY_CATEGORY).getValue();
                    String image  = ""+ds.child(Constant.KEY_IMAGE).getValue();
                    downloadUrl = image;
                    myName = name;
                    myBranch = branch;

                    binding.name.setText(name);
                    binding.email.setText(email);
                    binding.branch.setText(branch);

            try{
                Glide
                        .with(UserProfile.this)
                        .load(image)
                        .centerCrop()
                        .placeholder(R.drawable.ic_person)
                        .into(binding.imageProfile);

                Glide
                        .with(UserProfile.this)
                        .load(image)
                        .centerCrop()
                        .placeholder(R.drawable.ic_image)
                        .into(binding.imageProfileBg);
                    }catch (Exception e){

            }




                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sidedotmenu, menu);
        menu.findItem(R.id.addPost).setVisible(false);
        menu.findItem(R.id.addGroup).setVisible(false);
        menu.findItem(R.id.setting).setVisible(false);
        menu.findItem(R.id.addParticipant).setVisible(false);
        menu.findItem(R.id.groupInfo).setVisible(false);
        menu.findItem(R.id.videoCall).setVisible(false);
        menu.findItem(R.id.bottomSheet).setVisible(false);


        MenuItem item = menu.findItem(R.id.search);
        item.setIcon(R.drawable.ic_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query.trim())){
                    SearchMyPost(query);
                }else{
                    loadPost();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())){
                    SearchMyPost(newText);
                }else{
                    loadPost();
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.LogOut:
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);

                builder.setMessage("Are you sure you want to LogOut")
                        .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Toast.makeText(UserProfile.this, "Signing out", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(UserProfile.this, LogIn.class));
                        finish();
                    }

                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                break;
        }

        return true;
    }

}
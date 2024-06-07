package com.bypriyan.infomate.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bypriyan.infomate.Adapter.AdapterPost;
import com.bypriyan.infomate.Constant;
import com.bypriyan.infomate.Model.ModelPost;
import com.bypriyan.infomate.R;
import com.bypriyan.infomate.databinding.ActivityThereProfileBinding;
import com.bypriyan.infomate.databinding.ActivityUserProfileBinding;
import com.bypriyan.infomate.register.LogIn;
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
import java.util.List;

public class ThereProfile extends AppCompatActivity {

    private ActivityThereProfileBinding binding;

    List<ModelPost> postList;
    AdapterPost adapterPost;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String uid, name;

    private Toolbar toolbar;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityThereProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        uid = getIntent().getStringExtra("uid");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        postList = new ArrayList<>();

        databaseReference = firebaseDatabase.getReference(Constant.KEY_USERS);

        loadHisData();

        binding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadHisPosts();

    }

    private void loadHisData() {

        Query query = databaseReference.orderByChild(Constant.KEY_UID).equalTo(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                     name  = ""+ds.child(Constant.KEY_NAME).getValue();
                    String email  = ""+ds.child(Constant.KEY_EMAIL).getValue();
                    String branch  = ""+ds.child(Constant.KEY_CATEGORY).getValue();
                    String image  = ""+ds.child(Constant.KEY_IMAGE).getValue();

                    binding.name.setText(name);
                    binding.email.setText(email);
                    binding.branch.setText(branch);

                    try{
                        Glide
                                .with(ThereProfile.this)
                                .load(image)
                                .centerCrop()
                                .placeholder(R.drawable.ic_person)
                                .into(binding.imageProfile);

                        Glide
                                .with(ThereProfile.this)
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

    private void loadHisPosts() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(ThereProfile.this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        binding.ProfilepostRecyclear.setLayoutManager(layoutManager);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_POSTS);
        Query query = reference.orderByChild(Constant.KEY_UID).equalTo(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                postList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){

                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    postList.add(modelPost);
                    adapterPost = new AdapterPost(ThereProfile.this, postList);
                    binding.ProfilepostRecyclear.setAdapter(adapterPost);

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                Toast.makeText(ThereProfile.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void searchHisPost(final String searchQuery){
        LinearLayoutManager layoutManager = new LinearLayoutManager(ThereProfile.this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        binding.ProfilepostRecyclear.setLayoutManager(layoutManager);

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

                    adapterPost = new AdapterPost(ThereProfile.this, postList);
                    binding.ProfilepostRecyclear.setAdapter(adapterPost);

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                Toast.makeText(ThereProfile.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sidedotmenu, menu);
        menu.findItem(R.id.LogOut).setVisible(false);
        menu.findItem(R.id.addPost).setVisible(false);
        menu.findItem(R.id.setting).setVisible(false);
        menu.findItem(R.id.groupInfo).setVisible(false);
        menu.findItem(R.id.addGroup).setVisible(false);
        menu.findItem(R.id.addParticipant).setVisible(false);
        menu.findItem(R.id.videoCall).setVisible(false);
        menu.findItem(R.id.bottomSheet).setVisible(false);

        MenuItem item = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query.trim())){
                    searchHisPost(query);
                }else{
                    loadHisPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())){
                    searchHisPost(newText);
                }else{
                    loadHisPosts();
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);



    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        switch (item.getItemId()){
//            case R.id.LogOut:
//                AlertDialog.Builder builder = new AlertDialog.Builder(ThereProfile.this);
//
//                builder.setMessage("Are you sure you want to LogOut")
//                        .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        Toast.makeText(ThereProfile.this) "Signing out", Toast.LENGTH_SHORT).show();
//                        FirebaseAuth.getInstance().signOut();
//                        startActivity(new Intent(getActivity(), LogIn.class));
//                        getActivity().finish();
//                    }
//
//                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.cancel();
//                    }
//                });
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//
//                break;
//        }
//        switch (item.getItemId()){
//            case R.id.setting:
//                Intent intent = new Intent(getActivity(), UserProfile.class);
//                startActivity(intent);
//                break;
//
//        }
//        switch (item.getItemId()){
//            case R.id.addPost:
//                startActivity(new Intent(getContext(), AddPostActivity.class));
//                break;
//
//        }
        return true;
    }
}
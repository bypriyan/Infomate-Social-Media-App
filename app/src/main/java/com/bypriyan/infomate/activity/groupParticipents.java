package com.bypriyan.infomate.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bypriyan.infomate.Adapter.AdapterParticipentAdd;
import com.bypriyan.infomate.Adapter.AdapterUser;
import com.bypriyan.infomate.Constant;
import com.bypriyan.infomate.Model.ModelUsers;
import com.bypriyan.infomate.R;
import com.bypriyan.infomate.databinding.ActivityChatBinding;
import com.bypriyan.infomate.databinding.ActivityGroupParticipentsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class groupParticipents extends AppCompatActivity {

    private ActivityGroupParticipentsBinding binding;
    FirebaseAuth firebaseAuth;
    String groupId, myGroupRole;
    private ArrayList<ModelUsers> usersList;
    AdapterParticipentAdd adapterParticipentAdd;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.background));
        binding = ActivityGroupParticipentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        groupId = getIntent().getStringExtra(Constant.KEY_GROUP_ID);

        loadGroupInfo();

        Log.d("okay", "onDataChange: successfully ");

    }

    private void getAllUsers() {
        usersList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelUsers modelUsers = ds.getValue(ModelUsers.class);
                    if(!firebaseAuth.getUid().equals(modelUsers.getUid())){
                        usersList.add(modelUsers);
                    }
                }
                adapterParticipentAdd = new AdapterParticipentAdd(groupParticipents.this, usersList, ""+groupId,""+myGroupRole);
                binding.usersRv.setAdapter(adapterParticipentAdd);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    private void loadGroupInfo() {

        Log.d("okay", "onDataChange: successfully ");

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.orderByChild(Constant.KEY_GROUP_ID).equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String groupIds = ""+ds.child(Constant.KEY_GROUP_ID).getValue();
                    String groupTitle = ""+ds.child(Constant.KEY_GROUP_TITLE).getValue();
                    String groupDescription = ""+ds.child(Constant.KEY_GROUP_DESCRIPTION).getValue();
                    String groupIcon = ""+ds.child(Constant.KEY_GROUP_ICON).getValue();
                    String createdBy = ""+ds.child(Constant.KEY_CREATED_BY).getValue();
                    String timeStamp = ""+ds.child(Constant.KEY_TIMESTAMP).getValue();

                    reference1.child(groupIds).child(Constant.KEY_PARTICIPENTS).child(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            myGroupRole = ""+snapshot.child(Constant.KEY_ROLE).getValue();
                                            getAllUsers();
                                            Log.d("okay", "onDataChange: successfully 3  ");

                                        }

                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void searchUser(String query) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    ModelUsers modelUsers = ds.getValue(ModelUsers.class);
                    if(!modelUsers.getUid().equals(user.getUid())){
                        if(modelUsers.getEmail().toLowerCase().contains(query.toLowerCase())){
                            usersList.add(modelUsers);
                        }

                    }

                    adapterParticipentAdd = new AdapterParticipentAdd(groupParticipents.this, usersList, ""+groupId,""+myGroupRole);
                    adapterParticipentAdd.notifyDataSetChanged();
                    binding.usersRv.setAdapter(adapterParticipentAdd);

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
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
                    searchUser(query);
                }else{
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())){
                    searchUser(newText);
                }else{
                    getAllUsers();
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }
}
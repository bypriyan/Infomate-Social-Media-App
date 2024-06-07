package com.bypriyan.infomate.ui.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bypriyan.infomate.Adapter.AdapterPost;
import com.bypriyan.infomate.Adapter.AdapterUser;
import com.bypriyan.infomate.Constant;
import com.bypriyan.infomate.MainActivity;
import com.bypriyan.infomate.Model.ModelPost;
import com.bypriyan.infomate.Model.ModelUsers;
import com.bypriyan.infomate.R;
import com.bypriyan.infomate.activity.AddPostActivity;
import com.bypriyan.infomate.activity.ContectUs;
import com.bypriyan.infomate.activity.GroupCreate;
import com.bypriyan.infomate.activity.UserProfile;
import com.bypriyan.infomate.databinding.FragmentHomeBinding;
import com.bypriyan.infomate.databinding.FragmentUsersBinding;
import com.bypriyan.infomate.register.LogIn;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    List<ModelPost> postList;
    AdapterPost adapterPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        binding.shimmer.startShimmer();

        binding.postsRecyclearView.setLayoutManager(layoutManager);

        postList = new ArrayList<>();

        loadPost();

        return binding.getRoot();
    }

    private void loadPost() {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_POSTS);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                postList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                   ModelPost modelPost = ds.getValue(ModelPost.class);

                   postList.add(modelPost);
                   adapterPost = new AdapterPost(getActivity(), postList);
                   binding.postsRecyclearView.setAdapter(adapterPost);

                }
                if(postList.size()>0){
                    binding.shimmer.stopShimmer();
                    binding.shimmer.setVisibility(View.GONE);
                    binding.postsRecyclearView.setVisibility(View.VISIBLE);
                }else {
                    binding.shimmer.startShimmer();
                    binding.shimmer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void searchPost(String searchQuery){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_POSTS);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    if(modelPost.getPostTitle().toLowerCase().contains(searchQuery.toLowerCase())
                    || modelPost.getPostDescription().toLowerCase().contains(searchQuery.toLowerCase())){
                        postList.add(modelPost);
                    }
                    adapterPost = new AdapterPost(getActivity(), postList);
                    binding.postsRecyclearView.setAdapter(adapterPost);

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sidedotmenu, menu);
        MenuItem item = menu.findItem(R.id.search);

        menu.findItem(R.id.LogOut).setVisible(false);
        menu.findItem(R.id.setting).setVisible(false);
        menu.findItem(R.id.groupInfo).setVisible(false);
        menu.findItem(R.id.addParticipant).setVisible(false);
        menu.findItem(R.id.addGroup).setVisible(false);
        menu.findItem(R.id.videoCall).setVisible(false);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query.trim())){
                    searchPost(query);
                }else{
                    loadPost();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())){
                    searchPost(newText);
                }else{
                    loadPost();
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);



    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.addGroup:
                Intent intent = new Intent(getActivity(), GroupCreate.class);
                startActivity(intent);
                break;

        }
        switch (item.getItemId()){
            case R.id.addPost:
                startActivity(new Intent(getContext(), AddPostActivity.class));
                break;

        }

        switch (item.getItemId()){
            case R.id.bottomSheet:
                showDialog();
                break;

        }
        return true;
    }

    private void showDialog() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout editLayout = dialog.findViewById(R.id.ExamAlert);
        LinearLayout shareLayout = dialog.findViewById(R.id.website);
        LinearLayout uploadLayout = dialog.findViewById(R.id.contectUs);
        LinearLayout printLayout = dialog.findViewById(R.id.privacyPolicy);
        LinearLayout terms = dialog.findViewById(R.id.termsCondition);

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Uri uri = Uri.parse("https://infomateapp.blogspot.com/p/terms-conditions.html");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));

            }
        });

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Uri uri = Uri.parse("https://www.freejobalert.com/latest-notifications/");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));

            }
        });

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Uri uri = Uri.parse("https://www.sggcg.in/");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));

            }
        });

        uploadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                startActivity(new Intent(getActivity(), ContectUs.class));

            }
        });

        printLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Uri uri = Uri.parse("https://infomateapp.blogspot.com/p/privacy-policy.html");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));

            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
}
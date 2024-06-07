package com.bypriyan.infomate.ui.chat;

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
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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

import com.bypriyan.infomate.Adapter.AdapterChatlist;
import com.bypriyan.infomate.Constant;
import com.bypriyan.infomate.MainActivity;
import com.bypriyan.infomate.Model.ModelChat;
import com.bypriyan.infomate.Model.ModelChatlist;
import com.bypriyan.infomate.Model.ModelUsers;
import com.bypriyan.infomate.R;
import com.bypriyan.infomate.activity.AddPostActivity;
import com.bypriyan.infomate.activity.ContectUs;
import com.bypriyan.infomate.activity.UserProfile;
import com.bypriyan.infomate.databinding.FragmentChatBinding;
import com.bypriyan.infomate.databinding.FragmentHomeBinding;
import com.bypriyan.infomate.register.LogIn;
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


public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    List<ModelChatlist> chatlistsList;
    List<ModelUsers> usersList;
    DatabaseReference reference;
    FirebaseUser currentUser;
    AdapterChatlist adapterChatlist;


    public ChatFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(getLayoutInflater());

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        chatlistsList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHATLIST).child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                chatlistsList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelChatlist chatlist = ds.getValue(ModelChatlist.class);
                    chatlistsList.add(chatlist);

                }
                loadChats();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        return binding.getRoot();
    }

    private void loadChats() {

        usersList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelUsers users = ds.getValue(ModelUsers.class);

                    for(ModelChatlist chatlist: chatlistsList){
                        if(users.getUid() != null && users.getUid().equals(chatlist.getId())){
                            usersList.add(users);
                            break;

                        }
                    }
                    //adapter
                    adapterChatlist = new AdapterChatlist(getContext(),usersList);
                    binding.recyclearView.setAdapter(adapterChatlist);

                    if(usersList.size()>0){
                        loadinng(false);
                    }else {
                        loadinng(true);
                    }

                    for(int i=0; i< usersList.size(); i++){
                        lastMessage(usersList.get(i).getUid());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void lastMessage(String userId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHATS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String theLastMessage = "default";
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat == null){
                        continue;
                    }
                    String sender = chat.getSender();
                    String reciver = chat.getReciver();

                    if(sender == null || reciver== null){
                        continue;
                    }

                    if(chat.getReciver().equals(currentUser.getUid()) && chat.getSender().equals(userId) ||
                    chat.getReciver().equals(userId) && chat.getSender().equals(currentUser.getUid())){

                        if(chat.getType().equals("image")){
                            theLastMessage = "send a photo";
                        }else{
                            theLastMessage = chat.getMessage();
                        }

                    }
                }

                adapterChatlist.setLastMessageMap(userId, theLastMessage);
                adapterChatlist.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sidedotmenu, menu);
        menu.findItem(R.id.groupInfo).setVisible(false);
        menu.findItem(R.id.addParticipant).setVisible(false);
        menu.findItem(R.id.addGroup).setVisible(false);
        menu.findItem(R.id.addPost).setVisible(false);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.videoCall).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.LogOut:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setMessage("Are you sure you want to LogOut")
                        .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Toast.makeText(getActivity(), "Signing out", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getActivity(), LogIn.class));
                        getActivity().finish();
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
        switch (item.getItemId()){
            case R.id.setting:
                Intent intent = new Intent(getActivity(), UserProfile.class);
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

    private void loadinng(boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.GONE);
        }
    }
}
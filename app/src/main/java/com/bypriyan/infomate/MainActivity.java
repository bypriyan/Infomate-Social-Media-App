package com.bypriyan.infomate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bypriyan.infomate.activity.UserProfile;
import com.bypriyan.infomate.databinding.ActivityMainBinding;
import com.bypriyan.infomate.notifications.Token;
import com.bypriyan.infomate.register.LogIn;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Toolbar toolbar;

    private long backPressed;
    private Toast backToast;

    private BottomNavigationView bottomNavigationView;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.background));
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navController = Navigation.findNavController(this, R.id.frame_layout);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        SharedPreferences sp= getSharedPreferences("SP_USER", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        try {
            editor.putString("Current_userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        }catch (Exception e){

        }
        editor.apply();


        getToken();

    }



    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::UpdateToken );
    }
    private void UpdateToken(String token) {

        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_TOKENS);
        Token token1 = new Token(token);
        reference.child(uid).setValue(token1);
    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.sidedotmenu, menu);
//        MenuItem menuItem = menu.findItem(R.id.LogOut);
//        View view = MenuItemCompat.getActionView(menuItem);
//
//        return true;
//    }
//
//    @SuppressLint("NonConstantResourceId")
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        switch (item.getItemId()){
//            case R.id.LogOut:
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//                builder.setMessage("Are you sure you want to LogOut")
//                        .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        Toast.makeText(MainActivity.this, "Signing out", Toast.LENGTH_SHORT).show();
//                        FirebaseAuth.getInstance().signOut();
//                        startActivity(new Intent(getApplicationContext(), LogIn.class));
//                        finish();
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
//                Intent intent = new Intent(MainActivity.this, UserProfile.class);
//                startActivity(intent);
//                break;
//
//        }
//
//        return true;
//    }

    @Override
    public void onBackPressed() {
        if(backPressed+2500 > System.currentTimeMillis()){
            super.onBackPressed();
            backToast.cancel();
            return;
        }else{
            backToast =  Toast.makeText(this, "press again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressed = System.currentTimeMillis();
    }

}
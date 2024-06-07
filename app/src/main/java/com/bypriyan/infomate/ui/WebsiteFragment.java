package com.bypriyan.infomate.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bypriyan.infomate.R;
import com.bypriyan.infomate.activity.AddPostActivity;
import com.bypriyan.infomate.activity.ContectUs;
import com.bypriyan.infomate.activity.GroupCreate;
import com.bypriyan.infomate.activity.UserProfile;
import com.bypriyan.infomate.register.LogIn;
import com.google.firebase.auth.FirebaseAuth;


public class WebsiteFragment extends Fragment {

    private WebView mywebView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_website, container, false);



        mywebView=(WebView) view.findViewById(R.id.webview);
        mywebView.setWebViewClient(new WebViewClient());
        mywebView.loadUrl("https://www.sggcg.in/");
        WebSettings webSettings=mywebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        return view;
    }

    public class mywebClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){
            super.onPageStarted(view,url,favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,String url){
            view.loadUrl(url);
            return true;
        }

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
}
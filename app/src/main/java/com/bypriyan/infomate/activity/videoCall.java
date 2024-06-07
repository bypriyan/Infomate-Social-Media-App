package com.bypriyan.infomate.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bypriyan.infomate.R;
import com.google.android.material.textfield.TextInputLayout;


import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class videoCall extends AppCompatActivity {

    private TextInputLayout CodeBox;
    private Button joinBtn, ShareBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        CodeBox = findViewById(R.id.CodeBox);
        joinBtn = findViewById(R.id.joinBtn);
        ShareBtn = findViewById(R.id.ShareBtn);

        URL serverUrl;

        try {
            serverUrl = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(serverUrl)
                    .setWelcomePageEnabled(false)
                    .build();

            JitsiMeet.setDefaultConferenceOptions(options);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        ShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CodeBox.getEditText().getText().toString().isEmpty()){
                    CodeBox.setError("Empty");
                    return;
                }else if(CodeBox.getEditText().getText().toString().length()<6){
                    CodeBox.setError("More then Six character Required");
                    CodeBox.requestFocus();
                    return;

                }else {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("EditText",CodeBox.getEditText().getText().toString());
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(videoCall.this, "Copied...", Toast.LENGTH_SHORT).show();
                }
            }
        });


        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(CodeBox.getEditText().getText().toString().isEmpty()){
                    CodeBox.setError("Empty");
                    return;
                }else if(CodeBox.getEditText().getText().toString().length()<6){
                    CodeBox.setError("More then Six character Required");
                    CodeBox.requestFocus();
                    return;

                }else {

                    JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                            .setRoom(CodeBox.getEditText().getText().toString())
                            .setWelcomePageEnabled(false)
                            .setAudioMuted(true)
                            .setVideoMuted(true)
                            .build();
                    JitsiMeetActivity.launch(videoCall.this, options);
                }
            }
        });

    }
}
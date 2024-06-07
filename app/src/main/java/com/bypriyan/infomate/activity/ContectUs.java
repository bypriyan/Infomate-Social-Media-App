package com.bypriyan.infomate.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.bypriyan.infomate.R;
import com.bypriyan.infomate.databinding.ActivityContectUsBinding;

public class ContectUs extends AppCompatActivity {

    private ActivityContectUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContectUsBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

            binding.back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(binding.msgBox.getEditText().getText().toString().isEmpty()){
                    binding.msgBox.setError("empty");
                    return;
                }else{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"+"hashset.pvt.ltd@gmail.com"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "infoMate");
                    intent.putExtra(Intent.EXTRA_TEXT, binding.msgBox.getEditText().getText().toString());
                    startActivity(intent);
                }

            }
        });
    }
}
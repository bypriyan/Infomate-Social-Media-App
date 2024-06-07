package com.bypriyan.infomate.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bypriyan.infomate.R;
import com.bypriyan.infomate.databinding.ActivityMobileNumberBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MobileNumber extends AppCompatActivity {

    private ActivityMobileNumberBinding binding;

    private TextInputLayout t1;
    private Button b1;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String PhoneNumber;
    private ProgressDialog dialog;
    private TextView signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityMobileNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        t1 = findViewById(R.id.mobile);
        b1 = findViewById(R.id.next);

        PhoneNumber = t1.getEditText().getText().toString();


        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending Otp....");
        dialog.setCancelable(false);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                if (t1.getEditText().getText().toString().isEmpty()) {
                    t1.setError("Empty");
                    dialog.dismiss();
                    return;
                } else if (t1.getEditText().getText().toString().trim().length() != 10) {
                    t1.setError("please enter valid number");
                    dialog.dismiss();
                    return;
                } else {
                    otpSend();
                }

            }
        });
    }

    private void otpSend() {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                dialog.dismiss();
                Toast.makeText(MobileNumber.this, "sucessFull", Toast.LENGTH_SHORT).show();

            }


            @Override
            public void onVerificationFailed (FirebaseException e){
                dialog.dismiss();

                Toast.makeText(MobileNumber.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent (@NonNull String verificationId,
                                    @NonNull PhoneAuthProvider.ForceResendingToken token){

                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), OtpActivity.class);
                intent.putExtra("phoneNumber", t1.getEditText().getText().toString() );
                intent.putExtra("verificationId",verificationId);
                startActivity(intent);

            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+t1.getEditText().getText().toString().trim())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(FirebaseAuth.getInstance().getCurrentUser()!= null){
//            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//        }
//    }
}
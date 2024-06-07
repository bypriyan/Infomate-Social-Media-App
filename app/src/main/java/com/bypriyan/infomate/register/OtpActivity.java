package com.bypriyan.infomate.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bypriyan.infomate.R;
import com.bypriyan.infomate.databinding.ActivityOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private ActivityOtpBinding binding;
    private EditText t2;
    private TextView num;
    private Button b2;

    private String PhoneNumber, otpId;
    private String verificationId;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        firebaseAuth = FirebaseAuth.getInstance();
        verificationId = getIntent().getStringExtra("verificationId").toString();
        PhoneNumber = getIntent().getStringExtra("phoneNumber");
        num = findViewById(R.id.phoneLbl);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Verifying OTP....");
        dialog.setCancelable(false);


        t2 = findViewById(R.id.otp);
        b2 = findViewById(R.id.submitBtn);

        num.setText("Verify "+"+91 "+PhoneNumber);


        initiateOtp();


        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();

                if(t2.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Blank Field Cannot Be Processed", Toast.LENGTH_SHORT).show();
                }else if(t2.getText().toString().length()!= 6){
                    t2.setError("Invalid OTP");
                }else{
                    if(verificationId != null) {
                        String code = t2.getText().toString();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    dialog.dismiss();
                                    Intent intent = new Intent(OtpActivity.this, SignUp.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("MobNum",PhoneNumber);
                                    startActivity(intent);
                                    finish();

                                }else{
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

            }
        });
    }

    private void initiateOtp() {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(PhoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                otpId = s;

                            }

                            @Override
                            public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential phoneAuthCredential) {

                                signInWithPhoneAuthCredential(phoneAuthCredential);

                            }

                            @Override
                            public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
                                Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //signIn

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(OtpActivity.this,SignUp.class );
                            intent.putExtra("MobNum", PhoneNumber);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(OtpActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
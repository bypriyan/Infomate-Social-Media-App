package com.bypriyan.infomate.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bypriyan.infomate.MainActivity;
import com.bypriyan.infomate.R;
import com.bypriyan.infomate.databinding.ActivityLogInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class LogIn extends AppCompatActivity {

    private ActivityLogInBinding binding;
    private FirebaseAuth auth;
    boolean b = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogIn.this, MobileNumber.class ));
            }
        });

            b = binding.checkBox.isChecked();

            binding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        binding.login.setVisibility(View.VISIBLE);
                    }else{
                        binding.login.setVisibility(View.INVISIBLE);
                    }
                }
            });

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading(true);
                if(isValidation()){
                    loginUser(binding.email.getEditText().getText().toString().trim()
                            , binding.password.getEditText().getText().toString());
                }else{
                    loading(false);
                }
            }
        });

        binding.recoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoverDialog();
            }
        });

        binding.terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://infomateapp.blogspot.com/p/terms-conditions.html");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });

        binding.privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://infomateapp.blogspot.com/p/privacy-policy.html");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });

        binding.dataPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://infomateapp.blogspot.com/p/infomatedata-policy-type-of-information.html");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });

        binding.termsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://infomateapp.blogspot.com/p/terms-of-use.html");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });

    }

    private void showRecoverDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        LinearLayout layout = new LinearLayout(this);
        final EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setMinEms(16);
        layout.addView(emailEt);
        layout.setPadding(10, 10, 10, 10);

        builder.setView(layout);
        builder.setCancelable(false);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String email = emailEt.getText().toString().trim();
                if(!email.isEmpty()){
                    beingRecovery(email);
                }else{
                    emailEt.setError("Empty");
                }

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();

    }

    private void beingRecovery(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                Toast.makeText(LogIn.this, "Email Send Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(LogIn.this, "Unable to send email"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String email, String password) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    loading(false);
                    startActivity(new Intent(LogIn.this, MainActivity.class));

                }else{
                    loading(false);
                    Toast.makeText(LogIn.this, "Unable To login", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(LogIn.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus(){
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(LogIn.this, MainActivity.class));
            finish();
        }
    }

    private void loading(boolean isloading){
        if(isloading){
            binding.login.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.login.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);
        }

    }
    private boolean isValidation(){
        if(binding.email.getEditText().getText().toString().isEmpty()){
            binding.email.setError("Empty");
            binding.email.requestFocus();
            return false;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(binding.email.getEditText().getText().toString()).matches()){
            binding.email.setError("Enter valid email");
            binding.email.requestFocus();
            return false;

        } else if(binding.password.getEditText().getText().toString().isEmpty()) {
            binding.password.setError("Empty");
            binding.password.requestFocus();
            return false;

        }else {
            return true;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }
}
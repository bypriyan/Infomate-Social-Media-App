package com.bypriyan.infomate.register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.bypriyan.infomate.Constant;
import com.bypriyan.infomate.MainActivity;
import com.bypriyan.infomate.R;
import com.bypriyan.infomate.activity.GroupEdit;
import com.bypriyan.infomate.databinding.ActivitySignUpBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    private String Category,downloadurl="";
    private final int REQ = 1;
    private Bitmap bitmap = null;
    private String number;
    private ProgressDialog pd;
    private StorageReference storageReference;
    boolean b = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        number = getIntent().getStringExtra("MobNum");

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);

        b = binding.checkBox.isChecked();

        binding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.registerBtn.setVisibility(View.VISIBLE);
                }else{
                    binding.registerBtn.setVisibility(View.INVISIBLE);
                }
            }
        });
        Category = binding.branch.getEditText().getText().toString().trim();

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading(true);
                if (isValidation()) {
                    registerUser(binding.email.getEditText().getText().toString().trim(),
                            binding.password.getEditText().getText().toString().trim());
                } else {
                    loading(false);
                }
            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
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

    private void registerUser(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    loading(true);
                    uploadImg();
//                    addData();
//                    Toast.makeText(SignUp.this, "profile created successfully", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(SignUp.this, MainActivity.class));
//                    finish();
                } else {
                    loading(false);
                    Toast.makeText(SignUp.this, "Unable to create account", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private boolean isValidation() {

        if(bitmap == null){
            Toast.makeText(this, "Please select Profile Image", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(binding.name.getEditText().getText().toString().isEmpty()){
            binding.name.setError("Empty");
            binding.name.requestFocus();
            return false;
        } else if (binding.email.getEditText().getText().toString().isEmpty()) {
            binding.email.setError("Empty");
            binding.email.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getEditText().getText().toString()).matches()) {
            binding.email.setError("Enter valid email");
            binding.email.requestFocus();
            return false;

        } else if(binding.branch.getEditText().getText().toString().isEmpty()){
            binding.branch.setError("Empty");
             Toast.makeText(this, "Select Branch", Toast.LENGTH_SHORT).show();
             return false;
         }else if (binding.password.getEditText().getText().toString().isEmpty()) {
            binding.password.setError("Empty");
            binding.password.requestFocus();
            return false;
        } else if (binding.password.getEditText().getText().toString().length() < 6) {
            binding.password.setError("Enter at least 6 character ");
            binding.password.requestFocus();
            return false;
        } else if (binding.confirmPassword.getEditText().getText().toString().isEmpty()) {
            binding.confirmPassword.setError("Empty");
            binding.confirmPassword.requestFocus();
            return false;
        } else if (!binding.password.getEditText().getText().toString().equals(
                binding.confirmPassword.getEditText().getText().toString())) {
            binding.confirmPassword.setError("Check password");
            binding.confirmPassword.requestFocus();
            return false;
        } else {
            return true;
        }
    }

private void uploadImg() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImg = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("Group_Imgs/").child(finalImg +"jpeg");
        final UploadTask uploadTask = filePath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(SignUp.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadurl = String.valueOf(uri);
                                    addData();

                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(SignUp.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void loading(boolean isloading) {
        if (isloading) {
            binding.registerBtn.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        } else {
            binding.registerBtn.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);
        }

    }

    public void addData(){
        FirebaseUser user = mAuth.getCurrentUser();

        String email = user.getEmail();
        String uid = user.getUid();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_PHONE, number);
        hashMap.put(Constant.KEY_EMAIL, email);
        hashMap.put(Constant.KEY_UID, uid);
        hashMap.put(Constant.KEY_TYPING_STAUS, "noOne");
        hashMap.put(Constant.KEY_NAME, binding.name.getEditText().getText().toString());
        hashMap.put(Constant.KEY_CATEGORY, binding.branch.getEditText().getText().toString());
        hashMap.put(Constant.KEY_IMAGE,downloadurl);
        hashMap.put(Constant.KEY_ONLINE_STAUS,"online");
        hashMap.put(Constant.KEY_PASSWORD, binding.password.getEditText().getText().toString());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(uid)
                .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(SignUp.this, "Account created Successfully", Toast.LENGTH_SHORT).show();
                loading(false);
                startActivity(new Intent(SignUp.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(SignUp.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ && resultCode == RESULT_OK){
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            binding.profileImage.setImageBitmap(bitmap);
        }

    }

    @Override
    protected void onStart() {
        FirebaseAuth.getInstance().signOut();
        super.onStart();
    }
}
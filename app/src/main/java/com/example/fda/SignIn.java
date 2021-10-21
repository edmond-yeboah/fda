package com.example.fda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.regex.Pattern;

public class SignIn extends AppCompatActivity {
    private TextInputLayout mEmail, mPassword;
    private Button btnlogin;
    private boolean isvalidemail = false, isvalidpassword = false;
    private FirebaseAuth mAuth;
    private AVLoadingIndicatorView avi;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Context context = getApplicationContext();

        mAuth = FirebaseAuth.getInstance();
        avi = findViewById(R.id.avilogin);

        imageView = findViewById(R.id.fullscreen);

        mEmail = (TextInputLayout) findViewById(R.id.lemail);
        mPassword = (TextInputLayout) findViewById(R.id.lpassword);
        btnlogin = (Button) findViewById(R.id.signin);
        btnlogin.setEnabled(false);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //login here
                StartAnim();
                mAuth.signInWithEmailAndPassword(mEmail.getEditText().getText().toString(), mPassword.getEditText().getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                //if successful
                                Bundle bundle = new Bundle();
                                bundle.putString("email", mEmail.getEditText().getText().toString());

                                //check camera permission
                                Dexter.withContext(context).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                        //go to dashboard
                                        StopAnim();
                                        Intent intent = new Intent(SignIn.this, MainActivity.class);
                                        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(SignIn.this, imageView, ViewCompat.getTransitionName(imageView));
                                        startActivity(intent, compat.toBundle());
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                        StopAnim();
                                        Snackbar snackbar = Snackbar.make(view, "Camera access is required to read QR code", Snackbar.LENGTH_LONG);
                                        snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                                        snackbar.show();
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                        StopAnim();
                                        permissionToken.continuePermissionRequest();
                                    }
                                }).check();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //if failed
                                StopAnim();
                                Snackbar snackbar = Snackbar.make(view, "Error: Login failed", Snackbar.LENGTH_LONG);
                                snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                                snackbar.show();
                            }
                        });

            }
        });

        //setting textwatchers for edittext
        mEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateEmail(editable);
            }
        });

        mEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    validateEmail(((EditText) view).getText());
                }
            }
        });

        mPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validatepassword(s);
            }
        });

        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    validatepassword(((EditText) view).getText());
                }
            }
        });
    }

    private void StartAnim() {
        avi.show();
    }

    private void StopAnim() {
        avi.hide();
    }

    private void validatepassword(Editable text) {
        if (TextUtils.isEmpty(text)) {
            isvalidpassword = false;
            mPassword.setError("");
            btnlogin.setEnabled(false);
        } else {
            mPassword.setError(null);
            isvalidpassword = true;
            if (isvalidpassword & isvalidemail) {
                btnlogin.setEnabled(true);
            }
        }
    }

    private void validateEmail(Editable text) {
        if (TextUtils.isEmpty(text)) {
            isvalidemail = false;
            mEmail.setError("");
            btnlogin.setEnabled(false);
        } else {
            mEmail.setError(null);
            isvalidemail = true;
            if (isvalidemail & isvalidpassword) {
                btnlogin.setEnabled(true);
            }
        }
    }

    public void backtowelcome(View view) {
        Intent intent = new Intent(SignIn.this, Welcome.class);
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(SignIn.this, imageView, ViewCompat.getTransitionName(imageView));
        startActivity(intent, compat.toBundle());
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }
}
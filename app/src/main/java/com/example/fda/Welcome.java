package com.example.fda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class Welcome extends AppCompatActivity {
    private Button login, register;
    private FirebaseAuth mAuth;
    private Context context;
    private View v;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        FirebaseApp.initializeApp(this);

        imageView = findViewById(R.id.trans);
        context = getApplicationContext();
        v = findViewById(R.id.newview);

        login = findViewById(R.id.login);
        mAuth = FirebaseAuth.getInstance();
        register = findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goto login activity
                Intent intent = new Intent(Welcome.this, SignIn.class);
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(Welcome.this, imageView, ViewCompat.getTransitionName(imageView));
                startActivity(intent, compat.toBundle());
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goto register activity
                Intent intent = new Intent(Welcome.this, Register.class);
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(Welcome.this, imageView, ViewCompat.getTransitionName(imageView));
                startActivity(intent, compat.toBundle());
            }
        });
    }

    @Override
    public void onBackPressed() {
        //donothing
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        if (currentuser != null) {

            //check camera permission
            Dexter.withContext(context).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                    Intent intent = new Intent(Welcome.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_through_right);
                    finish();
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                    Snackbar snackbar = Snackbar.make(v, "Camera access is required to read QR code", Snackbar.LENGTH_LONG);
                    snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                    snackbar.show();

                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();

                }
            }).check();


        }
    }
}
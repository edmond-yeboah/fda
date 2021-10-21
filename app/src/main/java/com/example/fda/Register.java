package com.example.fda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Fade;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

public class Register extends AppCompatActivity {
    private TextInputLayout fname, lname, email, password, cpassword;
    private Button btnregister;
    private FirebaseAuth mAuth;
    private AVLoadingIndicatorView avi;
    private ImageView imageView;
    private UserClass userClass;
    private DatabaseReference reference;
    private boolean isvalidEmail = false, isvalidPassword = false, ispasswordmatch = false, isvalidfname = false, isvalidlname = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        imageView = findViewById(R.id.full);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("user");
        avi = (AVLoadingIndicatorView) findViewById(R.id.aviregister);

        btnregister = (Button) findViewById(R.id.createaccount);
        fname = (TextInputLayout) findViewById(R.id.fname);
        lname = (TextInputLayout) findViewById(R.id.lname);
        email = (TextInputLayout) findViewById(R.id.lemail);
        password = (TextInputLayout) findViewById(R.id.lpassword);
        cpassword = (TextInputLayout) findViewById(R.id.cpassword);

        btnregister.setEnabled(false);

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //starting animation
                StartAnim();
                //creating account and saving info here
                //checking if email already exist
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (ds.child("email").getValue().equals(email.getEditText().getText().toString())) {
                                StopAnim();
                                Snackbar snackbar = Snackbar.make(view, "There is an account associated with this email", Snackbar.LENGTH_LONG);
                                snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                                snackbar.show();
                            } else {
                                //creating account with email and password
                                mAuth.createUserWithEmailAndPassword(email.getEditText().getText().toString(), cpassword.getEditText().getText().toString())
                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                //saving user info in the database
                                                saveuserinfo(view);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                StopAnim();
                                                Snackbar snackbar = Snackbar.make(view, "Error: " + e, Snackbar.LENGTH_LONG);
                                                snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                                                snackbar.show();
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        StopAnim();

                    }
                });
            }
        });

        //checking fname validation
        fname.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validatefname(editable);

            }
        });

        fname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    validatefname(((EditText) view).getText());
                }
            }
        });


        //checking lname validation
        lname.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validatelname(editable);
            }
        });

        lname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    validatelname(((EditText) view).getText());
                }
            }
        });


        //checking email validation
        email.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateemail(editable);
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    validateemail(((EditText) view).getText());
                }
            }
        });


        //checking password validation
        password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validatepassword(editable);
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    validatepassword(((EditText) view).getText());
                }
            }
        });

        //checking confirm password validation
        cpassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validatecpassword(editable);
            }
        });

        cpassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    validatecpassword(((EditText) view).getText());
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

    private void saveuserinfo(View view) {
        String uid = reference.push().getKey();
        String firstname = fname.getEditText().getText().toString();
        String lastname = lname.getEditText().getText().toString();
        String Email = email.getEditText().getText().toString();
        String Password = password.getEditText().getText().toString();

        userClass = new UserClass(firstname, lastname, Email, Password, uid);

        reference.child(uid).setValue(userClass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //goto dashboard
                            StopAnim();
                            Intent intent = new Intent(Register.this, getstarted.class);
                            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(Register.this, imageView, ViewCompat.getTransitionName(imageView));
                            startActivity(intent, compat.toBundle());
                        } else {
                            StopAnim();
                            //show error message
                            Snackbar snackbar = Snackbar.make(view, "Error! Please try again later", Snackbar.LENGTH_LONG);
                            snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                            snackbar.show();
                        }
                    }
                });
    }

    private void validatecpassword(Editable editable) {
        if (!TextUtils.isEmpty(editable) & editable.toString().length() >= 6 & editable.toString().equals(password.getEditText().getText().toString())) {
            ispasswordmatch = true;
            cpassword.setError(null);
            if (isvalidPassword & isvalidEmail & ispasswordmatch & isvalidfname & isvalidlname) {
                btnregister.setEnabled(true);
            } else {
                btnregister.setEnabled(false);
            }
        } else {
            cpassword.setError("passwords do not match");
            btnregister.setEnabled(false);
        }
    }

    private void validatepassword(Editable editable) {
        if (!TextUtils.isEmpty(editable) & editable.toString().length() >= 6) {
            isvalidPassword = true;
            password.setError(null);
            if (isvalidPassword & isvalidEmail & ispasswordmatch & isvalidfname & isvalidlname) {
                btnregister.setEnabled(true);
            } else {
                btnregister.setEnabled(false);
            }
        } else {
            password.setError("too short");
            btnregister.setEnabled(false);
        }

        if (!TextUtils.isEmpty(cpassword.getEditText().getText().toString())) {
            if (editable.toString().equals(cpassword.getEditText().getText().toString())) {
                ispasswordmatch = true;
                cpassword.setError(null);
                if (isvalidPassword & isvalidEmail & ispasswordmatch & isvalidfname & isvalidlname) {
                    btnregister.setEnabled(true);
                } else {
                    btnregister.setEnabled(false);
                }
            } else {
                cpassword.setError("passwords do not match");
                btnregister.setEnabled(false);
            }
        }
    }

    private void validateemail(Editable editable) {
        if (!TextUtils.isEmpty(editable)) {
            if (!editable.toString().contains("@")) {
                email.setError("Invalid email");
                btnregister.setEnabled(false);
            } else {
                isvalidEmail = true;
                email.setError(null);
                if (isvalidPassword & isvalidEmail & ispasswordmatch & isvalidfname & isvalidlname) {
                    btnregister.setEnabled(true);
                } else {
                    btnregister.setEnabled(false);
                }
            }
        } else {
            email.setError("Invalid email");
            btnregister.setEnabled(false);
        }

    }

    private void validatelname(Editable editable) {
        if (!TextUtils.isEmpty(editable)) {
            isvalidlname = true;
            lname.setError(null);
            if (isvalidPassword & isvalidEmail & ispasswordmatch & isvalidfname & isvalidlname) {
                btnregister.setEnabled(true);
            } else {
                btnregister.setEnabled(false);
            }
        } else {
            isvalidlname = false;
            lname.setError("Invalid lname");
            if (isvalidPassword & isvalidEmail & ispasswordmatch & isvalidfname & isvalidlname) {
                btnregister.setEnabled(true);
            } else {
                btnregister.setEnabled(false);
            }
        }
    }

    private void validatefname(Editable editable) {
        if (!TextUtils.isEmpty(editable)) {
            isvalidfname = true;
            fname.setError(null);
            if (isvalidPassword & isvalidEmail & ispasswordmatch & isvalidfname & isvalidlname) {
                btnregister.setEnabled(true);
            } else {
                btnregister.setEnabled(false);
            }
        } else {
            isvalidfname = false;
            fname.setError("Invalid fname");
            if (isvalidPassword & isvalidEmail & ispasswordmatch & isvalidfname & isvalidlname) {
                btnregister.setEnabled(true);
            } else {
                btnregister.setEnabled(false);
            }
        }
    }

    public void backtowelcome(View view) {
        StopAnim();
        Intent intent = new Intent(Register.this, Welcome.class);
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(Register.this, imageView, ViewCompat.getTransitionName(imageView));
        startActivity(intent, compat.toBundle());
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }
}
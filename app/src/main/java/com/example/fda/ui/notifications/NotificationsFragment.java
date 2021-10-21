package com.example.fda.ui.notifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.fda.R;
import com.example.fda.UserClass;
import com.example.fda.Welcome;
import com.example.fda.databinding.FragmentNotificationsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Objects;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private Button logout;
    private TextView fname, lname, email;
    private FirebaseAuth mAuth;
    private String Email;
    private UserClass userClass;
    private ImageView imageView;
    private AVLoadingIndicatorView avi;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Fade fade = new Fade();
        View decor = getActivity().getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        mAuth = FirebaseAuth.getInstance();
        fname = root.findViewById(R.id.ufname);
        lname = root.findViewById(R.id.ulname);
        email = root.findViewById(R.id.uemail);
        logout = root.findViewById(R.id.logout);
        avi = root.findViewById(R.id.aviprofile);
        imageView = root.findViewById(R.id.profilelogo);

        FirebaseUser currentuser = mAuth.getCurrentUser();
        if (currentuser != null) {
            Email = currentuser.getEmail();
        }

        //getting user info from the database
        Query query = FirebaseDatabase.getInstance().getReference("user").orderByChild("email").equalTo(Email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Startanim();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    userClass = ds.getValue(UserClass.class);
                }

                //showing the user info
                if (userClass != null) {
                    fname.setText(userClass.getFirstname());
                    lname.setText(userClass.getLastname());
                    email.setText(userClass.getEmail());
                    Stopanim();
                } else {
                    //do nothing
                    Stopanim();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Stopanim();

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();

                Intent intent = new Intent(getActivity(), Welcome.class);
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), imageView, ViewCompat.getTransitionName(imageView));
                startActivity(intent, compat.toBundle());

            }
        });

        return root;
    }

    private void Stopanim() {
        avi.hide();
    }

    private void Startanim() {
        avi.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
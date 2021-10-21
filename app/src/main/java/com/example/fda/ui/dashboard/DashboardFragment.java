package com.example.fda.ui.dashboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.fda.ProductClass;
import com.example.fda.R;
import com.example.fda.databinding.FragmentDashboardBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private CodeScanner mCodeScanner;
    private ProductClass productClass;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final Activity activity = getActivity();
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //check if product is in database
                        chekcproduct(result);
                    }
                });
            }
        });


        return root;
    }

    private void chekcproduct(Result result) {
        Query query = FirebaseDatabase.getInstance().getReference("product").orderByChild("id").equalTo(result.getText());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    productClass = ds.getValue(ProductClass.class);
                }
                if (productClass != null) {
                    //product is genuine
                    BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(getActivity())
                            .setTitle(productClass.getName())
                            .setMessage(productClass.getName() + " is rich in " + productClass.getNutrients() + ". \n " +
                                    productClass.getName() + " is an " + productClass.getOrigin() + " product and has "
                                    + productClass.getCalories() + " calories")
                            .setAnimation(R.raw.good)
                            .setCancelable(false)
                            .setNegativeButton("Dismiss", R.drawable.ic_close, new BottomSheetMaterialDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    //Toast.makeText(activity.getApplicationContext(), "Cancelled!", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                }
                            })
                            .build();

                    // Show Dialog
                    mBottomSheetDialog.show();

                } else {
                    //product is fake
                    BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(getActivity())
                            .setTitle("Fake!")
                            .setMessage("Are you sure want to delete this file?")
                            .setAnimation(R.raw.error)
                            .setCancelable(false)
                            .setNegativeButton("Dismiss", R.drawable.ic_close, new BottomSheetMaterialDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    //Toast.makeText(activity.getApplicationContext(), "Cancelled!", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                }
                            })
                            .build();

                    // Show Dialog
                    mBottomSheetDialog.show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //do nothing

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}
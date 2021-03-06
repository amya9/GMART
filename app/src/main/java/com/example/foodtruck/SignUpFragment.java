package com.example.foodtruck;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpFragment extends Fragment {

    private ImageButton closeButton;
    private ProgressBar progressBar;

    private TextView haveAccount;
    private FrameLayout parentFrameLayout;
    private EditText signUpEmail, signUpName, signUpPassword, signUpConfirmPassword;
    private Button signUpButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    public static boolean disableCloseBtn = false;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";


    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        haveAccount = view.findViewById(R.id.already_have_account_tv);
        parentFrameLayout = getActivity().findViewById(R.id.register_frameLayout);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        signUpButton = view.findViewById(R.id.signUp_btn);
        signUpEmail = view.findViewById(R.id.signUp_email);
        signUpName = view.findViewById(R.id.signUp_fullName);
        signUpPassword = view.findViewById(R.id.signUp_password);
        signUpConfirmPassword = view.findViewById(R.id.signUp_confirm_password);

        closeButton = view.findViewById(R.id.close_signup_btn);

        progressBar = view.findViewById(R.id.signUp_progressBar);
        ////////using this cross btn we can go to app without signing in or up
        if (disableCloseBtn) {
            closeButton.setVisibility(View.GONE);
        } else {
            closeButton.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });

        signUpEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        signUpName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        signUpPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        signUpConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndConfirmPassword();
            }
        });

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }


    private void checkInputs() {
        if (!TextUtils.isEmpty(signUpEmail.getText())) {
            if (!TextUtils.isEmpty(signUpName.getText())) {
                if (!TextUtils.isEmpty(signUpPassword.getText()) && signUpPassword.length() > 7) {
                    if (!TextUtils.isEmpty(signUpConfirmPassword.getText())) {

                        signUpButton.setEnabled(true);
                        signUpButton.setTextColor(Color.parseColor("#000000"));
                    } else {
                        signUpButton.setEnabled(false);
                        signUpButton.setTextColor(Color.parseColor("#50000000"));
                    }

                } else {
                    signUpButton.setEnabled(false);
                    signUpButton.setTextColor(Color.parseColor("#50000000"));
                }

            } else {
                signUpButton.setEnabled(false);
                signUpButton.setTextColor(Color.parseColor("#50000000"));
            }

        } else {
            signUpButton.setEnabled(false);
            signUpButton.setTextColor(Color.parseColor("#50000000"));
        }
    }


    private void checkEmailAndConfirmPassword() {
        Drawable customErrorIcon = getResources().getDrawable(R.mipmap.custom_error_icon);
        customErrorIcon.setBounds(0, 0, customErrorIcon.getIntrinsicWidth(), customErrorIcon.getMinimumHeight());


        if (signUpEmail.getText().toString().matches(emailPattern)) {
            if (signUpPassword.getText().toString().equals(signUpConfirmPassword.getText().toString())) {

                progressBar.setVisibility(View.VISIBLE);
                signUpButton.setEnabled(false);
                signUpButton.setTextColor(Color.parseColor("#50000000"));

                firebaseAuth.createUserWithEmailAndPassword(signUpEmail.getText().toString(), signUpPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Map<String, Object> userData = new HashMap<>();
                            userData.put("FullName", signUpName.getText().toString());

                            firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                    .set(userData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                CollectionReference collectionReference = firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA");
                                                ////////////Maps
                                                Map<String, Object> wishListMap = new HashMap<>();
                                                wishListMap.put("list_size", (long) 0);

                                                Map<String, Object> ratingsMap = new HashMap<>();
                                                ratingsMap.put("list_size", (long) 0);

                                                Map<String, Object> cartMap = new HashMap<>();
                                                cartMap.put("list_size", (long) 0);

                                                Map<String, Object> myAddresses = new HashMap<>();
                                                myAddresses.put("list_size", (long) 0);

                                                ////////////Maps

                                                final List<String> documentNames = new ArrayList<>();
                                                documentNames.add("MY_WISHLIST");
                                                documentNames.add("MY_RATINGS");
                                                documentNames.add("MY_CART");
                                                documentNames.add("MY_ADDRESSES");

                                                List<Map<String, Object>> documentsFields = new ArrayList<>();
                                                documentsFields.add(wishListMap);
                                                documentsFields.add(ratingsMap);
                                                documentsFields.add(cartMap);
                                                documentsFields.add(myAddresses);

                                                for (int x = 0; x < documentNames.size(); x++) {
                                                    final int finalX = x;
                                                    collectionReference.document(documentNames.get(x))
                                                            .set(documentsFields.get(x))
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        if (finalX == documentNames.size() - 1) {
                                                                            mainIntent();
                                                                        }
                                                                    } else {
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                        signUpButton.setEnabled(true);
                                                                        signUpButton.setTextColor(Color.parseColor("#000000"));
                                                                        String error = task.getException().getMessage();
                                                                        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });
                                                }

                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            signUpButton.setEnabled(true);
                            signUpButton.setTextColor(Color.parseColor("#000000"));
                            String error = task.getException().getMessage();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();

                        }
                    }
                });
            } else {

                signUpConfirmPassword.setError("password doesn't match", customErrorIcon);
            }
        } else {
            signUpEmail.setError("Invalid Email", customErrorIcon);

        }

    }

    private void mainIntent() {
        if (disableCloseBtn) {
            disableCloseBtn = false;
        } else {
            Intent mainActivityIntent = new Intent(getActivity(), MainActivity.class);
            startActivity(mainActivityIntent);
        }
        getActivity().finish();
    }
}
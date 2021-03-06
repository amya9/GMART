package com.example.foodtruck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

public class RegisterActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    public static boolean onFragmentChange = false;
    public static boolean signUpFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        frameLayout = findViewById(R.id.register_frameLayout);

        if (signUpFragment) {
            signUpFragment = false;
            setMainFragment(new SignUpFragment());
        } else {
            setMainFragment(new SignInFragment());
        }
    }

    ////////this code runs when user get out from app by pressing back button and again enter in app then he is redirected to signin fragment
    //////// also here between fragment slide animation is attached
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK){
            if (onFragmentChange){
                onFragmentChange = false;
                setFragment(new SignInFragment());
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void setMainFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId() , fragment);
        fragmentTransaction.commit();
    }
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left , R.anim.slideout_from_right);
        fragmentTransaction.replace(frameLayout.getId() , fragment);
        fragmentTransaction.commit();
    }
}
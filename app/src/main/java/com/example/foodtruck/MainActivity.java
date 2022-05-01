package com.example.foodtruck;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.foodtruck.firebase.FirebaseQueries;
import com.example.foodtruck.ui.home.HomeFragment;
import com.example.foodtruck.ui.myAccount.MyAccountFragment;
import com.example.foodtruck.ui.myCart.MyCartFragment;
import com.example.foodtruck.ui.myOrder.MyOrderFragment;
import com.example.foodtruck.ui.myReward.MyRewardFragment;
import com.example.foodtruck.ui.myWishlist.MyWishlistFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.foodtruck.RegisterActivity.signUpFragment;
// top right side ke 3 items tune menu->main.xml me set kiya h (bell,notification,cart ke icons ).........

// nav_header_main.xml me tune place holder ka icon set kiya h, jab top left ke 3 dots wale icon ko click karega
// tab jo navigation bar khulta h uski baat ho rhi h yaha.........


// menu->activity_main_drawer.xml me jo left side ke navbar ke neeche dikhte h wo tune waha pr daale h,
// jaise amazon ke app jab left navbar kholte h to jo options dikhte h neeche wo waha pr h...........

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FrameLayout frameLayout;
    private NavigationView navigationView;
    private ImageView actionBarLogo;
    private static final int HOME_FRAGMENT = 0;
    private static final int CART_FRAGMENT = 1;
    private static final int ORDERS_FRAGMENT = 2;
    private static final int WISHLIST_FRAGMENT = 3;
    private static final int REWARD_FRAGMENT = 4;
    private static final int ACCOUNT_FRAGMENT = 5;
    private static final int NOTIFICATION_FRAGMENT = 6;

    public static Activity mainActivity;
    public static Boolean showCart = false;
    private Dialog signInDialog;

    public static DrawerLayout drawer;
    public static int currentFragment = -1;
    private Window window;
    private Toolbar toolbar;
    private FirebaseUser currentUsers;

    private TextView badgeCount;
    private AppBarLayout.LayoutParams params;
    private int scrollFlags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);

        actionBarLogo = findViewById(R.id.action_bar_logo);
        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        scrollFlags = params.getScrollFlags();

        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
        navigationView.bringToFront();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.main_nav_home, R.id.main_nav_my_order, R.id.main_nav_my_reward,
                R.id.main_nav_my_cart, R.id.main_nav_my_wishList, R.id.main_nav_my_account)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.getMenu().getItem(0).setChecked(true);

        frameLayout = findViewById(R.id.main_frameLayout);
        if (showCart) {
            mainActivity = this;
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            gotoFragment("My Cart", new MyCartFragment(), -2);
        } else {
//            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//            drawer.addDrawerListener(toggle);
//            toggle.syncState();
            setFragments(new HomeFragment(), HOME_FRAGMENT);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                if (currentUsers != null) {

                            int id = item.getItemId();
                            item.setChecked(true);
                            if (id == R.id.main_nav_home) {
                                actionBarLogo.setVisibility(View.VISIBLE);
                                invalidateOptionsMenu();
                                setFragments(new HomeFragment(), HOME_FRAGMENT);
                            } else if (id == R.id.main_nav_my_order) {
                                gotoFragment("My Order", new MyOrderFragment(), ORDERS_FRAGMENT);
                            } else if (id == R.id.main_nav_my_reward) {
                                gotoFragment("My Reward", new MyRewardFragment(), REWARD_FRAGMENT);
                            } else if (id == R.id.main_nav_my_cart) {
                                gotoFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
                            } else if (id == R.id.main_nav_my_wishList) {
                                gotoFragment("My WishList", new MyWishlistFragment(), WISHLIST_FRAGMENT);
                            } else if (id == R.id.main_nav_my_account) {
                                gotoFragment("My Account", new MyAccountFragment(), ACCOUNT_FRAGMENT);
                            } else if (id == R.id.main_nav_signOut) {
                                new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                                        .setTitle("Sign out ")
                                        .setMessage("Are you sure, you want to sign out?")
                                        .setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
//                                        loadingDialog.show();
                                                FirebaseAuth.getInstance().signOut();
                                                FirebaseQueries.clearListData();
                                                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                                                startActivity(registerIntent);
                                                finish();
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                    return true;
                } else {
                    signInDialog.show();
                    return false;
                }

            }

        });


        signInDialog = new Dialog(MainActivity.this);
        signInDialog.setCancelable(true);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        Button signInBtn = signInDialog.findViewById(R.id.dialog_sign_in_btn);
        Button signUpBtn = signInDialog.findViewById(R.id.dialog_sign_up_btn);

        final Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInDialog.dismiss();
                SignInFragment.disableCloseBtn = true;
                SignUpFragment.disableCloseBtn = true;
                signUpFragment = false;
                startActivity(registerIntent);
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInDialog.dismiss();
                SignInFragment.disableCloseBtn = true;
                SignUpFragment.disableCloseBtn = true;
                signUpFragment = true;
                startActivity(registerIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseQueries.clearListData();
        currentUsers = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUsers == null) {
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(false);
        } else {
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(true);
        }
        invalidateOptionsMenu();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (currentFragment == HOME_FRAGMENT) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.main, menu);

            //here setting badge layout on cart menu to show number of item in cart

            MenuItem badgeMenu = menu.findItem(R.id.main_action_cart);
            badgeMenu.setActionView(R.layout.badge_layout);
            ImageView badgeIcon = badgeMenu.getActionView().findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.mipmap.cart_white);
            badgeCount = badgeMenu.getActionView().findViewById(R.id.badge_text);
            if (currentUsers != null) {
                if (FirebaseQueries.cartList.size() == 0) {
                    FirebaseQueries.loadCartItem(MainActivity.this, new Dialog(MainActivity.this), false, badgeCount , new TextView(MainActivity.this));
                } else {
                    badgeCount.setVisibility(View.VISIBLE);
                    if (FirebaseQueries.cartList.size() < 99) {
                        badgeCount.setText(String.valueOf(FirebaseQueries.cartList.size()));
                    } else {
                        badgeCount.setText("99");
                    }
                }
            }

            badgeMenu.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUsers == null) {
                        signInDialog.show();
                    } else {
                        gotoFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
                    }
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_action_notification) {
            //todo
            return true;
        } else if (id == R.id.main_action_search) {
            //todo search code

            return true;
        } else if (id == R.id.main_action_cart) {
            //todo cart code
            if (currentUsers == null) {
                signInDialog.show();
            } else {
                gotoFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
            }
            return true;
        } else if (id == android.R.id.home) {
            if (showCart) {
                mainActivity = null;
                showCart = false;
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void setFragments(Fragment fragments, int fragmentNo) {
        if (fragmentNo != currentFragment) {
            currentFragment = fragmentNo;
            if (currentFragment == REWARD_FRAGMENT) {
                window.setStatusBarColor(Color.parseColor("#58b041"));
                toolbar.setBackgroundColor(Color.parseColor("#58b041"));
            } else {
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            }

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(frameLayout.getId(), fragments);
            fragmentTransaction.commit();
        }

    }

    private void gotoFragment(String title, Fragment fragment, int fragmentNumber) {
        invalidateOptionsMenu();
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        actionBarLogo.setVisibility(View.INVISIBLE);
        getSupportActionBar().setTitle(title);
        setFragments(fragment, fragmentNumber);
        if (fragmentNumber == CART_FRAGMENT || showCart) {
            navigationView.getMenu().getItem(3).setChecked(true);
            params.setScrollFlags(0);
        } else {
            params.setScrollFlags(scrollFlags);
        }
        if (fragmentNumber == WISHLIST_FRAGMENT) {
            navigationView.getMenu().getItem(4).setChecked(true);
        }
        if (fragmentNumber == REWARD_FRAGMENT) {
            navigationView.getMenu().getItem(2).setChecked(true);
        }
        if (fragmentNumber == ACCOUNT_FRAGMENT) {
            navigationView.getMenu().getItem(5).setChecked(true);
        }
        if (fragmentNumber == ORDERS_FRAGMENT ){
            navigationView.getMenu().getItem(1).setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment == HOME_FRAGMENT) {
                currentFragment = -1;
                super.onBackPressed();
            } else {
                if (showCart) {
                    mainActivity = null;
                    showCart = false;
                    finish();
                } else {
                    actionBarLogo.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    setFragments(new HomeFragment(), HOME_FRAGMENT);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            }
        }
    }
}
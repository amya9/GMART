package com.example.foodtruck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodtruck.adapters.MyRewardsAdapter;
import com.example.foodtruck.adapters.ProductImageAdapter;
import com.example.foodtruck.adapters.productDescriptionViewPagerAdapter;
import com.example.foodtruck.firebase.FirebaseQueries;
import com.example.foodtruck.models.CartItemModel;
import com.example.foodtruck.models.MyRewardsModel;
import com.example.foodtruck.models.MyWishListModel;
import com.example.foodtruck.models.ProductSpecificationFeaturesModel;
import com.example.foodtruck.ui.myCart.MyCartFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.foodtruck.MainActivity.showCart;
import static com.example.foodtruck.RegisterActivity.signUpFragment;

public class ProductDetailsActivity extends AppCompatActivity {

    private ViewPager productImageViewPager;
    private TabLayout productImageIndicator;

    private Dialog signInDialog;
    private TabLayout productDescriptionTabLayout;
    private ViewPager productDescriptionViewPager;
    public static LinearLayout rateNowSystemContainer;
    private Button buyNowBtn;
    private Button redeemCouponBtn;
    private List<String> productImages = new ArrayList<>();

    private FrameLayout productSpecification;
    private FrameLayout productDescription;
    private ConstraintLayout otherProductDetails;
    private ConstraintLayout productDescriptionContainer;

    private FirebaseFirestore firebaseFirestore;

    /////////////////product image layout variable
    public static FloatingActionButton addToWishList;
    public static boolean ADDED_TO_WISHLIST = false;
    public static boolean ADDED_TO_CARTLIST = false;

    private TextView productTitle;

    private TextView productOriginalPrice;
    private TextView productDiscountedPrice;

    /////////////////product image layout variable

    /////////////////reward dialog
    public static TextView couponTitle;
    public static TextView couponBody;
    public static TextView couponExpiryDate;
    private static RecyclerView couponContainerRecyclerView;
    private static LinearLayout priceContainerLinearLayout;


    public  static Activity productDetailsActivity;

    public ProductDetailsActivity() {
    }

    private FirebaseUser currentUsers;

    /////////////////reward dialog
    /////////////////rating layout
    private LinearLayout ratingNumberContainer;
    private LinearLayout progressBarContainer;
    private TextView totalRating;
    private TextView sumOfAllRating;
    private TextView productAverageRating;
    private TextView productAverageRatingMiniView;
    private TextView productTotalRating;

    /////////////////rating layout
    /////////////////product description
    private String productDescriptionContent;
    private String productDetailsContent;
    public static String productOtherDescriptionContent;
    private List<ProductSpecificationFeaturesModel> productSpecificationFeaturesModelList;
    public static TextView othersDetails;
    public static String productId;
    public static Dialog loadingDialog;
    private DocumentSnapshot documentSnapshot;
    public static boolean running_wishlist_query = false;
    public static boolean running_rating_query = false;
    public static boolean running_cartlist_query = false;
    public static int initialRating;
    /////////////////product description

    /////////////////cart item variable
    private LinearLayout addToCart;
    public static MenuItem badgeMenu;
    private TextView badgeCount;
    /////////////////cart item variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String productName = getIntent().getStringExtra("ProductName");
        getSupportActionBar().setTitle(productName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productImageViewPager = findViewById(R.id.product_image_viewPager);
        productImageIndicator = findViewById(R.id.product_image_tabLayout);
        addToWishList = findViewById(R.id.add_product_to_wishList);
        addToCart = findViewById(R.id.add_to_cart_linearlayout);

        productTitle = findViewById(R.id.product_name_mini_view);
        productAverageRating = findViewById(R.id.product_mini_product_rating_tv);
        productAverageRatingMiniView = findViewById(R.id.average_rating_of_product_tv);
        productTotalRating = findViewById(R.id.product_mini_view_total_rating);
        productOriginalPrice = findViewById(R.id.product_original_price_tv);
        productDiscountedPrice = findViewById(R.id.product_discounted_price_tv);

        progressBarContainer = findViewById(R.id.rating_system_progressBar_container_linrarLayout);
        ratingNumberContainer = findViewById(R.id.total_individuals_rating_in_number_container_linearLayout);
        totalRating = findViewById(R.id.total_rating_in_number_of_product_tv);
        sumOfAllRating = findViewById(R.id.sum_of_total_rating_tv);

        productSpecification = findViewById(R.id.product_specification_fragment);
        productDescription = findViewById(R.id.produvt_description_fragment);
        otherProductDetails = findViewById(R.id.other_product_details_container);
        productDescriptionContainer = findViewById(R.id.product_description_container);
        othersDetails = findViewById(R.id.other_product_details);

        productDescriptionTabLayout = findViewById(R.id.product_description_tabLayout);
        productDescriptionViewPager = findViewById(R.id.product_description_viewPager);

        initialRating = -1;

        ////////////////////////////Loading Dialog

        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(R.layout.loading_details);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.recycler_view_background));
        loadingDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();


        ////////////////////////////Loading Dialog

        firebaseFirestore = FirebaseFirestore.getInstance();
        productId = getIntent().getStringExtra("PRODUCT_ID");
        assert productId != null;
        firebaseFirestore.collection("GMPRODUCTS").document(productId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    documentSnapshot = task.getResult();
                    for (long x = 1; x < (long) documentSnapshot.get("number_of_image") + 1; x++) {
                        productImages.add(documentSnapshot.get("product_image_" + x).toString());

                    }
                    ProductImageAdapter productImageAdapter = new ProductImageAdapter(productImages);
                    productImageViewPager.setAdapter(productImageAdapter);

                    productTitle.setText(documentSnapshot.get("product_title").toString());

                    productAverageRating.setText(documentSnapshot.get("product_average_rating").toString());
                    productAverageRatingMiniView.setText(documentSnapshot.get("product_average_rating").toString());

                    productOriginalPrice.setText("Rs." + documentSnapshot.get("product_original_price").toString() + "/-");
                    productDiscountedPrice.setText("Rs." + documentSnapshot.get("product_discounted_price").toString() + "/-");

                    productTotalRating.setText(String.valueOf("(" + documentSnapshot.getLong("product_total_rating").intValue()) + ") ratings");
                    totalRating.setText("(" + String.valueOf(documentSnapshot.getLong("product_total_rating").intValue()) + ") ratings");
                    sumOfAllRating.setText(String.valueOf(documentSnapshot.getLong("product_total_rating").intValue()));

                    int ratingContainerNumber = ratingNumberContainer.getChildCount();
                    for (int y = 0; y < 4 + 1; y++) {
                        TextView rating = (TextView) ratingNumberContainer.getChildAt(y);
                        rating.setText(String.valueOf(documentSnapshot.getLong((5 - y) + "_star_rating_number").intValue()));

                        ProgressBar progressBar = (ProgressBar) progressBarContainer.getChildAt(y);
                        int maxRating = Integer.parseInt(String.valueOf((long) documentSnapshot.get("product_total_rating")));
                        progressBar.setMax(maxRating);
                        progressBar.setProgress(Integer.parseInt(String.valueOf(documentSnapshot.getLong((5 - y) + "_star_rating_number").intValue())));
                    }
                    if ((boolean) documentSnapshot.get("use_tab_layout")) {
                        productDescriptionContainer.setVisibility(View.VISIBLE);
                        otherProductDetails.setVisibility(View.GONE);
                        productDescriptionContent = (String) documentSnapshot.get("product_description");
                        productSpecificationFeaturesModelList = new ArrayList<>();
                        for (long s = 1; s < (long) documentSnapshot.get("product_spec_field_number") + 1; s++) {
                            productSpecificationFeaturesModelList.add(new ProductSpecificationFeaturesModel(0, documentSnapshot.get("product_spec_field_" + s + "_title").toString()));
                            for (long t = 1; t < (long) documentSnapshot.get("number_of_spec_field_" + s) + 1; t++) {
                                productSpecificationFeaturesModelList.add(new ProductSpecificationFeaturesModel(1, documentSnapshot.get("product_spec_" + t + "_field_" + s + "_title").toString(), documentSnapshot.get("product_spec_" + t + "_field_" + s + "_value").toString()));
                            }
                        }
                        productOtherDescriptionContent = (String) documentSnapshot.get("product_other_details");
                    } else {
                        productDescriptionContainer.setVisibility(View.GONE);
                        otherProductDetails.setVisibility(View.VISIBLE);
                        productDetailsContent = (String) documentSnapshot.get("product_details");
                        othersDetails.setText(productDetailsContent);
                    }
                    productDescriptionViewPager.setAdapter(new productDescriptionViewPagerAdapter(getSupportFragmentManager(), productDescriptionTabLayout.getTabCount(), productDescriptionContent, productOtherDescriptionContent, productSpecificationFeaturesModelList));
                    if (currentUsers != null) {
                        ////////call all function rom Firebasequeries here and also in onstart method
                        if (FirebaseQueries.cartList.size() == 0) {
                            FirebaseQueries.loadCartItem(ProductDetailsActivity.this, loadingDialog, false, badgeCount , new TextView(ProductDetailsActivity.this));
                        }
                        if (FirebaseQueries.wishList.size() == 0) {
                            FirebaseQueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
                        } else {
                            loadingDialog.dismiss();
                        }
                        if (FirebaseQueries.ratingNumber.size() == 0) {
                            FirebaseQueries.loadRatings(ProductDetailsActivity.this);
                        }
                    } else {
                        loadingDialog.dismiss();
                    }
                    //////for rating function
                    if (FirebaseQueries.ratingIds.contains(productId)) {
                        int index = FirebaseQueries.ratingIds.indexOf(productId);
                        initialRating = Integer.parseInt(String.valueOf(FirebaseQueries.ratingNumber.get(index)));
                        setRatingColor(initialRating - 1);
                    }
                    //////for cartlist function
                    if (FirebaseQueries.cartList.contains(productId)) {
                        ADDED_TO_CARTLIST = true;
                    } else {
                        ADDED_TO_CARTLIST = false;
                    }
                    //////for wishlist function
                    if (FirebaseQueries.wishList.contains(productId)) {
                        ADDED_TO_WISHLIST = true;
                        addToWishList.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#ce0000")));
                    } else {
                        ADDED_TO_WISHLIST = false;
                        addToWishList.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#989898")));
                    }
                    //////// below code is to check if the product is in stock or not
                    //////// if it is in stock then visibility of add to cart linear layout is true and product is added to cart
                    //////// else visibility is set gone and out of stock text is attached to first child of linear layout
                    if ((boolean) documentSnapshot.get("in_stock")) {
                        addToCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (currentUsers == null) {
                                    signInDialog.show();
                                } else {
                                    ////todo
                                    if (!running_cartlist_query) {
                                        running_cartlist_query = true;
                                        Toast.makeText(ProductDetailsActivity.this, "Something is wrong here in code. revisit it ", Toast.LENGTH_LONG).show();
                                        if (!ADDED_TO_CARTLIST) {
                                            running_cartlist_query = false;
                                            Toast.makeText(ProductDetailsActivity.this, "Already added to cart", Toast.LENGTH_LONG).show();
                                            Map<String, Object> addProductId = new HashMap<>();
                                            addProductId.put("product_ID_" + FirebaseQueries.cartList.size(), productId);
                                            addProductId.put("list_size", (long) (FirebaseQueries.cartList.size() + 1));
                                            firebaseFirestore.collection("USERS").document(currentUsers.getUid()).collection("USER_DATA")
                                                    .document("MY_CART").update(addProductId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        if (FirebaseQueries.cartItemModelList.size() != 0) {
                                                            FirebaseQueries.cartItemModelList.add(0,new CartItemModel(CartItemModel.CART_ITEM_VIEW
                                                                    , productId
                                                                    , (String) documentSnapshot.get("product_image_1")
                                                                    , (long) documentSnapshot.get("offer_available")
                                                                    , (long) 1
                                                                    , (String) documentSnapshot.get("product_title")
                                                                    , (String) documentSnapshot.get("product_original_price")
                                                                    , (String) documentSnapshot.get("product_discounted_price")
                                                                    , (boolean) documentSnapshot.get("in_stock")));
                                                        }
                                                        ADDED_TO_CARTLIST = true;
                                                        FirebaseQueries.cartList.add(productId);
                                                        Toast.makeText(ProductDetailsActivity.this, "Added to cart!", Toast.LENGTH_LONG).show();
                                                        invalidateOptionsMenu();
                                                        running_cartlist_query = false;
                                                    } else {
                                                        running_cartlist_query = false;
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        });
                    } else {
                        buyNowBtn.setVisibility(View.GONE);
                        TextView outOfStock = (TextView) addToCart.getChildAt(0);
                        outOfStock.setText("Out of Stock");
                        outOfStock.setTextColor(getResources().getColor(R.color.errorColor));
                        outOfStock.setCompoundDrawables(null, null, null, null);
                    }
                } else {
                    loadingDialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        ProductImageAdapter productImageAdapter = new ProductImageAdapter(productImages);
        productImageViewPager.setAdapter(productImageAdapter);
        productImageIndicator.setupWithViewPager(productImageViewPager, true);

        addToWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUsers == null) {
                    signInDialog.show();
                } else {
//                    addToWishList.setEnabled(false);
                    if (!running_wishlist_query) {
                        running_wishlist_query = true;
                        if (ADDED_TO_WISHLIST) {
                            int index = FirebaseQueries.wishList.indexOf(productId);
                            FirebaseQueries.removeWishListProduct(index, ProductDetailsActivity.this);
                            addToWishList.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#989898")));
                        } else {
                            addToWishList.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#ce0000")));
                            Map<String, Object> addProductId = new HashMap<>();
                            addProductId.put("product_ID_" + FirebaseQueries.wishList.size(), productId);

                            firebaseFirestore.collection("USERS").document(currentUsers.getUid()).collection("USER_DATA")
                                    .document("MY_WISHLIST").update(addProductId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Map<String, Object> addListSize = new HashMap<>();
                                        addListSize.put("list_size", (long) (FirebaseQueries.wishList.size() + 1));

                                        firebaseFirestore.collection("USERS").document(currentUsers.getUid()).collection("USER_DATA")
                                                .document("MY_WISHLIST").update(addListSize).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    if (FirebaseQueries.myWishListModelList.size() != 0) {
                                                        FirebaseQueries.myWishListModelList.add(new MyWishListModel(productId
                                                                , documentSnapshot.get("product_image_1").toString()
//                                                , (long)documentSnapshot.get("offer_available")
                                                                , (String) documentSnapshot.get("product_average_rating")
                                                                , (Long) documentSnapshot.get("product_total_rating")
                                                                , (String) documentSnapshot.get("product_title")
                                                                , (String) documentSnapshot.get("product_original_price")
                                                                , (String) documentSnapshot.get("product_discounted_price")));
                                                    }
                                                    ADDED_TO_WISHLIST = false;
                                                    addToWishList.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#ce0000")));
                                                    FirebaseQueries.wishList.add(productId);
                                                    Toast.makeText(ProductDetailsActivity.this, "Successfully added to wishlist", Toast.LENGTH_LONG).show();
                                                } else {
                                                    addToWishList.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#989898")));
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                                running_wishlist_query = false;
                                            }
                                        });
                                    } else {

                                        running_wishlist_query = false;
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
        buyNowBtn = findViewById(R.id.buy_now_btn);
        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUsers == null) {
                    signInDialog.show();
                } else {
                    loadingDialog.show();
                    ///////we set null value to activity to kill all the background activity once payment is successful
                    productDetailsActivity = ProductDetailsActivity.this;
                    Toast.makeText(ProductDetailsActivity.this ,"find the error in this part of code product detail 419", Toast.LENGTH_LONG).show();
                    OrderSummeryActivity.cartItemModelList = new ArrayList<>();
                    OrderSummeryActivity.cartItemModelList.add(0 , new CartItemModel(CartItemModel.CART_ITEM_VIEW
                            , productId
                            , (String) documentSnapshot.get("product_image_1")
                            , (Long) documentSnapshot.get("offer_available")
                            , (long) 1
                            , (String) documentSnapshot.get("product_title")
                            , (String) documentSnapshot.get("product_original_price")
                            , (String) documentSnapshot.get("product_discounted_price")
                            , (boolean) documentSnapshot.get("in_stock")));
                    OrderSummeryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.BALANCE_DETAILS_VIEW));

                    if (FirebaseQueries.myAddressesModelList.size() == 0) {
                        FirebaseQueries.loadAddresses(ProductDetailsActivity.this, loadingDialog);
                    }else {
                        loadingDialog.dismiss();
                        Intent orderSummeryIntent = new Intent(ProductDetailsActivity.this, OrderSummeryActivity.class);
                        startActivity(orderSummeryIntent);
                        finish();
                    }
                }
            }
        });

        ///////////////////////////////reward Dialog
        redeemCouponBtn = findViewById(R.id.product_coupon_redeem_btn);
        redeemCouponBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUsers == null) {
                    signInDialog.show();
                } else {
                    Dialog rewardDialog = new Dialog(ProductDetailsActivity.this);
                    rewardDialog.setContentView(R.layout.reward_dialog);
                    rewardDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    rewardDialog.setCancelable(true);
                    ImageView hamIcon = rewardDialog.findViewById(R.id.coupon_dialog_ham_menu_icon);
                    couponContainerRecyclerView = rewardDialog.findViewById(R.id.coupon_dialog_recyclerview);
                    TextView originalPrice = rewardDialog.findViewById(R.id.coupon_dialog_originalprice);
                    TextView discountedPrice = rewardDialog.findViewById(R.id.coupon_dialog_price_after_coupon_applied);
                    priceContainerLinearLayout = rewardDialog.findViewById(R.id.coupon_dialog_coupon_desc_ll);
                    couponTitle = rewardDialog.findViewById(R.id.my_reward_title);
                    couponBody = rewardDialog.findViewById(R.id.my_rewards_description);
                    couponExpiryDate = rewardDialog.findViewById(R.id.my_rewards_validity_date_tv);


                    LinearLayoutManager layoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    couponContainerRecyclerView.setLayoutManager(layoutManager);
                    List<MyRewardsModel> myRewardsModelList = new ArrayList<>();
                    myRewardsModelList.add(new MyRewardsModel("Cashback", "till 3rd, August 2018", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-"));
                    myRewardsModelList.add(new MyRewardsModel("Cashback", "till 3rd, August 2018", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-"));
                    myRewardsModelList.add(new MyRewardsModel("Cashback", "till 3rd, August 2018", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-"));
                    myRewardsModelList.add(new MyRewardsModel("Cashback", "till 3rd, August 2018", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-"));
                    myRewardsModelList.add(new MyRewardsModel("Cashback", "till 3rd, August 2018", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-"));
                    myRewardsModelList.add(new MyRewardsModel("Cashback", "till 3rd, August 2018", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-"));
                    myRewardsModelList.add(new MyRewardsModel("Cashback", "till 3rd, August 2018", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-"));
                    myRewardsModelList.add(new MyRewardsModel("Cashback", "till 3rd, August 2018", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-"));

                    MyRewardsAdapter myRewardsAdapter = new MyRewardsAdapter(myRewardsModelList, true);
                    couponContainerRecyclerView.setAdapter(myRewardsAdapter);
                    myRewardsAdapter.notifyDataSetChanged();

                    hamIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setVisible();
                        }
                    });
                    rewardDialog.show();
                }
            }
        });


        ///////////////////////////////reward Dialog


        productDescriptionViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDescriptionTabLayout));
        productDescriptionTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDescriptionViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ///////////////////sign un dialog
        signInDialog = new Dialog(ProductDetailsActivity.this);
        signInDialog.setCancelable(true);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        final Button signInBtn = signInDialog.findViewById(R.id.dialog_sign_in_btn);
        Button signUpBtn = signInDialog.findViewById(R.id.dialog_sign_up_btn);

        final Intent registerIntent = new Intent(ProductDetailsActivity.this, RegisterActivity.class);
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
        ///////////////////sign un dialog

        /////////////////rating layout
        rateNowSystemContainer = findViewById(R.id.rate_now_stars_container_linearLayout);
        for (int i = 0; i < rateNowSystemContainer.getChildCount(); i++) {
            final int starPosition = i;
            rateNowSystemContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUsers == null) {
                        signInDialog.show();
                    } else {
                        if (starPosition != initialRating) {
                            if (!running_rating_query) {

                                running_rating_query = true;
                                setRatingColor(starPosition);

                                Map<String, Object> updateRating = new HashMap<>();
                                if (FirebaseQueries.ratingIds.contains(productId)) {
                                    //USER HAS RATED IE USER WILL UPDATE ITS RATING
                                    TextView oldRating = (TextView) ratingNumberContainer.getChildAt(5 - initialRating - 1);
                                    TextView finalRating = (TextView) ratingNumberContainer.getChildAt(5 - starPosition - 1);

                                    updateRating.put(initialRating + 1 + "_star_rating_number", Long.parseLong(oldRating.getText().toString()) - 1);
                                    updateRating.put(starPosition + 1 + "_star_rating_number", Long.parseLong(finalRating.getText().toString()) + 1);
                                    updateRating.put("product_average_rating", String.valueOf(averageRatingCalculation((long) starPosition - initialRating, true)));
                                    Toast.makeText(ProductDetailsActivity.this, "stage 1 ", Toast.LENGTH_SHORT).show();


                                } else {

                                    //products collection rating first time rating
                                    updateRating.put((starPosition + 1) + "_star_rating_number", (long) documentSnapshot.get(starPosition + 1 + "_star_rating_number") + 1);
                                    updateRating.put("product_average_rating", String.valueOf(averageRatingCalculation((long) starPosition + 1, false)));
                                    updateRating.put("product_total_rating", ((long) documentSnapshot.get("product_total_rating") + 1));
                                }

                                firebaseFirestore.collection("GMPRODUCTS")
                                        .document(productId)
                                        .update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProductDetailsActivity.this, "inner stage ", Toast.LENGTH_SHORT).show();
                                            //USER IS RATING FOR THE FIRST TIME
                                            Map<String, Object> myRatings = new HashMap<>();

                                            if (FirebaseQueries.ratingIds.contains(productId)) {
                                                myRatings.put(FirebaseQueries.ratingIds.indexOf(productId) + "_star_rating_number", (long) starPosition + 1);
                                            } else {
                                                myRatings.put("list_size", (long) FirebaseQueries.ratingIds.size() + 1);
                                                myRatings.put("product_ID_" + FirebaseQueries.ratingIds.size(), productId);
                                                myRatings.put(FirebaseQueries.ratingIds.size() + "_star_rating_number", (long) (starPosition + 1));

                                            }

                                            firebaseFirestore.collection("USERS")
                                                    .document(currentUsers.getUid())
                                                    .collection("USER_DATA")
                                                    .document("MY_RATINGS")
                                                    .update(myRatings).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        //now update in gmproducts collection
                                                        if (FirebaseQueries.ratingIds.contains(productId)) {
                                                            FirebaseQueries.ratingNumber.set(FirebaseQueries.ratingIds.indexOf(productId), (long) starPosition + 1);

                                                            TextView oldRating = (TextView) ratingNumberContainer.getChildAt(5 - initialRating - 1);
                                                            TextView finalRating = (TextView) ratingNumberContainer.getChildAt(5 - starPosition - 1);

                                                            oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
                                                            finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) + 1));

                                                        } else {
                                                            FirebaseQueries.ratingIds.add(productId);
                                                            FirebaseQueries.ratingNumber.add((long) starPosition + 1);

                                                            TextView rating = (TextView) ratingNumberContainer.getChildAt(5 - starPosition - 1);
                                                            rating.setText(String.valueOf(Integer.parseInt(rating.getText().toString()) + 1));

                                                            productTotalRating.setText("(" + (documentSnapshot.getLong("product_total_rating").intValue() + 1) + ") ratings");
                                                            totalRating.setText("(" + String.valueOf(documentSnapshot.getLong("product_total_rating").intValue() + 1) + ") ratings");
                                                            sumOfAllRating.setText(String.valueOf(documentSnapshot.getLong("product_total_rating").intValue() + 1));

                                                            Toast.makeText(ProductDetailsActivity.this, "Thank you for rating Us!", Toast.LENGTH_LONG).show();
                                                        }
                                                        for (int y = 0; y < 4 + 1; y++) {
                                                            TextView ratingNumber = (TextView) ratingNumberContainer.getChildAt(y);

                                                            ProgressBar progressBar = (ProgressBar) progressBarContainer.getChildAt(y);
                                                            int maxRating = Integer.parseInt(sumOfAllRating.getText().toString());
                                                            progressBar.setMax(maxRating);

                                                            progressBar.setProgress(Integer.parseInt(ratingNumber.getText().toString()));
                                                        }
                                                        initialRating = starPosition;
                                                        productAverageRating.setText(String.valueOf(averageRatingCalculation(0, true)));
                                                        productAverageRatingMiniView.setText(String.valueOf(averageRatingCalculation(0, true)));
                                                        //below code will set the ratings value in wishlist fragment
                                                        if (FirebaseQueries.wishList.contains(productId) && FirebaseQueries.myWishListModelList.size() != 0) {
                                                            int index = FirebaseQueries.wishList.indexOf(productId);
                                                            FirebaseQueries.myWishListModelList.get(index).setAverageRating(productAverageRating.getText().toString());
                                                            FirebaseQueries.myWishListModelList.get(index).setTotalRating(Long.parseLong(sumOfAllRating.getText().toString()));
                                                        }
                                                        //////wishlist code end here
                                                    } else {
                                                        setRatingColor(initialRating);
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_LONG).show();
                                                    }
                                                    running_rating_query = false;
                                                }
                                            });
                                        } else {
                                            running_rating_query = false;
                                            setRatingColor(initialRating);
                                            String error = task.getException().getMessage();
                                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
        /////////////////rating layout
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUsers = FirebaseAuth.getInstance().getCurrentUser();

        ////////////////////
        if (currentUsers != null) {
            if (FirebaseQueries.ratingNumber.size() == 0) {
                FirebaseQueries.loadRatings(ProductDetailsActivity.this);
            }
            if (FirebaseQueries.wishList.size() == 0) {
                FirebaseQueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
            } else {
                loadingDialog.dismiss();
            }

        } else {
            loadingDialog.dismiss();
        }
        //////////////////////above
        //////for rating function
        if (FirebaseQueries.ratingIds.contains(productId)) {
            int index = FirebaseQueries.ratingIds.indexOf(productId);
            initialRating = Integer.parseInt(String.valueOf(FirebaseQueries.ratingNumber.get(index)));
            setRatingColor(initialRating - 1);
        }
        //////for cartlist function
        if (FirebaseQueries.cartList.contains(productId)) {
            ADDED_TO_CARTLIST = true;
        } else {
            ADDED_TO_CARTLIST = false;
        }
        //////for wishlist function
        if (FirebaseQueries.wishList.contains(productId)) {
            ADDED_TO_WISHLIST = true;
            addToWishList.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#ce0000")));
        } else {
            ADDED_TO_WISHLIST = false;
        }
        invalidateOptionsMenu();
    }

    public static void setVisible() {
        if (couponContainerRecyclerView.getVisibility() == View.GONE) {
            couponContainerRecyclerView.setVisibility(View.VISIBLE);
            priceContainerLinearLayout.setVisibility(View.GONE);
        } else {
            couponContainerRecyclerView.setVisibility(View.GONE);
            priceContainerLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    ///////////set rating wala code
    public static void setRatingColor(int starPos) {
        for (int y = 0; y < rateNowSystemContainer.getChildCount(); y++) {
            ImageView starBtn = (ImageView) rateNowSystemContainer.getChildAt(y);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#989898")));
            if (y <= starPos) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffff00")));
            }
        }
    }

    //average rating calculation method
    private String averageRatingCalculation(long currentUserRating, boolean update) {
        Double totalStars = (double) 0;
        for (int x = 1; x < 6; x++) {
            TextView ratingNo = (TextView) ratingNumberContainer.getChildAt(5 - x);
            totalStars = totalStars + (Long.parseLong(ratingNo.getText().toString()) * x);
        }
        totalStars = totalStars + currentUserRating;
        if (update) {
            return String.valueOf(totalStars / Long.parseLong(sumOfAllRating.getText().toString())).substring(0, 3);
        } else {
            return String.valueOf(totalStars / (Long.parseLong(sumOfAllRating.getText().toString()) + 1)).substring(0, 3);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_cart_menu, menu);
        //here setting badge layout on cart menu to show number of item in cart

        badgeMenu = menu.findItem(R.id.main_action_cart);
        badgeMenu.setActionView(R.layout.badge_layout);
        ImageView badgeIcon = badgeMenu.getActionView().findViewById(R.id.badge_icon);
        badgeIcon.setImageResource(R.mipmap.cart_white);
        badgeCount = badgeMenu.getActionView().findViewById(R.id.badge_text);
        if (currentUsers != null) {
            if (FirebaseQueries.cartList.size() == 0) {
                FirebaseQueries.loadCartItem(ProductDetailsActivity.this, loadingDialog, false, badgeCount , new TextView(ProductDetailsActivity.this));
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
                    Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                    showCart = true;
                    startActivity(cartIntent);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            ///////we set null value to activity to kill all the background activity once payment is successfull
            productDetailsActivity = null;
            finish();
            return true;
        } else if (id == R.id.main_action_search) {
            //todo search code
            return true;
        } else if (id == R.id.main_action_cart) {
            //todo cart code
            if (currentUsers == null) {
                signInDialog.show();
            } else {
                Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                showCart = true;
                startActivity(cartIntent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ///////we set null value to activity to kill all the background activity once payment is successfull
        productDetailsActivity = null;
        super.onBackPressed();
    }
}
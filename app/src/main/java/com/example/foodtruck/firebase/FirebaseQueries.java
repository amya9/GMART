package com.example.foodtruck.firebase;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodtruck.AddNewAddressActivity;
import com.example.foodtruck.OrderSummeryActivity;
import com.example.foodtruck.ProductDetailsActivity;
import com.example.foodtruck.adapters.CategoryAdapter;
import com.example.foodtruck.adapters.HomePageAdapter;
import com.example.foodtruck.models.CartItemModel;
import com.example.foodtruck.models.CategoryModel;
import com.example.foodtruck.models.HomePageModel;
import com.example.foodtruck.models.HorizontalProductScrollModel;
import com.example.foodtruck.models.MyAddressesModel;
import com.example.foodtruck.models.MyWishListModel;
import com.example.foodtruck.models.SliderModel;
import com.example.foodtruck.ui.home.HomeFragment;
import com.example.foodtruck.ui.myCart.MyCartFragment;
import com.example.foodtruck.ui.myWishlist.MyWishlistFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseQueries {

    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static List<CategoryModel> categoryModelList = new ArrayList<>();

    //    public static List<HomePageModel> homePageModelList = new ArrayList<>();
    public static List<SliderModel> sliderModelList;

    public static List<List<HomePageModel>> mainLists = new ArrayList<>();
    public static List<String> loadedListName = new ArrayList<>();

    public static List<String> wishList = new ArrayList<>();
    public static List<MyWishListModel> myWishListModelList = new ArrayList<>();

    public static List<String> ratingIds = new ArrayList<>();
    public static List<Long> ratingNumber = new ArrayList<>();

    public static List<String> cartList = new ArrayList<>();
    public static List<CartItemModel> cartItemModelList = new ArrayList<>();

    public static List<MyAddressesModel> myAddressesModelList = new ArrayList<>();
    public static int selectedAddress = -1;


    public static void loadCategoriesData(final RecyclerView categoryRecyclerView, final Context context) {
        categoryModelList.clear();
        firebaseFirestore.collection("GMCATEGORIES").orderBy("index").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        categoryModelList.add(new CategoryModel(documentSnapshot.get("iconUrl").toString(), documentSnapshot.get("categoryName").toString()));
                    }
                    CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModelList);
                    categoryRecyclerView.setAdapter(categoryAdapter);
                    categoryAdapter.notifyDataSetChanged();
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    ///////////////////////////load fragment in home fragment
    public static void loadHomeFragmentView(final RecyclerView homePageRecyclerView, final Context context, final int index, String categoryName) {
//        mainLists.clear();
//        loadedListName.clear();

        firebaseFirestore.collection("GMCATEGORIES").document(categoryName)
                .collection("HOMEPAGE_ITEMS").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if ((long) documentSnapshot.get("view_type") == 0) {
                                    sliderModelList = new ArrayList<>();
                                    long bannerNo = (long) documentSnapshot.get("number_of_banner");
                                    for (long x = 1; x < bannerNo + 1; x++) {
                                        sliderModelList.add(new SliderModel(documentSnapshot.get("banner_" + x).toString()
                                                , documentSnapshot.get("banner_" + x + "_background").toString()));
                                    }
                                    mainLists.get(index).add(new HomePageModel(0, sliderModelList));
                                } else if ((long) documentSnapshot.get("view_type") == 1) {
                                    mainLists.get(index).add(new HomePageModel(1, documentSnapshot.get("stripAd_banner").toString()
                                            , documentSnapshot.get("stripAd_background").toString()));

                                } else if ((long) documentSnapshot.get("view_type") == 2) {
                                    List<MyWishListModel> viewAllProductLists = new ArrayList<>();
                                    List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();
                                    long number_of_product = (long) documentSnapshot.get("number_of_product");
                                    for (long x = 1; x < number_of_product + 1; x++) {
                                        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_" + x).toString()
                                                , documentSnapshot.get("product_image_" + x).toString()
                                                , documentSnapshot.get("product_title_" + x).toString()
                                                , documentSnapshot.get("product_description_" + x).toString()
                                                , documentSnapshot.get("product_price_" + x).toString()));
                                        viewAllProductLists.add(new MyWishListModel(documentSnapshot.get("product_ID_" + x).toString()
                                                , documentSnapshot.get("product_image_" + x).toString()
//                                                , (long)documentSnapshot.get("offer_available_" + x)
                                                , (String) documentSnapshot.get("average_rating_" + x)
                                                , (long) documentSnapshot.get("total_rating_" + x)
                                                , (String) documentSnapshot.get("product_full_title_" + x)
                                                , (String) documentSnapshot.get("product_price_" + x)
                                                , (String) documentSnapshot.get("discounted_price_" + x)));
                                    }
                                    mainLists.get(index).add(new HomePageModel(2, documentSnapshot.get("layout_background").toString()
                                            , documentSnapshot.get("layout_title").toString(), horizontalProductScrollModelList, viewAllProductLists));
                                } else if ((long) documentSnapshot.get("view_type") == 3) {
                                    List<HorizontalProductScrollModel> gridlayoutModelList = new ArrayList<>();
                                    long number_of_product = (long) documentSnapshot.get("number_of_product");
                                    for (long x = 1; x < number_of_product + 1; x++) {
                                        gridlayoutModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_" + x).toString()
                                                , documentSnapshot.get("product_image_" + x).toString()
                                                , documentSnapshot.get("product_title_" + x).toString()
                                                , documentSnapshot.get("product_description_" + x).toString()
                                                , documentSnapshot.get("product_price_" + x).toString()));
                                    }
                                    mainLists.get(index).add(new HomePageModel(3, documentSnapshot.get("layout_background").toString()
                                            , documentSnapshot.get("layout_title").toString(), gridlayoutModelList));
                                }
                                HomePageAdapter homePageAdapter = new HomePageAdapter(mainLists.get(index));
                                homePageRecyclerView.setAdapter(homePageAdapter);
                                homePageAdapter.notifyDataSetChanged();
                                HomeFragment.swipeRefreshLayout.setRefreshing(false);
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    public static void loadWishList(final Context context, final Dialog dialog, final boolean loadData) {
        wishList.clear();
        firebaseFirestore.collection("USERS")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection("USER_DATA")
                .document("MY_WISHLIST")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        wishList.add(task.getResult().get("product_ID_" + x).toString());
                        if (FirebaseQueries.wishList.contains(ProductDetailsActivity.productId)) {
                            ProductDetailsActivity.ADDED_TO_WISHLIST = true;
                            if (ProductDetailsActivity.addToWishList != null) {
                                ProductDetailsActivity.addToWishList.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#ce0000")));
                            }
                        } else {
                            ProductDetailsActivity.ADDED_TO_WISHLIST = false;
                            if (ProductDetailsActivity.addToWishList != null) {
                                ProductDetailsActivity.addToWishList.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#989898")));
                            }
                        }
                        if (loadData) {
                            myWishListModelList.clear();
                            final String productId = task.getResult().get("product_ID_" + x).toString();
                            firebaseFirestore.collection("GMPRODUCTS").document(productId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        myWishListModelList.add(new MyWishListModel(productId
                                                , (String) task.getResult().get("product_image_1")
//                                                , (long)documentSnapshot.get("offer_available_" )
                                                , (String) task.getResult().get("product_average_rating")
                                                , (long) task.getResult().get("product_total_rating")
                                                , (String) task.getResult().get("product_title")
                                                , (String) task.getResult().get("product_original_price")
                                                , (String) task.getResult().get("product_discounted_price")));
                                        MyWishlistFragment.myWishListAdapter.notifyDataSetChanged();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
    }

    public static void removeWishListProduct(final int index, final Context context) {
        final String removedProductId = wishList.get(index);
        wishList.remove(index);
        Map<String, Object> updateWishList = new HashMap<>();
        for (int x = 0; x < wishList.size(); x++) {
            updateWishList.put("product_ID_" + x, wishList.get(x));
        }
        updateWishList.put("list_size", (long) wishList.size());

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA")
                .document("MY_WISHLIST")
                .set(updateWishList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (myWishListModelList.size() != 0) {
                        myWishListModelList.remove(index);
                        MyWishlistFragment.myWishListAdapter.notifyDataSetChanged();
                    }
                    ProductDetailsActivity.ADDED_TO_WISHLIST = false;
                    Toast.makeText(context, "Successfully removed from wishlist", Toast.LENGTH_LONG).show();

                } else {
                    if (ProductDetailsActivity.addToWishList != null) {
                        ProductDetailsActivity.addToWishList.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#ce0000")));
                    }
                    wishList.add(index, removedProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
                ProductDetailsActivity.running_wishlist_query = false;
            }
        });

    }

    public static void loadRatings(final Context context) {
        if (!ProductDetailsActivity.running_rating_query) {
            ProductDetailsActivity.running_rating_query = true;
            ratingIds.clear();
            ratingNumber.clear();
            firebaseFirestore.collection("USERS")
                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .collection("USER_DATA")
                    .document("MY_RATINGS")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                            ratingIds.add(task.getResult().get("product_ID_" + x).toString());
                            ratingNumber.add((Long) task.getResult().get(x + "_star_rating_number"));
                            if (task.getResult().get("product_ID_" + x).toString().equals(ProductDetailsActivity.productId)) {
                                ProductDetailsActivity.initialRating = Integer.parseInt(String.valueOf(task.getResult().get(x + "_star_rating_number"))) - 1;
                                if (ProductDetailsActivity.rateNowSystemContainer != null) {
                                    ProductDetailsActivity.setRatingColor(ProductDetailsActivity.initialRating);
                                }
                            }
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                    }
                    ProductDetailsActivity.running_rating_query = false;
                }
            });
        }
    }

    public static void loadCartItem(final Context context, final Dialog dialog, final boolean loadData, final TextView badgeCount , final TextView totalAmountInCartFragment ) {
        cartList.clear();
        firebaseFirestore.collection("USERS")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection("USER_DATA")
                .document("MY_CART")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        cartList.add(task.getResult().get("product_ID_" + x).toString());
                        if (FirebaseQueries.cartList.contains(ProductDetailsActivity.productId)) {
                            ProductDetailsActivity.ADDED_TO_CARTLIST = true;
                        } else {
                            ProductDetailsActivity.ADDED_TO_CARTLIST = false;
                        }
                        if (loadData) {
                            cartItemModelList.clear();
                            final String productId = task.getResult().get("product_ID_" + x).toString();
                            firebaseFirestore.collection("GMPRODUCTS").document(productId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        //this will help in showing the balance layout only once
                                        int index = 0;
                                        if (cartList.size() >= 2) {
                                            index = cartList.size() - 2;
                                        }
                                        cartItemModelList.add(index, new CartItemModel(CartItemModel.CART_ITEM_VIEW
                                                , productId
                                                , (String) task.getResult().get("product_image_1")
                                                , (long) task.getResult().get("number_of_offer")
                                                , (long) 1
                                                , (String) task.getResult().get("product_title")
                                                , (String) task.getResult().get("product_original_price")
                                                , (String) task.getResult().get("product_discounted_price")
                                                , (boolean) task.getResult().get("in_stock")));

                                        // this will show the balance details only once and not every time
                                        if (cartList.size() == 1) {
                                            cartItemModelList.add(new CartItemModel(CartItemModel.BALANCE_DETAILS_VIEW));
                                            LinearLayout parent = (LinearLayout) totalAmountInCartFragment.getParent().getParent();
                                            parent.setVisibility(View.VISIBLE);

                                        }
                                        //clear the modellist there is no item in cartlist
                                        if (cartList.size() == 0) {
                                            cartItemModelList.clear();
                                        }
                                        MyCartFragment.cartItemAdapter.notifyDataSetChanged();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                    if (cartList.size() != 0) {
                        badgeCount.setVisibility(View.VISIBLE);
                    } else {
                        badgeCount.setVisibility(View.INVISIBLE);
                    }
                    if (FirebaseQueries.cartList.size() < 99) {
                        badgeCount.setText(String.valueOf(FirebaseQueries.cartList.size()));
                    } else {
                        badgeCount.setText("99");
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
    }

    public static void removeCartItem(final int index, final Context context, final TextView totalAmountInCartFragment) {
        final String removedProductId = cartList.get(index);
        cartList.remove(index);
        Map<String, Object> updateCartList = new HashMap<>();
        for (int x = 0; x < cartList.size(); x++) {
            updateCartList.put("product_ID_" + x, cartList.get(x));
        }
        updateCartList.put("list_size", (long) cartList.size());

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA")
                .document("MY_CART")
                .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (cartItemModelList.size() != 0) {
                        cartItemModelList.remove(index);
                        MyCartFragment.cartItemAdapter.notifyDataSetChanged();
                    }
                    ProductDetailsActivity.ADDED_TO_CARTLIST = false;
                    //clear the modellist there is no item in cartlist
                    if (cartList.size() == 0) {
                        LinearLayout parent = (LinearLayout) totalAmountInCartFragment.getParent().getParent();
                        parent.setVisibility(View.GONE);

                        cartItemModelList.clear();
                    }
                    Toast.makeText(context, "Removed from cart", Toast.LENGTH_LONG).show();

                } else {
                    cartList.add(index, removedProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
                ProductDetailsActivity.running_cartlist_query = false;
            }
        });

    }

    public static void loadAddresses(final Context context, final Dialog dialog) {
        myAddressesModelList.clear();

        firebaseFirestore.collection("USERS")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection("USER_DATA")
                .document("MY_ADDRESSES")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Intent orderSummeryIntent;
                    if ((long) task.getResult().get("list_size") == 0) {
                        orderSummeryIntent = new Intent(context, AddNewAddressActivity.class);
                        orderSummeryIntent.putExtra("INTENT", "orderSummeryIntent");
                    } else {
                        for (long x = 1; x < (long) task.getResult().get("list_size") + 1; x++) {
                            myAddressesModelList.add(new MyAddressesModel(task.getResult().get("Full_Name_" + x).toString(),
                                    task.getResult().get("Address_" + x).toString(),
                                    task.getResult().get("Mobile_Number_" + x).toString(),
                                    (boolean) task.getResult().get("selected_" + x),
                                    task.getResult().get("pinCode_" + x).toString()));

                            if ((boolean) task.getResult().get("selected_" + x)) {
                                selectedAddress = Integer.parseInt(String.valueOf(x - 1));
                            }
                        }
                        orderSummeryIntent = new Intent(context, OrderSummeryActivity.class);
                    }
                    context.startActivity(orderSummeryIntent);

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
    }


    public static void clearListData() {
        mainLists.clear();
        categoryModelList.clear();
        wishList.clear();
        loadedListName.clear();
        myWishListModelList.clear();
        cartList.clear();
        cartItemModelList.clear();
        myAddressesModelList.clear();
    }

}

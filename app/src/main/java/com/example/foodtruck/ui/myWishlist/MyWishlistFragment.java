package com.example.foodtruck.ui.myWishlist;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.foodtruck.R;
import com.example.foodtruck.adapters.MyWishListAdapter;
import com.example.foodtruck.firebase.FirebaseQueries;


public class MyWishlistFragment extends Fragment {

    private RecyclerView myWishListRecyclerView;
    private Dialog loadingDialog;
    public static MyWishListAdapter myWishListAdapter;

    private LinearLayout removeWishlistProduct;
    private LinearLayout addToCartFromWishList;


    public MyWishlistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_wishlist, container, false);
        ////////////////////////////Loading Dialog

        loadingDialog = new Dialog(getContext());
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(R.layout.loading_details);
        loadingDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.recycler_view_background));
        loadingDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();


        ////////////////////////////Loading Dialog

        myWishListRecyclerView = view.findViewById(R.id.my_wishlist_recyclerView);
        removeWishlistProduct = view.findViewById(R.id.cart_remove_product_from_cart_btn);
        addToCartFromWishList = view.findViewById(R.id.my_wishlist_move_to_cart);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        myWishListRecyclerView.setLayoutManager(layoutManager);

        if (FirebaseQueries.myWishListModelList.size() == 0) {
            FirebaseQueries.myWishListModelList.clear();
            FirebaseQueries.loadWishList(getContext(), loadingDialog, true);
        } else {
            loadingDialog.dismiss();
        }


        myWishListAdapter = new MyWishListAdapter(FirebaseQueries.myWishListModelList, true);
        myWishListRecyclerView.setAdapter(myWishListAdapter);
        myWishListAdapter.notifyDataSetChanged();
        return view;
    }
}
package com.example.foodtruck.ui.myCart;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.foodtruck.OrderSummeryActivity;
import com.example.foodtruck.ProductDetailsActivity;
import com.example.foodtruck.R;
import com.example.foodtruck.adapters.CartItemAdapter;
import com.example.foodtruck.adapters.MyWishListAdapter;
import com.example.foodtruck.firebase.FirebaseQueries;
import com.example.foodtruck.models.CartItemModel;

import java.util.ArrayList;
import java.util.List;

public class MyCartFragment extends Fragment {
    private RecyclerView cartRecyclerView;
    private Button continueToOrderSummery;
    private Dialog loadingDialog;
    public static CartItemAdapter cartItemAdapter;
    private TextView totalAmountInCartFragment;


    public MyCartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view;
        view = inflater.inflate(R.layout.fragment_my_cart, container, false);

        ////////////////////////////Loading Dialog

        loadingDialog = new Dialog(getContext());
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(R.layout.loading_details);
        loadingDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.recycler_view_background));
        loadingDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        ////////////////////////////Loading Dialog
        continueToOrderSummery = view.findViewById(R.id.continue_to_checkout_btn);
        cartRecyclerView = view.findViewById(R.id.my_cart_recyclerView);

        totalAmountInCartFragment = view.findViewById(R.id.total_amount_in_cartFragment_tv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        cartRecyclerView.setLayoutManager(layoutManager);
        if (FirebaseQueries.cartItemModelList.size() == 0) {
            FirebaseQueries.cartItemModelList.clear();
            FirebaseQueries.loadCartItem(getContext(), loadingDialog, true, new TextView(getContext()) , totalAmountInCartFragment);
        } else {
            if (FirebaseQueries.cartItemModelList.get(FirebaseQueries.cartItemModelList.size() - 1).getType() == CartItemModel.BALANCE_DETAILS_VIEW) {
                LinearLayout parent = (LinearLayout) totalAmountInCartFragment.getParent().getParent();
                parent.setVisibility(View.VISIBLE);
            }
            loadingDialog.dismiss();
        }

//        List<CartItemModel>cartItemModelList = new ArrayList<>();
//        cartItemModelList.add(new CartItemModel(0 , R.drawable.phone , 2 , 1 , 3 , 4 , "One plus Nord" , "Rs.49,999/-" ,"Rs.28,999/-"));
//        cartItemModelList.add(new CartItemModel(0 , R.drawable.phone , 2 , 1 , 3 , 4 , "One plus Nord" , "Rs.49,999/-" ,"Rs.28,999/-"));
//
//        cartItemModelList.add(new CartItemModel(1 , "5", "Rs.45,000/-" , "Rs.50/-" ,"Rs.8,999/-" , ""));
        cartItemAdapter = new CartItemAdapter(FirebaseQueries.cartItemModelList, totalAmountInCartFragment, true);
        cartRecyclerView.setAdapter(cartItemAdapter);
        cartItemAdapter.notifyDataSetChanged();
        continueToOrderSummery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderSummeryActivity.cartItemModelList = new ArrayList<>();
                for (int x = 0 ;x < FirebaseQueries.cartItemModelList.size() ; x++){
                    CartItemModel cartItemModel = FirebaseQueries.cartItemModelList.get(x);
                    if (cartItemModel.isInStock()){
                        OrderSummeryActivity.cartItemModelList.add(cartItemModel);
                    }
                }
                OrderSummeryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.BALANCE_DETAILS_VIEW));
                loadingDialog.show();
                if (FirebaseQueries.myAddressesModelList.size() == 0) {
                    FirebaseQueries.loadAddresses(view.getContext(), loadingDialog);
                }else {
                    loadingDialog.dismiss();
                    Intent orderSummeryIntent = new Intent(getContext(), OrderSummeryActivity.class);
                    startActivity(orderSummeryIntent);
                }
            }
        });

        return view;
    }


}
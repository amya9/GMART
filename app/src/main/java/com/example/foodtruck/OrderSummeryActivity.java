package com.example.foodtruck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.foodtruck.adapters.CartItemAdapter;
import com.example.foodtruck.firebase.FirebaseQueries;
import com.example.foodtruck.models.CartItemModel;
import com.example.foodtruck.payment.PaymentOptions;

import java.util.ArrayList;
import java.util.List;

public class OrderSummeryActivity extends AppCompatActivity {
    private RecyclerView orderSummeryRecyclerView;
    private Button changeOrAddNewAddress;
    private ConstraintLayout cartBalanceLayout;
    public static final int SELECT_ADDRESS = 0;
   public static TextView totalAmountOrderSummery;
   private TextView fullName;
   private TextView fullAddress;
   private TextView mobileNumber;
   private Button continueToCheckOut;
   public static String userName , mobileNumb;

   public static List<CartItemModel>cartItemModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Order Summery");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fullName = findViewById(R.id.shipping_address_name_tv);
        fullAddress = findViewById(R.id.shipping_address_delivery_address_tv);
        mobileNumber = findViewById(R.id.shipping_address_mobile_no_tv);

        orderSummeryRecyclerView = findViewById(R.id.order_summery_recyclerView);
        changeOrAddNewAddress = findViewById(R.id.shipping_address_change_or_add_address_btn);
        cartBalanceLayout = findViewById(R.id.cart_balance_constraint_layout);
        totalAmountOrderSummery = findViewById(R.id.total_amount_in_orderSummery_tv);
        cartBalanceLayout.setVisibility(View.GONE);

        continueToCheckOut = findViewById(R.id.continue_to_checkout_btn);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        orderSummeryRecyclerView.setLayoutManager(layoutManager);

//        List<CartItemModel> cartItemModelList = new ArrayList<>();
//        cartItemModelList.add(new CartItemModel(0 , R.drawable.phone , 2 , 1 , 3 , 4 , "One plus Nord" , "Rs.49,999/-" ,"Rs.28,999/-"));
//        cartItemModelList.add(new CartItemModel(0 , R.drawable.phone , 2 , 1 , 3 , 4 , "One plus Nord" , "Rs.49,999/-" ,"Rs.28,999/-"));
//        cartItemModelList.add(new CartItemModel(0 , R.drawable.phone , 2 , 1 , 3 , 4 , "One plus Nord" , "Rs.49,999/-" ,"Rs.28,999/-"));
//        cartItemModelList.add(new CartItemModel(0 , R.drawable.phone , 2 , 1 , 3 , 4 , "One plus Nord" , "Rs.49,999/-" ,"Rs.28,999/-"));
//        cartItemModelList.add(new CartItemModel(0 , R.drawable.phone , 2 , 1 , 3 , 4 , "One plus Nord" , "Rs.49,999/-" ,"Rs.28,999/-"));
//        cartItemModelList.add(new CartItemModel(0 , R.drawable.phone , 2 , 1 , 3 , 4 , "One plus Nord" , "Rs.49,999/-" ,"Rs.28,999/-"));
//        cartItemModelList.add(new CartItemModel(0 , R.drawable.phone , 2 , 1 , 3 , 4 , "One plus Nord" , "Rs.49,999/-" ,"Rs.28,999/-"));
//        cartItemModelList.add(new CartItemModel(0 , R.drawable.phone , 2 , 1 , 3 , 4 , "One plus Nord" , "Rs.49,999/-" ,"Rs.28,999/-"));
//        cartItemModelList.add(new CartItemModel(0 , R.drawable.phone , 2 , 1 , 3 , 4 , "One plus Nord" , "Rs.49,999/-" ,"Rs.28,999/-"));
//        cartItemModelList.add(new CartItemModel(0 , R.drawable.phone , 2 , 1 , 3 , 4 , "One plus Nord" , "Rs.49,999/-" ,"Rs.28,999/-"));

//        cartItemModelList.add(new CartItemModel(1 , "5", "Rs.45,000/-" , "Rs.50/-" ,"Rs.8,999/-" , ""));



        CartItemAdapter cartItemAdapter = new CartItemAdapter(cartItemModelList , totalAmountOrderSummery , false);
        orderSummeryRecyclerView.setAdapter(cartItemAdapter);
        cartItemAdapter.notifyDataSetChanged();

        changeOrAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myAddressActivity = new Intent(OrderSummeryActivity.this , MyAddressActivity.class);
                myAddressActivity.putExtra("MODE" , SELECT_ADDRESS);
                startActivity(myAddressActivity);
            }
        });

        continueToCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /////todo paytm integration code
                Intent paymentIntent = new Intent(OrderSummeryActivity.this, PaymentOptions.class);
                startActivity(paymentIntent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        userName = FirebaseQueries.myAddressesModelList.get(FirebaseQueries.selectedAddress).getMyAddressName();
        mobileNumb = FirebaseQueries.myAddressesModelList.get(FirebaseQueries.selectedAddress).getMyAddressMobNo();
        fullName.setText(userName);
        fullAddress.setText(FirebaseQueries.myAddressesModelList.get(FirebaseQueries.selectedAddress).getMyFullAddress());
        mobileNumber.setText(FirebaseQueries.myAddressesModelList.get(FirebaseQueries.selectedAddress).getMyAddressMobNo());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
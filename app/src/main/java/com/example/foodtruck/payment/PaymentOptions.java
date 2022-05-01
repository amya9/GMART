package com.example.foodtruck.payment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.foodtruck.MainActivity;
import com.example.foodtruck.OTPverificationActivity;
import com.example.foodtruck.OrderSummeryActivity;
import com.example.foodtruck.ProductDetailsActivity;
import com.example.foodtruck.R;
import com.google.firebase.auth.FirebaseAuth;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.UUID;

public class PaymentOptions extends AppCompatActivity implements PaymentResultListener {
    private Dialog loadingDialog;
    private LinearLayout payWithPaytm;
    private LinearLayout payWithRazorPay;
    private LinearLayout payWithCOD;
    private TextView codAvailability;
    Integer ActivityRequestCode = 2;
    private static final String TAG = PaymentOptions.class.getSimpleName();
    private ConstraintLayout confirmationLayout;
    private TextView confirmedOrderId;
    private TextView continueShopping;
    private ImageView continueImageView;
    private TextView deliveryDate;

    public  static  String ORDER_ID;
    private boolean successResponse = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_options_layout);
        /*
         To ensure faster loading of the Checkout form,
          call this method as early as possible in your checkout flow.
         */
        Checkout.preload(getApplicationContext());

        ////////////////////////////Loading Dialog

        loadingDialog = new Dialog(PaymentOptions.this);
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(R.layout.loading_details);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.recycler_view_background));
        loadingDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        loadingDialog.show();
        ////////////////////////////Loading Dialog
         ORDER_ID = UUID.randomUUID().toString().substring(0, 28);

        payWithPaytm = findViewById(R.id.pay_with_paytm_ll);
        payWithRazorPay = findViewById(R.id.pay_with_razorpay_ll);
        payWithCOD = findViewById(R.id.pay_with_cod_ll);
        codAvailability = findViewById(R.id.cod_available_tv);

        ////////////////confirmation layout
        confirmationLayout = findViewById(R.id.confirmation_layout);
        confirmedOrderId = findViewById(R.id.confirmation_order_id);
        deliveryDate = findViewById(R.id.confirmation_expected_delivert_date);
        continueShopping = findViewById(R.id.confirmation_continue_shopping_tv);
        continueImageView = findViewById(R.id.confirmation_continue_toHome_iv);
        ////////////////
        payWithPaytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPaymentUsingPaytm();
            }
        });

        payWithRazorPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPaymentUsingRazorPay();
            }
        });

        /////////listener on cod payment option
        payWithCOD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent otpVerificationIntent = new Intent(PaymentOptions.this , OTPverificationActivity.class);
              otpVerificationIntent.putExtra("mobileNumber" , OrderSummeryActivity.mobileNumb.substring(0, 10));
              startActivity(otpVerificationIntent);
            }
        });
    }

    public void startPaymentUsingRazorPay() {
        ////in razorpay we have to convert the entered amount into paise and then pass into jason
        String payAbleAmount = String.valueOf(Integer.parseInt(OrderSummeryActivity.totalAmountOrderSummery.getText().toString().substring(3, OrderSummeryActivity.totalAmountOrderSummery.getText().length() - 2)) * 100);
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        final Activity activity = this;

        final Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_boq6asrhfbymEH");
        try {
            JSONObject options = new JSONObject();
            options.put("name", "G-mart.Inc");
            options.put("description", "Demoing Charges");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", payAbleAmount);

            JSONObject preFill = new JSONObject();
            preFill.put("email", userEmail);
            preFill.put("contact", "8789341233");

            options.put("prefill", preFill);

            checkout.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        try {
            Toast.makeText(this, "Payment Successful: " + s, Toast.LENGTH_SHORT).show();

            successResponse = true;
            if (MainActivity.mainActivity != null) {
                MainActivity.mainActivity.finish();
                MainActivity.mainActivity = null;
                MainActivity.showCart = false;
            }
            if (ProductDetailsActivity.productDetailsActivity != null) {
                ProductDetailsActivity.productDetailsActivity.finish();
                ProductDetailsActivity.productDetailsActivity = null;
            }

            confirmationLayout.setVisibility(View.VISIBLE);
            confirmedOrderId.setText("Order id: " + ORDER_ID);
            continueImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /////////finish all activity when payment is done
                    finish();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        try {
            Toast.makeText(this, "Payment failed: " + i + " " + s, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityRequestCode && data != null) {
            Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();
        }
    }

    public void startPaymentUsingPaytm() {
//                loadingDialog.show();
//                if (ContextCompat.checkSelfPermission(PaymentOptions.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(PaymentOptions.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
//                }
        Toast.makeText(PaymentOptions.this, "Integrate paytm SDK on this", Toast.LENGTH_LONG).show();
        String MERCHANT_ID = "UbbDut08794563911476";

        String CUSTOMER_ID = FirebaseAuth.getInstance().getUid();
        String hostUrl = "https://securegw-stage.paytm.in/";
//        String callback_url = "";
        String payAbleAmount = OrderSummeryActivity.totalAmountOrderSummery.toString().substring(3, OrderSummeryActivity.totalAmountOrderSummery.length() - 2);

        String orderDetails = "MID: " + MERCHANT_ID + ", OrderId: " + ORDER_ID + ", TxnToken: " + CUSTOMER_ID + ", Amount: " + payAbleAmount;
        Toast.makeText(PaymentOptions.this, orderDetails, Toast.LENGTH_SHORT).show();

        String callBackUrl = hostUrl + "theia/paytmCallback?ORDER_ID=" + ORDER_ID;
        PaytmOrder paytmOrder = new PaytmOrder(ORDER_ID, MERCHANT_ID, CUSTOMER_ID, payAbleAmount, callBackUrl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(Bundle bundle) {
                Toast.makeText(PaymentOptions.this, "Response (onTransactionResponse) : " + bundle.toString(), Toast.LENGTH_SHORT).show();
                if (bundle.getString("resultStatus").equals("TXN_SUCCESS")) {

                    if (MainActivity.mainActivity != null) {
                        MainActivity.mainActivity.finish();
                        MainActivity.mainActivity = null;
                        MainActivity.showCart = false;
                    }
                    if (ProductDetailsActivity.productDetailsActivity != null) {
                        ProductDetailsActivity.productDetailsActivity.finish();
                        ProductDetailsActivity.productDetailsActivity = null;
                    }

                    confirmationLayout.setVisibility(View.VISIBLE);
                    confirmedOrderId.setText("Order id: " + bundle.getString("orderId"));
                    continueImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /////////finish all activity when payment is done
                            finish();
                        }
                    });

                }

            }

            @Override
            public void networkNotAvailable() {

            }

            @Override
            public void onErrorProceed(String s) {

            }

            @Override
            public void clientAuthenticationFailed(String s) {

            }

            @Override
            public void someUIErrorOccurred(String s) {

            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {

            }

            @Override
            public void onBackPressedCancelTransaction() {

            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {

            }
        });
        transactionManager.setShowPaymentUrl(hostUrl + "theia/api/v1/showPaymentPage");
        transactionManager.startTransaction(PaymentOptions.this, ActivityRequestCode);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        loadingDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        ///////below code will finish the payment activity when back button is pressed
        if (successResponse){
            finish();
            return;
        }
        super.onBackPressed();
    }


}

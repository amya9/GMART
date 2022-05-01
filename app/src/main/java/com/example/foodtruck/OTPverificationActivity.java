package com.example.foodtruck;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OTPverificationActivity extends AppCompatActivity {
   private TextView otpMobileNumber;
   private EditText enterOtp;
   private Button verifyOtp;
   private String mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        otpMobileNumber = findViewById(R.id.verification_mobileNumber);
        enterOtp = findViewById(R.id.enter_otp);
        verifyOtp = findViewById(R.id.verify_otp_btn);

        mobileNumber = getIntent().getStringExtra("mobileNumber");
        otpMobileNumber.setText("Verification OTP has been sent to mobile number +91 "+ mobileNumber);

        Random random = new Random();
        final int otp_number = random.nextInt(999999 - 111111) + 111111 ;
        String post_url = "https://www.fast2sms.com/dev/bulk";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, post_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                verifyOtp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (enterOtp.getText().toString().equals(String.valueOf(otp_number))){

                            Toast.makeText(OTPverificationActivity.this, "Your order has been placed successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(OTPverificationActivity.this, "Incorrect OTP!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(OTPverificationActivity.this, "Failed to send the OTP code", Toast.LENGTH_SHORT).show();
                finish();
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                headers.put("authorization","zpqFekvUWJ04H3ryZ2SR5xjAdauLOfYVN9CGomt7DXBbcM1P6lUjrGfa0C4tPI2izqWodw7mR1JX9MYN");
                // get authorization key from "https://www.fast2sms.com/dashboard/dev-api"
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> body = new HashMap<>();
                // So abhi iss map ke andar kya kya daalna h wo mujhe ye link se pata chal rha h
                // "https://docs.fast2sms.com/?java#post"

                body.put("sender_id","FSTSMS");
                body.put("language","english");
                body.put("route","qt");
                body.put("numbers", mobileNumber);
                body.put("message","37924"); // ye mujhe yaha se mila "https://www.fast2sms.com/dev/quick-templates?authorization=GKe1b3ug095tYQyiJ627ToFkVl8SzIPLUZBOWRcwsfEap4MDnHWdNxcX4jPh8lmoi7sD9MKRtBunbpAS"
                body.put("variables","{#BB#}"); // ye #BB# matlab apan sirf max 10 characters use kr sakte h
                body.put("variables_values", String.valueOf(otp_number));

                return body;
            }
        };

        // isse multiple times otp send nhi hoga........
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue = Volley.newRequestQueue(OTPverificationActivity.this);
        requestQueue.add(stringRequest);

    }
}
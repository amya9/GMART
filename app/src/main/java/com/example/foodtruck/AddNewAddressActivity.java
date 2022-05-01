package com.example.foodtruck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.foodtruck.firebase.FirebaseQueries;
import com.example.foodtruck.models.MyAddressesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNewAddressActivity extends AppCompatActivity {
    private EditText name;
    private EditText mobileNumber;
    private EditText alternateMobileNumber;
    private EditText pinCode;
    private EditText buildingName;
    private EditText area;
    private EditText city;
    private EditText landmarks;
    private Spinner stateSpinner;

    private String[] stateList;
    private String selectedState;


    private Dialog loadingDialog;

    private Button saveAddressBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Add New Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.add_new_address_name_tv);
        mobileNumber = findViewById(R.id.add_new_address_mobile_number_tv);
        alternateMobileNumber = findViewById(R.id.add_new_address_alternate_mobile_number_tv);
        pinCode = findViewById(R.id.add_new_address_pincode_tv);
        buildingName = findViewById(R.id.add_new_address_house_number_tv);
        area = findViewById(R.id.add_new_address_road_number_tv);
        city = findViewById(R.id.add_new_address_select_city_tv);
        landmarks = findViewById(R.id.add_new_address_landmark_tv);
        stateSpinner = findViewById(R.id.spinner_state_list);

        stateList = getResources().getStringArray(R.array.india_states);
        ////////////////////////////Loading Dialog

        loadingDialog = new Dialog(AddNewAddressActivity.this);
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(R.layout.loading_details);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.recycler_view_background));
        loadingDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        loadingDialog.show();


        ////////////////////////////Loading Dialog

        ArrayAdapter spinnerStateListAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, stateList);
        spinnerStateListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(spinnerStateListAdapter);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedState = stateList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        saveAddressBtn = findViewById(R.id.add_new_address_save_address_tv);

        saveAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(name.getText())) {
                    if (!TextUtils.isEmpty(mobileNumber.getText()) && mobileNumber.getText().length() == 10) {
                        if (!TextUtils.isEmpty(pinCode.getText()) && pinCode.getText().length() == 6) {
                            if (!TextUtils.isEmpty(buildingName.getText())) {
                                if (!TextUtils.isEmpty(area.getText())) {
                                    if (!TextUtils.isEmpty(city.getText())) {
                                        loadingDialog.show();

                                        final String fullAddress = buildingName.getText().toString() + " " + area.getText().toString() + " " + landmarks.getText().toString() + " " + city.getText().toString() + " " + selectedState + "-" + pinCode.getText().toString();

                                        Map<String, Object> addAddress = new HashMap();
                                        addAddress.put("list_size", (long) FirebaseQueries.myAddressesModelList.size() + 1);
                                        addAddress.put("Full_Name_" + String.valueOf((long) FirebaseQueries.myAddressesModelList.size() + 1), name.getText().toString());
                                        if (TextUtils.isEmpty(alternateMobileNumber.getText())) {
                                            addAddress.put("Mobile_Number_" + String.valueOf((long) FirebaseQueries.myAddressesModelList.size() + 1), mobileNumber.getText().toString());
                                        } else {
                                            addAddress.put("Mobile_Number_" + String.valueOf((long) FirebaseQueries.myAddressesModelList.size() + 1), mobileNumber.getText().toString() + " " + alternateMobileNumber.getText().toString());

                                        }
                                        addAddress.put("Address_" + String.valueOf((long) FirebaseQueries.myAddressesModelList.size() + 1), fullAddress);
                                        addAddress.put("pinCode_" + String.valueOf((long) FirebaseQueries.myAddressesModelList.size() + 1), pinCode.getText().toString());
                                        addAddress.put("selected_" + String.valueOf((long) FirebaseQueries.myAddressesModelList.size() + 1), true);
                                        if (FirebaseQueries.myAddressesModelList.size() > 0) {

                                            addAddress.put("selected_" + (FirebaseQueries.selectedAddress + 1), false);
                                        }

                                        FirebaseFirestore.getInstance().collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_DATA")
                                                .document("MY_ADDRESSES")
                                                .update(addAddress)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            if (FirebaseQueries.myAddressesModelList.size() > 0) {
                                                                FirebaseQueries.myAddressesModelList.get(FirebaseQueries.selectedAddress).setSelectedAddress(false);
                                                            }
                                                            if (TextUtils.isEmpty(alternateMobileNumber.getText())) {
                                                                FirebaseQueries.myAddressesModelList.add(new MyAddressesModel(name.getText().toString() + "-" + mobileNumber.getText().toString(), fullAddress, mobileNumber.getText().toString(), true, pinCode.getText().toString()));
                                                            } else {
                                                                FirebaseQueries.myAddressesModelList.add(new MyAddressesModel(name.getText().toString() + "-" + mobileNumber.getText().toString() + " " + alternateMobileNumber.getText().toString(), fullAddress, mobileNumber.getText().toString(), true, pinCode.getText().toString()));

                                                            }
//
                                                            if (getIntent().getStringExtra("INTENT").equals("orderSummeryIntent")) {
                                                                Intent deliveryIntent = new Intent(AddNewAddressActivity.this, OrderSummeryActivity.class);
                                                                startActivity(deliveryIntent);
                                                            } else {
                                                                MyAddressActivity.refreshItem(FirebaseQueries.selectedAddress, FirebaseQueries.myAddressesModelList.size() - 1);
                                                            }
                                                            FirebaseQueries.selectedAddress = FirebaseQueries.myAddressesModelList.size() - 1;

                                                            finish();

                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(AddNewAddressActivity.this, error, Toast.LENGTH_LONG).show();
                                                        }
                                                        loadingDialog.dismiss();
                                                    }

                                                });
                                    } else {
                                        city.requestFocus();
                                    }

                                } else {
                                    area.requestFocus();
                                }

                            } else {
                                buildingName.requestFocus();
                            }

                        } else {
                            pinCode.requestFocus();
                            Toast.makeText(AddNewAddressActivity.this, "Please provide valid pinCode", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        mobileNumber.requestFocus();
                        Toast.makeText(AddNewAddressActivity.this, "Please provide valid number", Toast.LENGTH_LONG).show();
                    }

                } else {
                    name.requestFocus();
                }

            }

        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
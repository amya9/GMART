package com.example.foodtruck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodtruck.adapters.MyAddressesAdapter;
import com.example.foodtruck.firebase.FirebaseQueries;
import com.example.foodtruck.models.MyAddressesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.foodtruck.OrderSummeryActivity.SELECT_ADDRESS;

public class MyAddressActivity extends AppCompatActivity {
    private LinearLayout addNewAddress;
    private RecyclerView savedAddressesRecyclerView;
    private Button deliverHere;
    public static MyAddressesAdapter myAddressesAdapter;
    private TextView numberOfAddressesSaved;
    private int previousSelectedAddressState;
    private Dialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Select Address (9)");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ////////////////////////////Loading Dialog

        loadingDialog = new Dialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(R.layout.loading_details);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.recycler_view_background));
        loadingDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        loadingDialog.show();
        ////////////////////////////Loading Dialog
////////get previous selected address position from firebase queries
        previousSelectedAddressState = FirebaseQueries.selectedAddress;

        deliverHere = findViewById(R.id.deliver_here_btn);
        deliverHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseQueries.selectedAddress != previousSelectedAddressState) {
                    final int previousAddressIndex = previousSelectedAddressState;
                    loadingDialog.show();
                    Map<String, Object> updateStateOfSelectedAddress = new HashMap<>();
                    updateStateOfSelectedAddress.put("selected_" + String.valueOf(previousSelectedAddressState + 1), false);
                    updateStateOfSelectedAddress.put("selected_" + String.valueOf(FirebaseQueries.selectedAddress + 1), true);

                    previousSelectedAddressState = FirebaseQueries.selectedAddress;

                    FirebaseFirestore.getInstance().collection("USERS")
                            .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                            .collection("USER_DATA")
                            .document("MY_ADDRESSES")
                            .update(updateStateOfSelectedAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                finish();
                            } else {
                                previousSelectedAddressState = previousAddressIndex;
                                String error = task.getException().getMessage();
                                Toast.makeText(MyAddressActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
                } else {
                    finish();
                }
            }
        });
        savedAddressesRecyclerView = findViewById(R.id.saved_address_recycler_view);
        addNewAddress = findViewById(R.id.add_new_address_linearLayout);
        addNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNewAddressIntent = new Intent(MyAddressActivity.this, AddNewAddressActivity.class);
                addNewAddressIntent.putExtra("INTENT", "null");
                startActivity(addNewAddressIntent);
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        savedAddressesRecyclerView.setLayoutManager(layoutManager);
//        List<MyAddressesModel> myAddressesModelList = new ArrayList<>();
//        myAddressesModelList.add(new MyAddressesModel("Amit kumar" , "Bhawanipur , purainiya , darpa thana , near narkatiya bazae , east champaran , bihar , 845301" , "8789341233" , true));
//        myAddressesModelList.add(new MyAddressesModel("Sumit kumar" , "Bhawanipur , purainiya , darpa thana , near narkatiya bazae , east champaran , bihar , 845301" , "8789341233" , false));
//        myAddressesModelList.add(new MyAddressesModel("Amit kumar" , "Bhawanipur , purainiya , darpa thana , near narkatiya bazae , east champaran , bihar , 845301" , "8789341233", false));
//        myAddressesModelList.add(new MyAddressesModel("Amit kumar" , "Bhawanipur , purainiya , darpa thana , near narkatiya bazae , east champaran , bihar , 845301" , "8789341233" , false));
//        myAddressesModelList.add(new MyAddressesModel("Amit kumar" , "Bhawanipur , purainiya , darpa thana , near narkatiya bazae , east champaran , bihar , 845301" , "8789341233" , false));
//        myAddressesModelList.add(new MyAddressesModel("Amit kumar" , "Bhawanipur , purainiya , darpa thana , near narkatiya bazae , east champaran , bihar , 845301" , "8789341233" , false));
//        myAddressesModelList.add(new MyAddressesModel("Amit kumar" , "Bhawanipur , purainiya , darpa thana , near narkatiya bazae , east champaran , bihar , 845301" , "8789341233" , false));
//        myAddressesModelList.add(new MyAddressesModel("Amit kumar" , "Bhawanipur , purainiya , darpa thana , near narkatiya bazae , east champaran , bihar , 845301" , "8789341233" , false));
//        myAddressesModelList.add(new MyAddressesModel("Amit kumar" , "Bhawanipur , purainiya , darpa thana , near narkatiya bazae , east champaran , bihar , 845301" , "8789341233" , false));

        int mode = getIntent().getIntExtra("MODE", -1);
        if (mode == SELECT_ADDRESS) {
            deliverHere.setVisibility(View.VISIBLE);
        } else {
            deliverHere.setVisibility(View.GONE);
        }
        myAddressesAdapter = new MyAddressesAdapter(FirebaseQueries.myAddressesModelList, mode);
        savedAddressesRecyclerView.setAdapter(myAddressesAdapter);
        ((SimpleItemAnimator) savedAddressesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        myAddressesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        numberOfAddressesSaved = findViewById(R.id.saved_number_of_address);
        if ((long) FirebaseQueries.myAddressesModelList.size() > 1) {
            numberOfAddressesSaved.setText(FirebaseQueries.myAddressesModelList.size() + " addresses saved");
        } else {
            numberOfAddressesSaved.setText(FirebaseQueries.myAddressesModelList.size() + " address saved");
        }
    }

    public static void refreshItem(int preSelectedPosition, int position) {
        myAddressesAdapter.notifyItemChanged(preSelectedPosition);
        myAddressesAdapter.notifyItemChanged(position);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (FirebaseQueries.selectedAddress != previousSelectedAddressState) {
                FirebaseQueries.myAddressesModelList.get(FirebaseQueries.selectedAddress).setSelectedAddress(false);
                FirebaseQueries.myAddressesModelList.get(previousSelectedAddressState).setSelectedAddress(true);
                FirebaseQueries.selectedAddress = previousSelectedAddressState;
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (FirebaseQueries.selectedAddress != previousSelectedAddressState) {
            FirebaseQueries.myAddressesModelList.get(FirebaseQueries.selectedAddress).setSelectedAddress(false);
            FirebaseQueries.myAddressesModelList.get(previousSelectedAddressState).setSelectedAddress(true);
            FirebaseQueries.selectedAddress = previousSelectedAddressState;
        }
        super.onBackPressed();
    }
}
package com.example.foodtruck.ProductDetailsFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.foodtruck.R;

public class ProductDescriptionFragment extends Fragment {
    private TextView descriptionContainer;
    public static String desc;


    public ProductDescriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view =  inflater.inflate(R.layout.fragment_product_description, container, false);
        descriptionContainer = view.findViewById(R.id.tv_product_description);
     descriptionContainer.setText(desc);
        return view;
    }
}
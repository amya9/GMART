package com.example.foodtruck.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.foodtruck.ProductDetailsFragments.ProductDescriptionFragment;
import com.example.foodtruck.ProductDetailsFragments.ProductSpecificationFragment;
import com.example.foodtruck.models.ProductSpecificationFeaturesModel;

import java.util.List;

public class productDescriptionViewPagerAdapter extends FragmentPagerAdapter {
    private int tabCount;
    private String productDescription;
    private String otherDetails;
    private List<ProductSpecificationFeaturesModel>productSpecificationFeaturesModelList;

    public productDescriptionViewPagerAdapter(@NonNull FragmentManager fm, int tabCount, String productDescription, String otherDetails, List<ProductSpecificationFeaturesModel> productSpecificationFeaturesModelList) {
        super(fm);
        this.tabCount = tabCount;
        this.productDescription = productDescription;
        this.otherDetails = otherDetails;
        this.productSpecificationFeaturesModelList = productSpecificationFeaturesModelList;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ProductDescriptionFragment productDescriptionFragment1 = new ProductDescriptionFragment();
                ProductDescriptionFragment.desc = productDescription;
                return productDescriptionFragment1;
            case 1:
                ProductSpecificationFragment productSpecificationFragment = new ProductSpecificationFragment();
                ProductSpecificationFragment.productSpecificationFeaturesModelList = productSpecificationFeaturesModelList;
                return  productSpecificationFragment;
            case 2:
                ProductDescriptionFragment productDescriptionFragment2 = new ProductDescriptionFragment();
                ProductDescriptionFragment.desc = otherDetails;
                return productDescriptionFragment2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}

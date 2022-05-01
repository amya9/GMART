package com.example.foodtruck.ui.home;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.foodtruck.MainActivity;
import com.example.foodtruck.R;
import com.example.foodtruck.adapters.CategoryAdapter;
import com.example.foodtruck.adapters.HomePageAdapter;
import com.example.foodtruck.models.CategoryModel;
import com.example.foodtruck.models.HomePageModel;
import com.example.foodtruck.models.HorizontalProductScrollModel;
import com.example.foodtruck.models.MyWishListModel;
import com.example.foodtruck.models.SliderModel;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.foodtruck.firebase.FirebaseQueries.categoryModelList;

import static com.example.foodtruck.firebase.FirebaseQueries.loadCategoriesData;
import static com.example.foodtruck.firebase.FirebaseQueries.loadHomeFragmentView;
import static com.example.foodtruck.firebase.FirebaseQueries.loadedListName;
import static com.example.foodtruck.firebase.FirebaseQueries.mainLists;
import static com.example.foodtruck.firebase.FirebaseQueries.sliderModelList;

public class HomeFragment extends Fragment {

    private Button retryBtn;

    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ImageView noInternetConnection;
    private GridView myGridView;
    private HomePageAdapter homePageAdapter;
    public static SwipeRefreshLayout swipeRefreshLayout;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private List<CategoryModel> fakeCategoryModelList = new ArrayList<>();
    private List<HomePageModel> fakeHomePageModelLit = new ArrayList<>();
    private List<SliderModel> fakeSliderModelList = new ArrayList<>();
    private List<HorizontalProductScrollModel> fakeHorizontalModelList = new ArrayList<>();
    private RecyclerView homeContainerRecyclerView;

//    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        noInternetConnection = root.findViewById(R.id.no_internet_connection);
        categoryRecyclerView = root.findViewById(R.id.category_recyclerView);
        retryBtn = root.findViewById(R.id.retry_btn);
        homeContainerRecyclerView = root.findViewById(R.id.home_fragment_container_recyclerView);
        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.colorPrimary)
                , getContext().getResources().getColor(R.color.resetBtn)
                , getContext().getResources().getColor(R.color.forgot_password));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(layoutManager);

        fakeCategoryModelList.add(new CategoryModel("null", ""));
        fakeCategoryModelList.add(new CategoryModel("", ""));
        fakeCategoryModelList.add(new CategoryModel("", ""));
        fakeCategoryModelList.add(new CategoryModel("", ""));
        fakeCategoryModelList.add(new CategoryModel("", ""));
        fakeCategoryModelList.add(new CategoryModel("", ""));
        fakeCategoryModelList.add(new CategoryModel("", ""));
        fakeCategoryModelList.add(new CategoryModel("", ""));
        fakeCategoryModelList.add(new CategoryModel("", ""));
        fakeCategoryModelList.add(new CategoryModel("", ""));

        LinearLayoutManager testingLayout = new LinearLayoutManager(getContext());
        testingLayout.setOrientation(LinearLayoutManager.VERTICAL);
        homeContainerRecyclerView.setLayoutManager(testingLayout);
//        homeContainerRecyclerView.setAdapter(homePageAdapter);
        //////////////////////////homepage list

        fakeSliderModelList.add(new SliderModel("null", "#dfdfdf"));
        fakeSliderModelList.add(new SliderModel("null", "#dfdfdf"));
        fakeSliderModelList.add(new SliderModel("null", "#dfdfdf"));
        fakeSliderModelList.add(new SliderModel("null", "#dfdfdf"));
        fakeSliderModelList.add(new SliderModel("null", "#dfdfdf"));
        fakeSliderModelList.add(new SliderModel("null", "#dfdfdf"));


        fakeHorizontalModelList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        fakeHorizontalModelList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        fakeHorizontalModelList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        fakeHorizontalModelList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        fakeHorizontalModelList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        fakeHorizontalModelList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        fakeHorizontalModelList.add(new HorizontalProductScrollModel("", "", "", "", ""));

        fakeHomePageModelLit.add(new HomePageModel(0, fakeSliderModelList));
        fakeHomePageModelLit.add(new HomePageModel(1, "", "#dfdfdf"));
        fakeHomePageModelLit.add(new HomePageModel(2, "#dfdfdf", " ", fakeHorizontalModelList));
        fakeHomePageModelLit.add(new HomePageModel(3, "#dfdfdf", " ", fakeHorizontalModelList, new ArrayList<MyWishListModel>()));

        //////////////////////////homepage list

        categoryAdapter = new CategoryAdapter(fakeCategoryModelList);
//        categoryRecyclerView.setAdapter(categoryAdapter);
        homePageAdapter = new HomePageAdapter(fakeHomePageModelLit);
//        homeContainerRecyclerView.setAdapter(homePageAdapter);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            noInternetConnection.setVisibility(View.GONE);
            myGridView = root.findViewById(R.id.grid_product_container);
            categoryRecyclerView.setVisibility(View.VISIBLE);
            homeContainerRecyclerView.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.GONE);
            ////////////////////////////////categories item

            if (categoryModelList.size() == 0) {
                loadCategoriesData(categoryRecyclerView, root.getContext());
            } else {
                categoryAdapter = new CategoryAdapter(categoryModelList);
                categoryAdapter.notifyDataSetChanged();
            }
            categoryRecyclerView.setAdapter(categoryAdapter);
            ////////////////////////////////categories item

            /////////////////////load home fragment view
            if (mainLists.size() == 0) {
                loadedListName.add("Home");
                mainLists.add(new ArrayList<HomePageModel>());
            } else {
                loadHomeFragmentView(homeContainerRecyclerView, root.getContext(), 0, "Home");
//                homePageAdapter = new HomePageAdapter(mainLists.get(0));
                homePageAdapter.notifyDataSetChanged();
            }
            homeContainerRecyclerView.setAdapter(homePageAdapter);
        } else {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            categoryRecyclerView.setVisibility(View.GONE);
            homeContainerRecyclerView.setVisibility(View.GONE);
            Glide.with(root.getContext()).load(R.drawable.no_internet_connection_b).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadPage();
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPage();
            }
        });
        return root;
    }

    private void reloadPage() {
        networkInfo = connectivityManager.getActiveNetworkInfo();
        mainLists.clear();
        categoryModelList.clear();
        loadedListName.clear();
        if (networkInfo != null && networkInfo.isConnected()) {
            noInternetConnection.setVisibility(View.GONE);

            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            categoryRecyclerView.setVisibility(View.VISIBLE);
            homeContainerRecyclerView.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.GONE);

            categoryAdapter = new CategoryAdapter(fakeCategoryModelList);
            categoryRecyclerView.setAdapter(categoryAdapter);
            homePageAdapter = new HomePageAdapter(fakeHomePageModelLit);
            homeContainerRecyclerView.setAdapter(homePageAdapter);

            loadCategoriesData(categoryRecyclerView, getContext());
            loadedListName.add("Home");
            mainLists.add(new ArrayList<HomePageModel>());
            homePageAdapter = new HomePageAdapter(mainLists.get(0));
            loadHomeFragmentView(homeContainerRecyclerView, getContext(), 0, "Home");

        } else {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            retryBtn.setVisibility(View.VISIBLE);
            Toast.makeText(requireView().getContext(), "No internet Connection", Toast.LENGTH_LONG).show();
            categoryRecyclerView.setVisibility(View.GONE);
            homeContainerRecyclerView.setVisibility(View.GONE);
            noInternetConnection.setVisibility(View.VISIBLE);
            Glide.with(requireView().getContext()).load(R.drawable.no_internet_connection_b).into(noInternetConnection);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
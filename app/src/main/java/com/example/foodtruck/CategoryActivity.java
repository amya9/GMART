package com.example.foodtruck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.foodtruck.adapters.HomePageAdapter;
import com.example.foodtruck.models.HomePageModel;
import com.example.foodtruck.models.HorizontalProductScrollModel;
import com.example.foodtruck.models.MyWishListModel;
import com.example.foodtruck.models.SliderModel;

import java.util.ArrayList;
import java.util.List;

import static com.example.foodtruck.firebase.FirebaseQueries.loadHomeFragmentView;
import static com.example.foodtruck.firebase.FirebaseQueries.loadedListName;
import static com.example.foodtruck.firebase.FirebaseQueries.mainLists;

public class CategoryActivity extends AppCompatActivity {
    private RecyclerView categoryRecyclerView;
    private HomePageAdapter homePageAdapter;
    private List<HomePageModel> fakeHomePageModelLit = new ArrayList<>();
    private List<SliderModel> fakeSliderModelList = new ArrayList<>();
    private  List<HorizontalProductScrollModel>  fakeHorizontalModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String categoryTitle = getIntent().getStringExtra("categoryTitle");
        getSupportActionBar().setTitle(categoryTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        categoryRecyclerView = findViewById(R.id.category_activity_recyclerView);
        //////////////////////////homepage list

        fakeSliderModelList.add(new SliderModel("null" , "#00ffff"));
        fakeSliderModelList.add(new SliderModel("null" , "#00ffff"));
        fakeSliderModelList.add(new SliderModel("null" , "#00ffff"));
        fakeSliderModelList.add(new SliderModel("null" , "#00ffff"));
        fakeSliderModelList.add(new SliderModel("null" , "#00ffff"));
        fakeSliderModelList.add(new SliderModel("null" , "#00ffff"));


        fakeHorizontalModelList.add(new HorizontalProductScrollModel("" , "" ,"","" ,"" ));
        fakeHorizontalModelList.add(new HorizontalProductScrollModel("" , "" ,"","" ,"" ));
        fakeHorizontalModelList.add(new HorizontalProductScrollModel("" , "" ,"","" ,"" ));
        fakeHorizontalModelList.add(new HorizontalProductScrollModel("" , "" ,"","" ,"" ));
        fakeHorizontalModelList.add(new HorizontalProductScrollModel("" , "" ,"","" ,"" ));
        fakeHorizontalModelList.add(new HorizontalProductScrollModel("" , "" ,"","" ,"" ));
        fakeHorizontalModelList.add(new HorizontalProductScrollModel("" , "" ,"","" ,"" ));

        fakeHomePageModelLit.add(new HomePageModel(0 , fakeSliderModelList));
        fakeHomePageModelLit.add(new HomePageModel(1, "" , "#ffffff"));
        fakeHomePageModelLit.add(new HomePageModel(2 , "#ffffff" , " " , fakeHorizontalModelList));
        fakeHomePageModelLit.add(new HomePageModel(3 , "#ffffff" , " " , fakeHorizontalModelList , new ArrayList<MyWishListModel>()));

        //////////////////////////homepage list
        /////////////////banner slider

        LinearLayoutManager testingLayout = new LinearLayoutManager(this);
        testingLayout.setOrientation(LinearLayoutManager.VERTICAL);
        categoryRecyclerView.setLayoutManager(testingLayout);

        homePageAdapter = new HomePageAdapter(fakeHomePageModelLit);

        int listPosition = 0;
        for (int x = 0; x < loadedListName.size(); x++) {
            if (loadedListName.get(x).equals(categoryTitle)) {
                listPosition = x;
            }
        }if (listPosition == 0){
            loadedListName.add(categoryTitle);
            mainLists.add(new ArrayList<HomePageModel>());

            loadHomeFragmentView(categoryRecyclerView,this, loadedListName.size()-1 , categoryTitle) ;
        }else {
            homePageAdapter = new HomePageAdapter(mainLists.get(listPosition));
        }


        categoryRecyclerView.setAdapter(homePageAdapter);
        homePageAdapter.notifyDataSetChanged();

        ///////////////////////////////////////////////////////////////////////

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_item, menu);
        return true;
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
package com.alitali.sample;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.alitali.talibottomnavigation.TaliBottomNavigationView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LiveData<NavController> currentNavController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            setupBottomNavigationBar();
        }
    }
    private void setupBottomNavigationBar(){
        TaliBottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        //bottomNavigationView.setSelectedItemId(R.id.home);
        List<Integer> navGraphIds = Arrays.asList(R.navigation.setting,R.navigation.home,R.navigation.about);
        LiveData<NavController> controller = bottomNavigationView.setupWithNavController(navGraphIds,
                getSupportFragmentManager(),
                R.id.nav_host_container,
                getIntent());
        controller.observe(this, this::setupActionBarWithNavController);
        currentNavController = controller;
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setupBottomNavigationBar();
    }
    protected void setupActionBarWithNavController(NavController navController){
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, configuration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return currentNavController.getValue() != null && currentNavController.getValue().navigateUp();
    }
}
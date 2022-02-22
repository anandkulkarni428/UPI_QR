package com.anand.upiqr.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;

import com.anand.upiqr.Fragments.HistoryFragment;
import com.anand.upiqr.Fragments.HomeFragment;
import com.anand.upiqr.Fragments.ProfileFragment;
import com.anand.upiqr.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    HomeFragment homeFragment = new HomeFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, homeFragment).commit();
                return true;
            case R.id.history:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, historyFragment).commit();
                return true;

            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, profileFragment).commit();
                return true;
        }
        return false;
    }
}

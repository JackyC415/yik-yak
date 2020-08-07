package com.cbsjchen0601;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MiscActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.YakPost:
                            Intent yakIntent = new Intent(MiscActivity.this, MainActivity.class);
                            startActivity(yakIntent);
                            return true;
                        case R.id.GPS:
                            Intent gpsIntent = new Intent(MiscActivity.this, LocationActivity.class);
                            startActivity(gpsIntent);
                            return true;
                        case R.id.Notifcation:
                            break;
                    }
                    return false;
                }
            };

}
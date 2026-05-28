package com.s224776282.a91lostandfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCreateAdvert = findViewById(R.id.btnCreateAdvert);
        Button btnShowItems = findViewById(R.id.btnShowItems);
        Button btnShowMap = findViewById(R.id.btnShowMap);

        // Open the Create Advert screen
        btnCreateAdvert.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateAdvertActivity.class);
            startActivity(intent);
        });

        // Open the List Items screen
        btnShowItems.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListItemsActivity.class);
            startActivity(intent);
        });

        // Open the Map Activity
        btnShowMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });
    }
}
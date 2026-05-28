package com.s224776282.a91lostandfoundapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListItemsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private Spinner spinnerFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewItems);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the string that the user just clicked
                String selectedCategory = parent.getItemAtPosition(position).toString();

                // Reload the list with that filter
                loadItems(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default to showing everything if nothing is selected
                loadItems("All");
            }
        });
    }

    // Refresh list
    @Override
    protected void onResume() {
        super.onResume();

        // Grab whatever is currently selected in the spinner and reload
        if (spinnerFilter != null && spinnerFilter.getSelectedItem() != null) {
            loadItems(spinnerFilter.getSelectedItem().toString());
        } else {
            loadItems("All");
        }
    }

    private void loadItems(String filter) {
        List<Item> itemList = dbHelper.getItems(filter);
        adapter = new ItemAdapter(this, itemList);
        recyclerView.setAdapter(adapter);
    }
}
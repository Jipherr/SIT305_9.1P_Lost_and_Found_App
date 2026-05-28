package com.s224776282.a91lostandfoundapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ItemDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private Item currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        dbHelper = new DatabaseHelper(this);

        TextView detailTitle = findViewById(R.id.detailTitle);
        TextView detailTimeAgo = findViewById(R.id.detailTimeAgo);
        TextView detailInfo = findViewById(R.id.detailInfo);
        Button btnRemoveAdvert = findViewById(R.id.btnRemoveAdvert);

        currentItem = (Item) getIntent().getSerializableExtra("ITEM_DATA");

        if (currentItem != null) {
            detailTitle.setText(currentItem.getPostType() + " " + currentItem.getDescription());

            String timeAgo = calculateTimeAgo(currentItem.getTimestamp());
            detailTimeAgo.setText(timeAgo);

            String fullDetails =
                    "Name: " + currentItem.getName() + "\n" +
                            "Phone: " + currentItem.getPhone() + "\n" +
                            "Date Lost/Found: " + currentItem.getDate() + "\n" +
                            "Location: " + currentItem.getLocation() + "\n" +
                            "Category: " + currentItem.getCategory();

            detailInfo.setText(fullDetails);
        }

        btnRemoveAdvert.setOnClickListener(v -> {
            if (currentItem != null) {
                dbHelper.deleteItem(currentItem.getId());
                Toast.makeText(this, "Advert Removed", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // Method to calculate how long ago an item was posted
    private String calculateTimeAgo(long pastTime) {
        long diff = System.currentTimeMillis() - pastTime;
        long minutes = diff / (1000 * 60);
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + " days ago";
        if (hours > 0) return hours + " hours ago";
        if (minutes > 0) return minutes + " minutes ago";
        return "Just now";
    }
}
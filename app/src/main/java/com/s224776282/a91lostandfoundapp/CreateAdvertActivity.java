package com.s224776282.a91lostandfoundapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.location.Address;
import android.location.Geocoder;
import java.io.IOException;
import java.util.Locale;

import java.util.Calendar;

public class CreateAdvertActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private Uri selectedImageUri = null;
    private TextView txtImageStatus;
    private long selectedTimestamp = 0;

    private double currentLat = 0.0;
    private double currentLng = 0.0;
    private EditText editLocation;
    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    txtImageStatus.setText("Image Selected!");
                    txtImageStatus.setTextColor(getColor(android.R.color.holo_green_dark));
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    fetchCurrentLocation();
                } else {
                    Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        dbHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        RadioGroup radioGroupType = findViewById(R.id.radioGroupType);
        EditText editName = findViewById(R.id.editName);
        EditText editPhone = findViewById(R.id.editPhone);
        EditText editDescription = findViewById(R.id.editDescription);
        EditText editDate = findViewById(R.id.editDate);
        editLocation = findViewById(R.id.editLocation);
        Button btnCurrentLocation = findViewById(R.id.btnCurrentLocation);
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        Button btnUploadImage = findViewById(R.id.btnUploadImage);
        txtImageStatus = findViewById(R.id.txtImageStatus);
        Button btnSave = findViewById(R.id.btnSave);

        editDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    CreateAdvertActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Display the date as a readable string
                        String dateString = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        editDate.setText(dateString);

                        // Converts date to time in millis for calculation
                        Calendar selectedCal = Calendar.getInstance();
                        selectedCal.set(selectedYear, selectedMonth, selectedDay);
                        selectedTimestamp = selectedCal.getTimeInMillis();
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        btnUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnCurrentLocation.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                fetchCurrentLocation();
            }
        });

        btnSave.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String description = editDescription.getText().toString().trim();
            String date = editDate.getText().toString().trim();
            String location = editLocation.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();

            int selectedId = radioGroupType.getCheckedRadioButtonId();
            RadioButton selectedRadio = findViewById(selectedId);
            String postType = selectedRadio.getText().toString();

            if (description.isEmpty() || name.isEmpty() || phone.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill in all text fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedImageUri == null) {
                Toast.makeText(this, "Image upload is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Fallback just in case something went weird
            if (selectedTimestamp == 0) {
                selectedTimestamp = System.currentTimeMillis();
            }

            Item newItem = new Item();
            newItem.setPostType(postType);
            newItem.setName(name);
            newItem.setPhone(phone);
            newItem.setDescription(description);
            newItem.setDate(date);
            newItem.setLocation(location);
            newItem.setLatitude(currentLat);
            newItem.setLongitude(currentLng);
            newItem.setCategory(category);
            newItem.setImageUri(selectedImageUri.toString());

            newItem.setTimestamp(selectedTimestamp);

            long result = dbHelper.insertItem(newItem);

            if (result != -1) {
                Toast.makeText(this, "Advert Saved Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error saving advert", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLng = location.getLongitude();

                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    try {
                        java.util.List<Address> addresses = geocoder.getFromLocation(currentLat, currentLng, 1);

                        if (addresses != null && !addresses.isEmpty()) {
                            String addressLine = addresses.get(0).getAddressLine(0);
                            editLocation.setText(addressLine);
                        } else {
                            editLocation.setText("Location: " + currentLat + ", " + currentLng);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        editLocation.setText("Location: " + currentLat + ", " + currentLng);
                    }

                    Toast.makeText(this, "Location Captured!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to get location. Ensure GPS is on.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
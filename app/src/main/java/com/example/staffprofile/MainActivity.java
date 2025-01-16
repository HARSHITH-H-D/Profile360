
package com.example.staffprofile;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_MANAGE_ALL_FILES = 1;
    private ListView listViewProfiles;
    private EditText searchBar;  // Reference for the search bar
    private DatabaseHelper databaseHelper;
    private ArrayList<String> profileList; // List to store profile names
    private ArrayList<String> filteredProfileList; // List to store filtered profile names
    private ArrayList<String> employeeIds; // List to store employee IDs
    private ArrayList<String> filteredEmployeeIds; // List to store filtered employee IDs
    private ArrayAdapter<String> adapter;
    private Button buttonAddProfile; // Button to add a new profile
    private static final int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewProfiles = findViewById(R.id.listViewProfiles);
        searchBar = findViewById(R.id.searchBar); // Reference to the search bar
        buttonAddProfile = findViewById(R.id.buttonAddProfile); // Reference to the button in the layout
        databaseHelper = new DatabaseHelper(this);
        profileList = new ArrayList<>();
        employeeIds = new ArrayList<>();
        filteredProfileList = new ArrayList<>();
        filteredEmployeeIds = new ArrayList<>();

        // Load data from Firebase database and display it in ListView
        loadProfileData();

        // Set up the adapter for ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredProfileList);
        listViewProfiles.setAdapter(adapter);

        // Handle list item click to view the selected profile
        listViewProfiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get employee ID of the clicked item
                String employeeId = filteredEmployeeIds.get(position);

                // Start ViewProfileActivity to show selected profile details
                Intent intent = new Intent(MainActivity.this, ViewProfileActivity.class);
                intent.putExtra("employeeId", employeeId); // Pass the employeeId
                startActivity(intent);
            }
        });

        // Set an OnClickListener for buttonAddProfile to open AddProfileActivity
        buttonAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start AddProfileActivity when the button is clicked
                Intent intent = new Intent(MainActivity.this, AddProfileActivity.class);
                startActivity(intent);
            }
        });

        // Add a TextWatcher to filter profiles based on search query
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProfiles(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text changes
            }
        });
        createNotificationChannel();
    }

    // Method to load profiles from Firebase database and populate the ListView
    private void loadProfileData() {
        databaseHelper.getAllProfiles(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    profileList.clear();  // Clear old data
                    employeeIds.clear();
                    filteredProfileList.clear();
                    filteredEmployeeIds.clear();

                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        String employeeId = snapshot.getKey();
                        HashMap<String, Object> employeeData = (HashMap<String, Object>) snapshot.getValue();

                        if (employeeId != null && employeeData != null) {
                            String name = (String) employeeData.get("name");

                            // Add the name and ID to their respective lists
                            profileList.add(name);
                            employeeIds.add(employeeId);
                        }
                    }

                    // Copy original data to filtered lists and notify adapter
                    filteredProfileList.addAll(profileList);
                    filteredEmployeeIds.addAll(employeeIds);
                    adapter.notifyDataSetChanged();

                    if (profileList.isEmpty()) {
                        // Show message if no profiles are found
                        Toast.makeText(MainActivity.this, "No profiles found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Show an error message if fetching profiles failed
                    Toast.makeText(MainActivity.this, "Error loading profiles.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @SuppressLint("NewApi")
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_MANAGE_ALL_FILES);
            }
            String channelId = "profile_notification_channel";
            CharSequence channelName = "Profile Notifications";
            String description = "Notification channel for new profiles";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to filter profiles based on search query
    private void filterProfiles(String query) {
        filteredProfileList.clear();
        filteredEmployeeIds.clear();

        for (int i = 0; i < profileList.size(); i++) {
            if (profileList.get(i).toLowerCase().contains(query.toLowerCase())) {
                filteredProfileList.add(profileList.get(i));
                filteredEmployeeIds.add(employeeIds.get(i));
            }
        }

        adapter.notifyDataSetChanged(); // Refresh the ListView with filtered data
    }
}

package com.example.smartirrigationsystem.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartirrigationsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Home extends Fragment {

    private TextView temperatureTextView;
    private TextView humidityTextView;
    private TextView soilHumidityTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize TextViews
        temperatureTextView = rootView.findViewById(R.id.temperatureTextView);
        humidityTextView = rootView.findViewById(R.id.humidityTextView);
        soilHumidityTextView = rootView.findViewById(R.id.soilHumidityTextView);

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Reference to the user's data
            DatabaseReference userRef = mDatabase.child("Users").child(userId).child("dataPlant");

            // Query to get the last entry
            Query lastEntryQuery = userRef.orderByKey().limitToLast(1);

            // Attach a listener to read the data at our user reference
            lastEntryQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.exists()) {
                            Long humidity = snapshot.child("humidity").getValue(Long.class);
                            Long soilHumidity = snapshot.child("soilHumidity").getValue(Long.class);
                            Long temperature = snapshot.child("temperature").getValue(Long.class);

                            if (humidity != null) {
                                humidityTextView.setText( humidity + "%");
                            }

                            if (soilHumidity != null) {
                                soilHumidityTextView.setText( soilHumidity + "%");
                            }

                            if (temperature != null) {
                                temperatureTextView.setText( temperature + "°C");
                            }
                        } else {
                            Toast.makeText(getActivity(), "No data available", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting data failed, log a message
                    Toast.makeText(getActivity(), "Failed to load data.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return rootView;
    }
}

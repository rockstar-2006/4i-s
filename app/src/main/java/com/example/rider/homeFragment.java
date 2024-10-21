package com.example.rider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class homeFragment extends Fragment {

    private Button emergency_button;
    private static final int SMS_PERMISSION_CODE = 101;
    private static final int LOCATION_PERMISSION_CODE = 102;
    private static final String EMERGENCY_NUMBER = "+916363428833"; // Add your emergency contact number
    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Initialize the button
        emergency_button = view.findViewById(R.id.emergency_button);

        // Load the animation
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.vibrate_animation);
        emergency_button.startAnimation(fadeInAnimation);

        // Set up button click listener
        emergency_button.setOnClickListener(v -> {
            // Show confirmation dialog
            showCustomDialog();
        });
    }

    // Show a confirmation dialog before sending the SMS
    // Show a confirmation dialog before sending the SMS
    private void showCustomDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_custom_confirm, null); // Replace with your layout file name

        // Create AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        // Initialize UI components from custom layout
        Button btnOkay = dialogView.findViewById(R.id.btn_okay);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Set click listeners for buttons
        btnOkay.setOnClickListener(v -> {
            // Handle okay button click
            Toast.makeText(getContext(), "Confirmed", Toast.LENGTH_SHORT).show();
            dialog.dismiss();

            // Call the method to get the current location and send SMS
            getCurrentLocationAndSendSms();
        });

        btnCancel.setOnClickListener(v -> {
            // Handle cancel button click
            Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }



    // Check for SMS permission
    private boolean checkSmsPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    // Check for Location permission
    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // Request both SMS and Location permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION},
                SMS_PERMISSION_CODE);
    }

    // Retrieve the current location and send the SMS
    private void getCurrentLocationAndSendSms() {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if they are not granted
            requestPermissions();
            return;
        }

        // Get the last known location
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                // Get latitude and longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String locationUrl = "https://www.google.com/maps?q=" + latitude + "," + longitude;
                String emergencyMessage = "This is an emergency message. Please help! Here is my location: " + locationUrl;

                // Send the SMS
                sendSms(emergencyMessage);
            } else {
                // If location is null, send a fallback message without the location
                sendSms("This is an emergency message. Please help! Location not available.");
            }
        });
    }

    // Send SMS
    private void sendSms(String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(EMERGENCY_NUMBER, null, message, null, null);
            Toast.makeText(getContext(), "Emergency SMS sent successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Both permissions are granted
                    getCurrentLocationAndSendSms();
                } else {
                    Toast.makeText(getContext(), "Location permission denied.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "SMS permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

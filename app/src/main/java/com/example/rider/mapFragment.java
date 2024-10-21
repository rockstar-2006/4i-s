package com.example.rider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

public class mapFragment extends Fragment implements OnMapReadyCallback {


    private LatLng currentLocationLatLng;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private DatabaseReference riderLocationRef;
    private String riderId = "Rider";
    private Polyline polyline;
    private List<LatLng> polylinePoints = new ArrayList<>();

    private HashMap<String, Marker> driverMarkers = new HashMap<>();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationProviderClient != null && locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize riderLocationRef
        riderLocationRef = FirebaseDatabase.getInstance().getReference("riders").child(riderId).child("location");

        return view;
    }

    private void init() {
        locationRequest = LocationRequest.create();
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    LatLng newPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 18f)); // Directly move the camera
                    currentLocationLatLng = newPosition; // Update current location

                    // Update rider location in Firebase
                    Map<String, Object> locationMap = new HashMap<>();
                    locationMap.put("latitude", location.getLatitude());
                    locationMap.put("longitude", location.getLongitude());

                    riderLocationRef.updateChildren(locationMap)
                            .addOnSuccessListener(aVoid -> Log.d("RiderLocation", "Rider location updated successfully"))
                            .addOnFailureListener(e -> Log.e("RiderLocation", "Failed to update rider location: " + e.getMessage()));
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng indiaCenter = new LatLng(20.5937, 78.9629); // Approximate center of India
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(indiaCenter, 5)); // Zoom level to view whole India

        Dexter.withContext(getContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);

                        mMap.setOnMyLocationButtonClickListener(() -> {
                            fusedLocationProviderClient.getLastLocation()
                                    .addOnFailureListener(e -> Snackbar.make(requireView(), e.getMessage(), Snackbar.LENGTH_SHORT).show())
                                    .addOnSuccessListener(location -> {
                                        if (location != null) {
                                            LatLng userLatlng = new LatLng(location.getLatitude(), location.getLongitude());
                                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatlng, 18f));
                                        } else {
                                            Snackbar.make(requireView(), "Location is null", Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                            return true;
                        });

                        if (mapFragment != null && mapFragment.getView() != null) {
                            View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                            params.setMargins(0, 0, 0, 50);
                        }

                        startFirebaseLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Snackbar.make(getView(), permissionDeniedResponse.getPermissionName() + " needs to be enabled", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.uber_maps_style));
        init();
    }

    private void startFirebaseLocationUpdates() {
        DatabaseReference driversLocationRef = FirebaseDatabase.getInstance().getReference("users");

        driversLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot driverSnapshot : snapshot.getChildren()) {
                        DataSnapshot locationSnapshot = driverSnapshot.child("driverLocation");
                        Double latitude = locationSnapshot.child("latitude").getValue(Double.class);
                        Double longitude = locationSnapshot.child("longitude").getValue(Double.class);
                        String locationName = locationSnapshot.child("locationName").getValue(String.class);
                        String driverId = driverSnapshot.getKey();

                        if (latitude != null && longitude != null && driverId != null) {
                            LatLng driverLatLng = new LatLng(latitude, longitude);

                            if (driverMarkers.containsKey(driverId)) {
                                Marker existingMarker = driverMarkers.get(driverId);
                                // Smoothly animate the marker to the new position
                                animateMarkerToPosition(existingMarker, driverLatLng);
                                existingMarker.setTitle("Driver in " + (locationName != null ? locationName : "Unknown"));
                            } else {
                                BitmapDescriptor customIcon = BitmapDescriptorFactory.fromResource(R.drawable.ambu);
                                Marker newMarker = mMap.addMarker(new MarkerOptions()
                                        .position(driverLatLng)
                                        .title("Driver in " + (locationName != null ? locationName : "Unknown"))
                                        .icon(customIcon));

                                driverMarkers.put(driverId, newMarker);
                            }

                            Log.d("DriverLocation", "Driver Location: " + driverLatLng.toString());
                        }
                    }
                } else {
                    Snackbar.make(requireView(), "No driver location data available", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(requireView(), "Failed to load driver locations: " + error.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void animateMarkerToPosition(final Marker marker, final LatLng newPosition) {
        final LatLng startPosition = marker.getPosition();
        final long duration = 1000; // Animation duration in milliseconds

        final ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> {
            float v = animation.getAnimatedFraction();
            double lat = v * newPosition.latitude + (1 - v) * startPosition.latitude;
            double lng = v * newPosition.longitude + (1 - v) * startPosition.longitude;
            marker.setPosition(new LatLng(lat, lng));
        });
        animator.start();
    }

    private void searchForPlace(String query) {
        new Thread(() -> {
            try {
                PlaceSearch placeSearch = new PlaceSearch();
                String response = placeSearch.searchPlace(query);
                getActivity().runOnUiThread(() -> updateMapWithPlaces(response));
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Snackbar.make(requireView(), "Failed to search places", Snackbar.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void updateMapWithPlaces(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject place = jsonArray.getJSONObject(i);
                double lat = place.getDouble("lat");
                double lon = place.getDouble("lon");
                String displayName = place.getString("display_name");

                LatLng destinationLatLng = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions()
                        .position(destinationLatLng)
                        .title(displayName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                if (i == 0) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 12f));
                    drawPolyline(currentLocationLatLng, destinationLatLng); // Draw and animate polyline from current location to destination
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(requireView(), "Error parsing place search results", Snackbar.LENGTH_SHORT).show();
        }
    }

    // Other methods remain the same...


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        EditText searchInput = view.findViewById(R.id.search_input);
        Button searchButton = view.findViewById(R.id.search_button);
        LinearLayout searchLayout = view.findViewById(R.id.txtwelcome);

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;

                    // Set up map and add markers
                    setUpMap();

                    // Enable the user's location layer

                    // Set marker click listener
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            LatLng destinationLocation = marker.getPosition();

                            // Draw the route from the current location to the marker location
                            drawRouteToDestination(destinationLocation);

                            return true; // Return true to consume the click event
                        }
                    });
                }
            });
        }






        class PlaceSearch {
            private String searchPlace(String query) throws Exception {
                // Encode query to handle spaces and special characters
                String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
                // Bounding box coordinates for India
                String bbox = "&viewbox=68.7,8.4,97.2,37.6"; // west,south,east,north
                String apiUrl = "https://nominatim.openstreetmap.org/search?q=" + encodedQuery + "&format=json&addressdetails=1&limit=5" + bbox;

                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            }
        }
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchInput.getText().toString();
                if (!query.isEmpty()) {
                    searchForPlace(query);
                }
            }
        });
}


    private void getRoute(LatLng origin, LatLng destination) {
        String apiKey = "AIzaSyCWHXwSWSikQwpfyhUNwxGTmcIyPsJseEk";
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                origin.latitude + "," + origin.longitude + "&destination=" +
                destination.latitude + "," + destination.longitude +
                "&key=" + apiKey;

        new Thread(() -> {
            try {
                URL routeUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) routeUrl.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();


            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Snackbar.make(requireView(), "Failed to get route", Snackbar.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void drawGlowingPolyline(String jsonResponse) {
        try {
            // Parse JSON response
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray routes = jsonObject.getJSONArray("routes");
            if (routes.length() > 0) {
                JSONObject route = routes.getJSONObject(0);
                JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                String encodedPolyline = overviewPolyline.getString("points");

                List<LatLng> polylinePoints = decodePoly(encodedPolyline);

                // Clear existing polylines
                mMap.clear();

                // Define polyline options for glow effect
                PolylineOptions glowingOptions = new PolylineOptions()
                        .addAll(polylinePoints)
                        .width(20)  // Width of glowing effect
                        .color(Color.argb(100, 255, 255, 255)); // Light color

                Polyline glowingPolyline = mMap.addPolyline(glowingOptions);

                PolylineOptions mainOptions = new PolylineOptions()
                        .addAll(polylinePoints)
                        .width(10)
                        .color(Color.BLUE);

                Polyline mainPolyline = mMap.addPolyline(mainOptions);

                // Animate the main polyline
                animatePolyline(mainPolyline, polylinePoints);

                // Animate glowing effect
                animateGlowingEffect(glowingPolyline);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(requireView(), "Error parsing route", Snackbar.LENGTH_SHORT).show();
        }
    }


    private void animatePolyline(Polyline polyline, List<LatLng> polylinePoints) {
        if (polyline == null) return;

        ValueAnimator polylineAnimator = ValueAnimator.ofFloat(0, 1);
        polylineAnimator.setDuration(3000); // Duration in milliseconds
        polylineAnimator.setInterpolator(new LinearInterpolator());

        polylineAnimator.addUpdateListener(animation -> {
            float v = animation.getAnimatedFraction();
            int totalPoints = polylinePoints.size();
            int endIndex = (int) (v * (totalPoints - 1));

            if (endIndex < totalPoints) {
                List<LatLng> pointsToShow = polylinePoints.subList(0, endIndex + 1);
                polyline.setPoints(pointsToShow);
            }
        });

        polylineAnimator.start();
    }

    private void animateGlowingEffect(Polyline polyline) {
        if (polyline == null) return;

        // Create a ValueAnimator to change the color of the polyline
        ValueAnimator colorAnimator = ValueAnimator.ofArgb(
                Color.argb(100, 255, 255, 255),  // Start color
                Color.argb(0, 255, 255, 255)     // End color (transparent)
        );
        colorAnimator.setDuration(1000);  // Duration of one cycle in milliseconds
        colorAnimator.setInterpolator(new LinearInterpolator());

        colorAnimator.addUpdateListener(animation -> {
            int color = (int) animation.getAnimatedValue();
            polyline.setColor(color);
        });

        // Start the animation and repeat indefinitely
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimator.start();
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng(
                    (lat / 1E5),
                    (lng / 1E5)
            );
            poly.add(p);
        }

        return poly;
    }

    private void drawPolyline(LatLng startLatLng, LatLng endLatLng) {
        if (startLatLng != null && endLatLng != null) {
            Polyline polyline = mMap.addPolyline(new PolylineOptions()
                    .add(startLatLng, endLatLng)
                    .width(5)
                    .color(Color.BLACK));
        }
    }
    private void searchForHospital(String query) {
        new Thread(() -> {
            try {
                PlaceSearch placeSearch = new PlaceSearch();
                String response = placeSearch.searchPlace(query);
                getActivity().runOnUiThread(() -> updateMapWithHospitals(response));
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Snackbar.make(requireView(), "Failed to search hospitals", Snackbar.LENGTH_SHORT).show());
            }
        }).start();
    }
    private void updateMapWithHospitals(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray results = jsonObject.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject hospital = results.getJSONObject(i);
                JSONObject geometry = hospital.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");

                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");
                String name = hospital.getString("name");

                LatLng hospitalLocation = new LatLng(lat, lng);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(hospitalLocation)
                        .title(name));

                marker.setTag(hospitalLocation);
            }

            mMap.setOnMarkerClickListener(marker -> {
                LatLng location = (LatLng) marker.getTag();
                if (location != null) {
                    Log.d("Map", "Marker clicked: " + marker.getTitle());

                    // Hide the search bar
                    View searchLayout = getActivity().findViewById(R.id.txtwelcome);
                    if (searchLayout != null) {
                        Log.d("Map", "Hiding search bar");
                        searchLayout.setVisibility(View.GONE); // Hides the search bar
                    } else {
                        Log.d("Map", "Search layout not found");
                    }

                    // Show the destination dialog
                    showDestinationDialog(location);
                } else {
                    Log.d("Map", "Marker tag is null");
                }
                return false; // Return false to indicate the default behavior of the click event
            });

        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(requireView(), "Failed to update map", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void searchNearbyHospitals() {
        if (currentLocationLatLng != null) {
            double latitude = currentLocationLatLng.latitude;
            double longitude = currentLocationLatLng.longitude;

            String urlString = "https://nominatim.openstreetmap.org/search?q=hospital&format=json&addressdetails=1&limit=5&viewbox=" +
                    (longitude - 0.01) + "," + (latitude - 0.01) + "," +
                    (longitude + 0.01) + "," + (latitude + 0.01);

            new Thread(() -> {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    reader.close();
                    connection.disconnect();

                    parseHospitalResponse(result.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            Snackbar.make(requireView(), "Current location is not available", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void parseHospitalResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject hospital = jsonArray.getJSONObject(i);
                double lat = hospital.getDouble("lat");
                double lon = hospital.getDouble("lon");
                String name = hospital.getString("display_name");

                // Update the UI on the main thread
                getActivity().runOnUiThread(() -> addHospitalMarker(lat, lon, name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addHospitalMarker(double lat, double lon, String name) {
        LatLng hospitalLocation = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(hospitalLocation).title(name));
    }

    private void showDestinationDialog(LatLng location) {
        Log.d("MapFragment", "Showing dialog for location: " + location.toString());
        new AlertDialog.Builder(requireContext()) // Use requireContext() for non-null context
                .setTitle("Destination")
                .setMessage("Coordinates: " + location.latitude + ", " + location.longitude)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void navigateToLocation(LatLng location) {
        String uri = "google.navigation:q=" + location.latitude + "," + location.longitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Snackbar.make(requireView(), "No navigation app found", Snackbar.LENGTH_SHORT).show();
        }
    }
    private void setUpMap() {
        // Example of adding a marker
        LatLng exampleLocation = new LatLng(-34, 151);
        Marker marker = mMap.addMarker(new MarkerOptions().position(exampleLocation).title("Hospital Location"));
        if (marker != null) {
            marker.setTag(exampleLocation);  // Set tag to the location
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(exampleLocation, 15));
    }
    private void drawRouteToDestination(LatLng destination) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get current location
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    // Construct the Google Directions API URL
                    String directionsUrl = getDirectionsUrl(currentLocation, destination);

                    // Fetch and draw the route
                    new FetchDirectionsTask().execute(directionsUrl);
                }
            });
        } else {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String key = "key=YOUR_GOOGLE_MAPS_API_KEY";
        String parameters = strOrigin + "&" + strDest + "&" + sensor + "&" + key;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }
    private class FetchDirectionsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            try {
                // Fetch data from the provided URL
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(url[0]).openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }}
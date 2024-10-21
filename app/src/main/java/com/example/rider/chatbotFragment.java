package com.example.rider;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Correct import for Toolbar
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class chatbotFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    private static final int CAPTURE_IMAGE = 2;
    private Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        // Correct Toolbar reference
        Toolbar bottomToolbar = view.findViewById(R.id.bottomToolbar);
        if (getActivity() != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(bottomToolbar);
        }

        // Request permissions
        requestPermissions();

        Button selectImageButton = view.findViewById(R.id.selectImageButton);
        Button captureImageButton = view.findViewById(R.id.captureImageButton);
        ImageView imageView = view.findViewById(R.id.imageView);

        selectImageButton.setOnClickListener(v -> openGallery());
        captureImageButton.setOnClickListener(v -> openCamera());

        // Set up the share button listener
        Button shareImageButton = view.findViewById(R.id.shareImageButton);
        shareImageButton.setOnClickListener(v -> shareImage(imageUri));

        return view;
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 2);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE);
    }

    private void shareImage(Uri uri) {
        if (uri != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            try {
                startActivity(Intent.createChooser(shareIntent, "Share Image"));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireContext(), "No application available to share images", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "No image to share", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ImageView imageView = requireView().findViewById(R.id.imageView); // Reference to ImageView
            if (requestCode == PICK_IMAGE && data != null) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri); // Display selected image
            } else if (requestCode == CAPTURE_IMAGE && data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageUri = getImageUri(bitmap);
                imageView.setImageURI(imageUri); // Display captured image
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
        } else {
            Toast.makeText(requireContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
        } else {
            Toast.makeText(requireContext(), "Permission denied to use your Camera", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendImageToAmbucare(Uri imageUri) {
        if (imageUri != null) {
            Intent intent = new Intent();
            intent.setAction("com.example.ambucare.RECEIVE_IMAGE");  // Custom action for Ambucare
            intent.setData(imageUri);  // Attach the image URI
            intent.setPackage("com.example.ambucare"); // Directly send to Ambucare by specifying package name
            intent.setClassName("com.example.ambucare", "com.example.ambucare.HomeActivity"); // Class name of the target activity

            try {
                startActivity(intent);  // Start the activity to handle the intent in Ambucare
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireContext(), "Ambucare app not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "No image to send", Toast.LENGTH_SHORT).show();
        }
    }

}

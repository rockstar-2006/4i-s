package com.example.rider;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Random;

public class profileFragment extends Fragment {

    private EditText inputText;
    private Button generateButton;
    private LottieAnimationView lottieAnimation;
    private ImageView generatedImage;
    private TextView explanationText;
    private CardView resultCardView;

    // Array of predefined image explanations
    private String[] imageExplanations = {
            "1. Check for Responsiveness\n" +
                    "Tap the person gently and shout loudly to check if they respond.\n" +
                    "Call for Help: If they don’t respond, call emergency services immediately (e.g., dial 911 or the local emergency number). If possible, have someone else call for help.\n" +
                    "2. Check for Breathing\n" +
                    "Look at their chest to see if it’s rising and falling.\n" +
                    "Listen for breath sounds by placing your ear close to their mouth and nose.\n" +
                    "If they’re not breathing or only gasping, proceed to CPR.\n" +
                    "3. Start Chest Compressions\n" +
                    "Position your hands: Place the heel of one hand on the center of the person's chest, right on the lower half of the breastbone. Place your other hand on top of the first hand and interlock your fingers.\n" +
                    "Compressions: Use your body weight to push down hard and fast.\n" +
                    "Compression depth: At least 2 inches (5 cm).\n" +
                    "Compression rate: 100 to 120 compressions per minute (it helps to think of the beat of the song “Stayin' Alive” by the Bee Gees).\n" +
                    "Allow the chest to recoil completely between compressions, but don’t lift your hands off the chest.\n" +
                    "4. Give Rescue Breaths (if trained)\n" +
                    "After 30 chest compressions, you can give 2 rescue breaths if you’re trained.\n" +
                    "Open the airway by tilting the head back and lifting the chin up.\n" +
                    "Pinch the person’s nose shut and give a breath into their mouth, making sure the chest rises.\n" +
                    "Give a second breath in the same way.\n" +
                    "Continue cycles of 30 compressions and 2 breaths.\n" +
                    "5. Continue Until Help Arrives\n" +
                    "Keep performing CPR until:\n" +
                    "Emergency help arrives.\n" +
                    "The person starts breathing or shows signs of life.\n" +
                    "You’re too exhausted to continue.",
            "2.Call an ambulance \"119\" to get \"AED\"\n" +
                    "Give chest compressions when you get an AED.\n" +
                    "Open the AED. Apply the Pads.if the patient need the shock push the flashing button.\n" +
                    "Call the Health Center and or Security office."
    };

    // Array of predefined images
    private int[] imageArray = {
            R.drawable.cpr,  // Corresponding to the first explanation
            R.drawable.aed   // Corresponding to the second explanation
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        inputText = view.findViewById(R.id.inputText);
        generateButton = view.findViewById(R.id.generateButton);
        lottieAnimation = view.findViewById(R.id.lottieAnimation);
        generatedImage = view.findViewById(R.id.generatedImage);
        explanationText = view.findViewById(R.id.explanationText);
        resultCardView = view.findViewById(R.id.resultCardView);

        // Set click listener for the Generate Button
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = inputText.getText().toString().trim();

                if (!userInput.isEmpty()) {
                    // Show loading animation and hide the image and explanation
                    lottieAnimation.setVisibility(View.VISIBLE);
                    generatedImage.setVisibility(View.GONE);
                    explanationText.setVisibility(View.GONE);

                    // Simulate the image generation process (3 seconds delay for example)
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Hide the loading animation and show the image and explanation
                            lottieAnimation.setVisibility(View.GONE);
                            generatedImage.setVisibility(View.VISIBLE);
                            explanationText.setVisibility(View.VISIBLE);

                            // Generate a random index to select image and explanation
                            Random random = new Random();
                            int randomIndex = random.nextInt(imageArray.length);

                            // Set the randomly selected image
                            generatedImage.setImageResource(imageArray[randomIndex]);

                            // Set the corresponding explanation
                            explanationText.setText(imageExplanations[randomIndex]);
                        }
                    }, 3000); // Simulating a delay of 3 seconds for loading
                } else {
                    // Show a message if the input is empty
                    Toast.makeText(getActivity(), "Please enter text to generate an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}

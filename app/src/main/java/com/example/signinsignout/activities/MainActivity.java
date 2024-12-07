package com.example.signinsignout.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.signinsignout.R;
import com.example.signinsignout.databinding.ActivityMainBinding;
import com.example.signinsignout.utilities.Constants;
import com.example.signinsignout.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    /**
     * Called when the activity is starting. Sets up the layout, initializes the preference manager,
     * loads user details, retrieves a token, and sets up event listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}.
     *                           <code>null</code> if the activity is being launched for the first time.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        getToken();
        setListeners();

    }

    /**
     * Sets click listeners for UI components.
     *
     * - Signs out the user when the sign-out button is clicked.
     * - Opens the user activity when the new chat button is clicked.
     */
    private void setListeners(){
        binding.imageSignOut.setOnClickListener(view -> signOut());
        binding.fabNewChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), userActivity.class)));
    }

    /**
     * Loads the current user's details from the preference manager and updates the UI.
     *
     * - Sets the user's full name in the text view.
     * - Decodes the user's profile image from a Base64 string and displays it in the profile image view.
     */
    private void loadUserDetails(){
        binding.textName.setText(preferenceManager.getString(Constants.KEY_FIRST_NAME) + " " + preferenceManager.getString(Constants.KEY_LAST_NAME));

        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    /**
     * Displays a short toast message.
     *
     * @param message The message to be displayed in the toast.
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    /**
     * Retrieves the Firebase Cloud Messaging (FCM) token and updates it using the updateToken method.
     */
    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    /**
     * Retrieves the Firebase Cloud Messaging (FCM) token and updates it using the updateToken method.
     */
    private void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
                .addOnSuccessListener(unused -> showToast("Token updated successfully"))
                .addOnFailureListener(e -> showToast("Unable to update Token"));
    }

    /**
     * Signs the user out of the application.
     *
     * - Displays a toast message indicating the sign-out process.
     * - Deletes the user's FCM token from the Firestore database.
     * - Clears user preferences and redirects to the SignInActivity on success.
     * - Displays an error toast if the sign-out process fails.
     */
    private void signOut(){
        showToast("Signing out ...");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String,Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                }).addOnFailureListener(e -> showToast("Unable to sign out"));
    }
}
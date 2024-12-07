package com.example.signinsignout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.signinsignout.R;
import com.example.signinsignout.adapters.UsersAdapter;
import com.example.signinsignout.databinding.ActivityUserBinding;
import com.example.signinsignout.listeners.UserListener;
import com.example.signinsignout.models.User;
import com.example.signinsignout.utilities.Constants;
import com.example.signinsignout.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class userActivity extends AppCompatActivity implements UserListener {

    private ActivityUserBinding binding;
    private PreferenceManager preferenceManager;

/**
 * Called when the activity is starting. Sets up the layout, initializes the preference manager,
 * sets up event listeners, and retrieves the list of users.
 *
 * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
 *                           this Bundle contains the data it most recently
*/
 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        setListeners();
        getUsers();
    }

    /**
     * Sets a click listener for the back button to navigate to the previous screen when clicked.
     */
    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    /**
     * Retrieves the list of users from the Firestore database and updates the UI.
     *
     * - Shows a loading indicator while fetching data.
     * - Filters out the current user's details from the retrieved user list.
     * - Populates the RecyclerView with the list of users if available.
     * - Displays an error message if no users are found or the task fails.
     */
    private void getUsers() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.fname = queryDocumentSnapshot.getString(Constants.KEY_FIRST_NAME);
                            user.lname = queryDocumentSnapshot.getString(Constants.KEY_LAST_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if (users.size() > 0) {
                            UsersAdapter usersAdapter = new UsersAdapter(users, this);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        } else{
                            showErrorMessage();
                        }
                    }else{
                        showErrorMessage();
                    }
                });
    }

    /**
     * Displays an error message indicating that no users are available.
     *
     * - Sets the error message text.
     * - Makes the error message view visible.
     */
    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    /**
     * Displays a toast message with the specified text.
     *
     * @param message The message to display in the toast.
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Toggles the visibility of the progress bar based on the loading state.
     *
     * @param isLoading A Boolean indicating whether to show or hide the progress bar.
     *                  - true: Shows the progress bar.
     *                  - false: Hides the progress bar.
     */
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * Handles the event when a user is clicked in the user list.
     *
     * - Starts the ChatActivity, passing the selected user as an intent extra.
     * - Finishes the current activity.
     *
     * @param user The selected user object.
     */
    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }

}

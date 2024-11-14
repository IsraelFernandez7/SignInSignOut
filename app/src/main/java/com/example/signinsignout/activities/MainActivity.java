package com.example.signinsignout.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.signinsignout.R;

/**
 * Main activity that serves as the entry point for the application.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Called when the activity is starting. Sets up the main view layout
     * and applies system bar insets for edge-to-edge display.
     *      *
     *      * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, this Bundle contains
     *                           the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
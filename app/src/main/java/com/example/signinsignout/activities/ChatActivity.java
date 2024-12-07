package com.example.signinsignout.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.signinsignout.R;
import com.example.signinsignout.adapters.ChatAdapter;
import com.example.signinsignout.databinding.ActivityChatBinding;
import com.example.signinsignout.models.ChatMessage;
import com.example.signinsignout.models.User;
import com.example.signinsignout.utilities.Constants;
import com.example.signinsignout.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User receiverUser;

    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;


/**
 * Called when the activity is starting. Initializes the layout, listeners, and starts message listening.
 *
 * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
 *                           this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}.
 *                           <code>null</code> if the activity is being launched for the first time.
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadReceiverDetails();
        setListeners();
        init();
        ListenMessage();
    }

    /**
     * Initializes the necessary components for the chat activity, including the preference manager,
     * chat messages list, chat adapter, and Firebase Firestore database instance.
     */
    private void init(){
        preferenceManager = new PreferenceManager((getApplicationContext()));
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                getBitmapFromEncodedString(receiverUser.image),
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database= FirebaseFirestore.getInstance();
    }

    /**
     * Sends a chat message by creating a message object and storing it in the Firestore database.
     *
     * The message object includes the sender ID, receiver ID, message content, and timestamp.
     * After sending the message, the input message field is cleared.
     */
    private void sendMessages(){
        HashMap<String, Object> message = new HashMap<>();

        message.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.id);

        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());

        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        binding.inputMessage.setText(null);
    }

    /**
     * Listens for real-time updates to chat messages in the Firestore database.
     *
     * Sets up snapshot listeners for messages sent by the current user to the receiver
     * and messages sent by the receiver to the current user. Updates are handled by the
     * specified event listener.
     */
    private void ListenMessage(){
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,
                        preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
                .addSnapshotListener(eventListener);

        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,
                        receiverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,
                        preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }

    /**
     * Event listener for real-time updates to Firestore chat messages.
     *
     * Processes added chat messages, updates the local chat messages list, sorts them
     * by timestamp, and notifies the chat adapter of changes.
     */
    private final EventListener<QuerySnapshot> eventListener = ((value,error) ->{
        if(error !=null){
            return;
        }
        if(value != null){
            int count = chatMessages.size();
            for(DocumentChange documentChange:value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverID = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(
                            documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));

                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }

            Collections.sort(chatMessages,(obj1,obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if(count == 0){
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemChanged(chatMessages.size(), chatMessages.size());

                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size()-1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }

        binding.progressBar.setVisibility(View.GONE);
    });
    /**
     * Decodes a Base64 encoded image string into a Bitmap.
     *
     * @param encodedImage The Base64 encoded string representing the image.
     * @return The decoded Bitmap.
     */

    private Bitmap getBitmapFromEncodedString(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    /**
     * Loads the receiver's details from the intent and updates the UI with the receiver's name.
     */
    private void loadReceiverDetails(){
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUser.fname);
    }

    /**
     * Sets click listeners for UI components.
     *
     * - Navigates back when the back button is clicked.
     * - Sends a message when the send button is clicked.
     */
    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());

        binding.layoutSend.setOnClickListener(v -> sendMessages());
    }
    /**
     * Converts a Date object into a formatted, human-readable date-time string.
     *
     * @param date The Date object to be formatted.
     * @return A string representing the formatted date and time (e.g., "MMM dd, yyyy - hh:mm a").
     */
    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMM dd, yyyy - hh:mm a",
                Locale.getDefault()).format(date);
    }
}
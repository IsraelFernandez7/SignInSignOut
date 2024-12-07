package com.example.signinsignout.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.signinsignout.databinding.ItemContainerReceivedMessageBinding;
import com.example.signinsignout.databinding.ItemContainerSentMessageBinding;
import com.example.signinsignout.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Bitmap receiverProfileImage;
    private final List<ChatMessage> chatMessages;
    private final String sendId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    /**
     * Constructor for the ChatAdapter.
     *
     * @param chatMessages         The list of chat messages to be displayed.
     * @param receiverProfileImage The Bitmap image of the receiver's profile picture.
     * @param sendId               The sender's unique ID.
     */
    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String sendId) {
        this.receiverProfileImage = receiverProfileImage;
        this.chatMessages = chatMessages;
        this.sendId = sendId;
    }

    /**
     * Creates and returns the appropriate ViewHolder for a chat message based on its view type.
     *
     * @param parent   The parent ViewGroup into which the new View will be added.
     * @param viewType The type of view to create (e.g., sent or received message).
     * @return A RecyclerView.ViewHolder corresponding to the specified view type.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(ItemContainerSentMessageBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return new ReceiverMessageViewHolder(ItemContainerReceivedMessageBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    /**
     * Binds data to the appropriate ViewHolder based on the position and view type.
     *
     * @param holder   The RecyclerView.ViewHolder to bind data to.
     * @param position The position of the item within the data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        } else {
            ((ReceiverMessageViewHolder) holder)
                    .setData(chatMessages.get(position), receiverProfileImage);
        }
    }

    /**
     * Returns the total number of items in the chat messages list.
     *
     * @return The size of the chat messages list.
     */
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    /**
     * Returns the view type of the item at the specified position.
     *
     * @param position The position of the item within the data set.
     * @return VIEW_TYPE_SENT if the message was sent by the current user,
     *         VIEW_TYPE_RECEIVED otherwise.
     */
    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(sendId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }


    /**
     * ViewHolder class for displaying sent messages in the chat.
     */
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentMessageBinding binding;

        /**
         * Constructor for the SentMessageViewHolder.
         *
         * @param itemContainerSentMessageBinding The binding object for the sent message layout.
         */
        public SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        /**
         * Binds chat message data to the sent message view.
         *
         * @param chatMessage The ChatMessage object containing the message text and date-time.
         */
        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }

    /**
     * ViewHolder class for displaying received messages in the chat.
     */
    static class ReceiverMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceivedMessageBinding binding;

        public ReceiverMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);

            if (receiverProfileImage != null) {
                binding.imageProfile.setImageBitmap(receiverProfileImage);
            }
        }
    }
}

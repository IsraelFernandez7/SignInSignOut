package com.example.signinsignout.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.signinsignout.databinding.ItemContainerUserBinding;
import com.example.signinsignout.listeners.UserListener;
import com.example.signinsignout.models.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private final List<User> users;
    private final UserListener userListener;

    /**
     * Constructor for the UsersAdapter.
     *
     * @param users        The list of User objects to be displayed.
     * @param userListener The listener for user click events.
     */
    public UsersAdapter(List<User> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }


    /**
     * Creates and returns a UserViewHolder for the user item view.
     *
     * @param parent   The parent ViewGroup into which the new View will be added.
     * @param viewType The type of view to create (unused in this case).
     * @return A new UserViewHolder instance.
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding
                .inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new UserViewHolder(itemContainerUserBinding);
    }

    /**
     * Binds the user data to the UserViewHolder at the specified position.
     *
     * @param holder   The UserViewHolder to bind data to.
     * @param position The position of the item within the user list.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));

    }

    /**
     * Returns the total number of users in the list.
     *
     * @return The size of the users list.
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * ViewHolder class for displaying user details in the user list.
     */
    class UserViewHolder extends RecyclerView.ViewHolder{
        ItemContainerUserBinding binding;


        public UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());

            binding = itemContainerUserBinding;
        }

        void setUserData(User user){
            binding.textName.setText(user.fname);
            binding.textEmail.setText(user.email);
            binding.imageProfile.setImageBitmap(getUserImage(user.image));

            binding.getRoot().setOnClickListener(y -> userListener.onUserClicked(user));
        }
    }

    /**
     * Decodes a Base64 encoded string into a Bitmap representing the user's profile image.
     *
     * @param encodedImage The Base64 encoded string of the image.
     * @return The decoded Bitmap image.
     */
    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

}
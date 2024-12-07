package com.example.signinsignout.utilities;

/**
 * Constants class to store keys used across the application, primarily for database
 * collections and shared preferences.
 */
public class Constants {

    /** Key for the users collection in the database. */
    public static final String KEY_COLLECTION_USERS = "user";

    /** Key for the name field in the user database. */
    public static final String KEY_NAME = "name";

    /** Key for the first name field in the user database. */
    public static final String KEY_FIRST_NAME = "First name";

    /** Key for the last name field in the user database. */
    public static final String KEY_LAST_NAME = "Last name";

    /** Key for the email field in the user database. */
    public static final String KEY_EMAIL = "email";

    /** Key for the password field in the user database. */
    public static final String KEY_PASSWORD = "password";

    /** Key for the user ID field in the user database. */
    public static final String KEY_USER_ID = "userid";

    /** Key to check if a user is signed in, stored in shared preferences. */
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";

    /** Key for the shared preferences name. */
    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";

    /** Key for the image field in the user database. */
    public static final String KEY_IMAGE = "image";

    /**
     * Constants used throughout the application for keys and identifiers.
     */
    public static final String KEY_FCM_TOKEN = "fcmToken";

    public static final String KEY_USER = "user";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";

}

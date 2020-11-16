//this class for set table names and column names
package com.example.healthcare;

import android.provider.BaseColumns;

public class UserMaster {
    private UserMaster(){}

    //table for users
    public static class Users implements BaseColumns{
        public static String TABLE_NAME = "users";
        public static String COLUMN_NAME_PROFILEIMAGE = "profileImage";
        public static String COLUMN_NAME_USERNAME = "username";
        public static String COLUMN_NAME_PASSWORD = "password";
        public static String COLUMN_NAME_BLOODGROUP = "bloodGroup";
    }
    //table for images
    public static class Images implements BaseColumns{
        public static String TABLE_NAME = "images";
        public static String COLUMN_NAME_USERNAME = "username";
        public static String COLUMN_NAME_IMAGE = "image";
    }
    //table for profile pictures
    public static class ProfileImages implements BaseColumns{
        public static String TABLE_NAME = "Profile_images";
        public static String COLUMN_NAME_USERNAME = "username";
        public static String COLUMN_NAME_IMAGE = "image";
    }
}

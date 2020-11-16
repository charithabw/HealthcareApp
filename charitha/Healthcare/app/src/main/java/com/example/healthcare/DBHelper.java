//this is the DB class
//all the Db CRUD doing here
package com.example.healthcare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    Context context;
    public static final String DATABASE_NAME = "userIfo.bd";
    private ByteArrayOutputStream byteArrayOutputStream;
    private byte[] imageInByte;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);//set Db name
        this.context = context;
    }

    //creating table
    //Usermaster.User and Usermaster.Images are class of the table name and column name
    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_ENTRIES = "create table " + UserMaster.Users.TABLE_NAME + " (" +
                UserMaster.Users._ID + " integer primary key, " +
                UserMaster.Users.COLUMN_NAME_USERNAME + " text," +
                UserMaster.Users.COLUMN_NAME_BLOODGROUP + " text," +
                UserMaster.Users.COLUMN_NAME_PASSWORD + " text)";

        String SQL_CREATE_ENTRIES_2 = "create table " + UserMaster.Images.TABLE_NAME + " (" +
                UserMaster.Images._ID + " integer primary key, " +
                UserMaster.Images.COLUMN_NAME_USERNAME + " text," +
                UserMaster.Images.COLUMN_NAME_IMAGE + " blob)";//this is the type for storing images

        String SQL_CREATE_ENTRIES_3 = "create table " + UserMaster.ProfileImages.TABLE_NAME + " (" +
                UserMaster.ProfileImages._ID + " integer primary key, " +
                UserMaster.ProfileImages.COLUMN_NAME_USERNAME + " text," +
                UserMaster.ProfileImages.COLUMN_NAME_IMAGE + " blob)";

        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES_3);
        db.execSQL(SQL_CREATE_ENTRIES_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
    //inserting data
    public boolean insertData( String username, String blood, String password){

        SQLiteDatabase db = this.getWritableDatabase();
        password = md5(password);
        Log.d("yyy", password);
        ContentValues values = new ContentValues();

        //insert data
        values.put(UserMaster.Users.COLUMN_NAME_USERNAME, username);
        values.put(UserMaster.Users.COLUMN_NAME_BLOODGROUP, blood);
        values.put(UserMaster.Users.COLUMN_NAME_PASSWORD, password);

        long newRowID = db.insert(UserMaster.Users.TABLE_NAME, null, values);

        if(newRowID == -1){
            return false;
        }
        else{
            return true;
        }
    }
    //check username if early in the BD
    public boolean checkUsername(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + UserMaster.Users.TABLE_NAME + " where " +
                UserMaster.Users.COLUMN_NAME_USERNAME + "  = ?", new String[] {username});
        if(cursor.getCount() > 0){
            return true;
        }
        else{
            return false;
        }
    }
    //check username and the password with stored data
    public boolean checkUsernamePasswrod(String username, String passsword){
        passsword = md5(passsword);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + UserMaster.Users.TABLE_NAME + " where " +
                UserMaster.Users.COLUMN_NAME_USERNAME + " =? and " + UserMaster.Users.COLUMN_NAME_PASSWORD +
                " =? ", new String[] {username, passsword});
        if(cursor.getCount() > 0){
            return true;
        }
        else{
            return false;
        }
    }
    //get users table data
    public Cursor getDetails(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] col =
                {UserMaster.Users._ID,
                 UserMaster.Users.COLUMN_NAME_USERNAME,
                 UserMaster.Users.COLUMN_NAME_PASSWORD};
        String selection = UserMaster.Users.COLUMN_NAME_USERNAME + " =? ";
        String[] selectionArgs = {name};

        Cursor cursor = db.rawQuery("select * from " + UserMaster.Users.TABLE_NAME + " where " +
                UserMaster.Users.COLUMN_NAME_USERNAME + " = ? ", new String[] {name});

        //Log.e("ssss", name);

        return cursor;
    }
    //uodate users table data
    public boolean updateInfo(String name, String blood, String password){
        password = md5(password);
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(UserMaster.Users.COLUMN_NAME_USERNAME, name);
        values.put(UserMaster.Users.COLUMN_NAME_BLOODGROUP, blood);
        values.put(UserMaster.Users.COLUMN_NAME_PASSWORD, password);

        String selection = UserMaster.Users.COLUMN_NAME_USERNAME + " like ? ";
        String[] selectionArgs = {name};

        int count = db.update(
                UserMaster.Users.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        if(count > 0){
            return true;
        }
        else{
            return false;
        }

    }
    public boolean updatePassword(String name, String password){
//        String md5Password = md5(password);
        password = md5(password);
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(UserMaster.Users.COLUMN_NAME_PASSWORD, password);

        String selection = UserMaster.Users.COLUMN_NAME_USERNAME + " like ? ";
        String[] selectionArgs = {name};

        int count = db.update(
                UserMaster.Users.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        if(count > 0){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean updateProfileImage(String name, ImageModel imageObject){
        SQLiteDatabase db = this.getReadableDatabase();

        Bitmap imageToStore = imageObject.getImage();
        byteArrayOutputStream = new ByteArrayOutputStream();
        imageToStore.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        imageInByte = byteArrayOutputStream.toByteArray();

        ContentValues values = new ContentValues();

        values.put(UserMaster.ProfileImages.COLUMN_NAME_USERNAME, name);
        values.put(UserMaster.ProfileImages.COLUMN_NAME_IMAGE, imageInByte);

        String selection = UserMaster.ProfileImages.COLUMN_NAME_USERNAME + " like ? ";
        String[] selectionArgs = {name};

        int count = db.update(
                UserMaster.ProfileImages.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        if(count > 0){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean storeProfileImage(String name, ImageModel imageObject){

        SQLiteDatabase db = this.getWritableDatabase();
        Bitmap imageToStore = imageObject.getImage();
        byteArrayOutputStream = new ByteArrayOutputStream();
        imageToStore.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        imageInByte = byteArrayOutputStream.toByteArray();
        ContentValues values = new ContentValues();

        values.put(UserMaster.ProfileImages.COLUMN_NAME_USERNAME,name );
        values.put(UserMaster.ProfileImages.COLUMN_NAME_IMAGE, imageInByte);

        long newRowID = db.insert(UserMaster.ProfileImages.TABLE_NAME, null, values);

        if(newRowID == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public ArrayList<ImageModel> getProfileImage(String name){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ArrayList<ImageModel> imageModel = new ArrayList<>();

            Cursor cursor = db.rawQuery("select * from " + UserMaster.ProfileImages.TABLE_NAME + " where " +
                    UserMaster.ProfileImages.COLUMN_NAME_USERNAME + " = ? ", new String[]{name});
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String name2 = cursor.getString(1);
                    byte[] imageByte = cursor.getBlob(2);
                    Bitmap bitmapObject = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);//convert image byte array into bitmap for show images

                    imageModel.add(new ImageModel(bitmapObject, name2));//add to the array
                }
                return imageModel;
            } else {

                return null;
            }
        }catch (Exception e){

            return null;
        }
    }
    //add images to db
    public boolean storeImage(String name, ImageModel imageObject){

        SQLiteDatabase db = this.getWritableDatabase();
        Bitmap imageToStore = imageObject.getImage();
        byteArrayOutputStream = new ByteArrayOutputStream();
        imageToStore.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        imageInByte = byteArrayOutputStream.toByteArray();
        ContentValues values = new ContentValues();

        values.put(UserMaster.Images.COLUMN_NAME_USERNAME,name );
        values.put(UserMaster.Images.COLUMN_NAME_IMAGE, imageInByte);

        long newRowID = db.insert(UserMaster.Images.TABLE_NAME, null, values);

        if(newRowID == -1){
            return false;
        }
        else{
            return true;
        }
    }
    //get images
    public ArrayList<ImageModel> getImage(String name){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ArrayList<ImageModel> imageModel = new ArrayList<>();

            Cursor cursor = db.rawQuery("select * from " + UserMaster.Images.TABLE_NAME + " where " +
                    UserMaster.Images.COLUMN_NAME_USERNAME + " = ? ", new String[]{name});
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String name2 = cursor.getString(1);
                    byte[] imageByte = cursor.getBlob(2);
                    Bitmap bitmapObject = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);//convert image byte array into bitmap for show images

                    imageModel.add(new ImageModel(bitmapObject, name2));//add to the array
                }
                return imageModel;
            } else {

                return null;
            }
        }catch (Exception e){

            return null;
        }
    }
    public boolean deleteAccont(String name){
        SQLiteDatabase db = this.getReadableDatabase();

        String selection1 = UserMaster.ProfileImages.COLUMN_NAME_USERNAME + " like ? ";
        String selection2 = UserMaster.Images.COLUMN_NAME_USERNAME + " like ? ";
        String selection3 = UserMaster.Users.COLUMN_NAME_USERNAME + " like ? ";

        String[] selectionArgs1 = {name};
        String[] selectionArgs2 = {name};
        String[] selectionArgs3 = {name};

        int count1 = db.delete(UserMaster.ProfileImages.TABLE_NAME, selection1, selectionArgs1);
        int count2 = db.delete(UserMaster.Images.TABLE_NAME, selection2, selectionArgs2);
        int count3 = db.delete(UserMaster.Users.TABLE_NAME, selection3, selectionArgs3);

        if(count2 > 0 || count1 > 0 && count3 > 0){
            return true;
        }
        else{
            return false;
        }
    }

    //password hashing function
    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}

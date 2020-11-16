//this is signup activity
package com.example.healthcare;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecurityPermission;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final int GALLRY_REQUEST_CODE = 105;
    EditText username, password, rePassword;
    Button signup;
    Spinner spinnerBloodGroup;
    CircleImageView profilePic;
    private String blood;
    String currentPhotoPath;
    Bitmap bitmap;
    ActivityLoadingPeogressBarDialog progressBarDialog;
    DBHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        db = new DBHelper(this);
        username = (EditText) findViewById(R.id.edtSInUsername);
        spinnerBloodGroup = (Spinner) findViewById(R.id.drpSInBloodGroup);
        password = (EditText) findViewById(R.id.edtSInPassword);
        rePassword = (EditText) findViewById(R.id.edtSInRePassword);
        signup = (Button) findViewById(R.id.btnSInSignup);
        profilePic = (CircleImageView) findViewById(R.id.profile_image);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pro_pic);

        spinnerBloodGroup.setOnItemSelectedListener(this);
        progressBarDialog = new ActivityLoadingPeogressBarDialog(SignupActivity.this);

        String[] bloodGroup = getResources().getStringArray(R.array.bloodGroup);//get XML array items to string array variable
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroup);//set adapter for dropdown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodGroup.setAdapter(adapter);

        //signUp button click
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get user inputs to a variable
                final String name = username.getText().toString();
                String pwd = password.getText().toString();
                String rPwd = rePassword.getText().toString();

                if (name.equals("") || pwd.equals("") || rPwd.equals("")) {
                    Toast.makeText(SignupActivity.this, "Please Enter UN & PW!!! ", Toast.LENGTH_SHORT).show();
                } else {
                    //check password and confirm password
                    if (pwd.equals(rPwd)) {
                        Boolean checkUser = db.checkUsername(name);
                        if (checkUser == false && bitmap != null) {
                            Boolean isInsert = db.insertData(name, blood, pwd);//inserting data to db
                            boolean isInsertProfileImg = db.storeProfileImage(name, new ImageModel(bitmap, name));
                            if (isInsert == true && isInsertProfileImg == true) {
                                progressLoading();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        SessionManagment sessionManagment = new SessionManagment(SignupActivity.this);
                                        User user = new User(name);
                                        sessionManagment.setSession(user);
                                        Toast.makeText(SignupActivity.this, "Sign-up successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignupActivity.this, Dashboard.class);//go to next activity
                                        intent.putExtra("USERNAME", name);//go to next activity with username as attribute
                                        startActivity(intent);
                                    }
                                }, 2500);
//                                Toast.makeText(SignupActivity.this, "signUp successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignupActivity.this, "Error in signUp", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignupActivity.this, "User name already exist!!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignupActivity.this, "Password Not match !!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLRY_REQUEST_CODE);
            }
        });

    }

    public void progressLoading() {
        progressBarDialog.startLoadingDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBarDialog.dismissLoadingDailog();
            }
        }, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLRY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(uri);

//                profilePic.setImageURI(uri);
                bitmap = decodeUriToBitmap(this, uri);
                profilePic.setImageBitmap(bitmap);

            }
        }
    }

    //convert URI to bitmap
    public static Bitmap decodeUriToBitmap(Context mContext, Uri sendUri) {
        Bitmap getBitmap = null;
        try {
            InputStream image_stream;
            try {
                image_stream = mContext.getContentResolver().openInputStream(sendUri);
                getBitmap = BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getBitmap;
    }

    private String getFileExt(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap typeMap = MimeTypeMap.getSingleton();
        return typeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    //dropdown method
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.drpSInBloodGroup) {
            blood = adapterView.getItemAtPosition(i).toString();
            //Toast.makeText(this, bloodValue, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}

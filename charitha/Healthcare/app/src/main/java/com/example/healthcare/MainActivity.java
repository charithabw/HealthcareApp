//this is the login screen java code
package com.example.healthcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.annotation.IncompleteAnnotationException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    TextView forgotPassowrd, signup;
    EditText username, password;
    Button login;
    public static String name;
    public static String pwd;

    ActivityLoadingPeogressBarDialog progressBarDialog;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHelper(this);//initializing DB object
        username = (EditText) findViewById(R.id.edtUsername);
        password = (EditText) findViewById(R.id.edtPassword);
        login = (Button) findViewById(R.id.btnLogin);
        signup = (TextView) findViewById(R.id.txtSingup);
        forgotPassowrd = (TextView) findViewById(R.id.txtLoginForgotPassword);

        progressBarDialog = new ActivityLoadingPeogressBarDialog(MainActivity.this);

        forgotPassowrd.setVisibility(View.GONE);

        //create login button activities
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get user inputs into string variable
                name = username.getText().toString();
                pwd = password.getText().toString();

                //check UN and PW null or not
                if(name.equals("") || pwd.equals("")){
                    Toast.makeText(MainActivity.this, "Please Enter UN & PW!!! ",Toast.LENGTH_SHORT).show();
                }
                else{

                    Boolean checkuser = db.checkUsernamePasswrod(name, pwd);//call db class for check username
                    if(checkuser == true){
                        progressLoading();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //crete a session for user
                                SessionManagment sessionManagment = new SessionManagment(MainActivity.this);
                                User user = new User(name);
                                sessionManagment.setSession(user);
                                Toast.makeText(MainActivity.this, "login successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, Dashboard.class);//go to next activity
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("USERNAME", name);//go to next activity with username as attribute
                                startActivity(intent);
                                finish();
                            }
                        }, 2500);



                    }
                    else{
                        Toast.makeText(MainActivity.this, "login error", Toast.LENGTH_SHORT).show();
                        forgotPassowrd.setVisibility(View.VISIBLE);
                    }
                }


            }
        });
        //signUp button click
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);//go to next activity
                startActivity(intent);
            }
        });

        //forgot password implementation
        forgotPassowrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fgtName = username.getText().toString();
                if(!fgtName.isEmpty()){
                    boolean checkUsername = db.checkUsername(fgtName);
                    if(checkUsername){
                        Intent intent = new Intent(MainActivity.this, ForgotPassword.class);
                        intent.putExtra("USERNAME", fgtName);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Username not registered yet!!!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }
    //loading window
    public void progressLoading(){
        progressBarDialog.startLoadingDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBarDialog.dismissLoadingDailog();
            }
        }, 3000);//loading time in milisecnd
    }
    //manage login session
    //if user already logged into the system
    //may not display this login UI
    //and redirect to the dashboard
    @Override
    protected void onStart() {
        super.onStart();
        SessionManagment sessionManagment = new SessionManagment(MainActivity.this);
        boolean isLoged = sessionManagment.checkLogin();//check already logged or not

        //if logged
        if(isLoged){
            HashMap<String, String> sessionDetails = sessionManagment.getSession();
            name = sessionDetails.get(SessionManagment.SESSION_KEY);

            Intent intent = new Intent(MainActivity.this, Dashboard.class);//go to next activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("USERNAME",name);//go to next activity with username as attribute
            startActivity(intent);
        }
        //not logged or logout
        else {
            //do nothing
        }
    }



}

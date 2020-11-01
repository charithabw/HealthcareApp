package com.example.healthcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class ForgotPassword extends AppCompatActivity {

    TextView username;
    EditText password, rePassword;
    Button conform;
    public static String name;
    ActivityLoadingPeogressBarDialog progressBarDialog;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        username = (TextView) findViewById(R.id.txtFgtUsername);
        password = (EditText) findViewById(R.id.edtFgtPassword);
        rePassword = (EditText) findViewById(R.id.edtFgtRePassword);
        conform = (Button) findViewById(R.id.btnFgtConform);

        db = new DBHelper(this);

        progressBarDialog = new ActivityLoadingPeogressBarDialog(ForgotPassword.this);
        final Intent intent = getIntent();
        name = intent.getStringExtra("USERNAME");
        username.setText(name);


        conform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String name = username.getText().toString();
                String pwd = password.getText().toString();
                String rPwd = rePassword.getText().toString();

                if(name.equals("") || pwd.equals("") || rPwd.equals("")){
                    Toast.makeText(ForgotPassword.this, "Please Enter PW!!! ",Toast.LENGTH_SHORT).show();
                }
                else{
                    //check password and confirm password
                    if(pwd.equals(rPwd)){
                        Boolean checkUser = db.checkUsername(name);
                        if(checkUser == true){
                            boolean isUpdatePassword = db.updatePassword(name, pwd);
                            if(isUpdatePassword == true){
                                progressLoading();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                SessionManagment sessionManagment = new SessionManagment(ForgotPassword.this);
                                User user = new User(name);
                                sessionManagment.setSession(user);
                                Toast.makeText(ForgotPassword.this, "Update password successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ForgotPassword.this, Dashboard.class);
                                intent.putExtra("USERNAME", name);
                                startActivity(intent);
                                    }
                                }, 2500);
                            }
                            else{
                                Toast.makeText(ForgotPassword.this, "Error in changing password", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(ForgotPassword.this, "User name already exist!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(ForgotPassword.this, "Password Not match !!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
    public void progressLoading(){
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
    protected void onStart() {
        super.onStart();
        SessionManagment sessionManagment = new SessionManagment(ForgotPassword.this);
        boolean isLoged = sessionManagment.checkLogin();

        if(isLoged){
            HashMap<String, String> sessionDetails = sessionManagment.getSession();
            name = sessionDetails.get(SessionManagment.SESSION_KEY);

            Intent intent = new Intent(ForgotPassword.this, Dashboard.class);//go to next activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("USERNAME",name);//go to next activity with username as attribute
            startActivity(intent);
        }
        else {

        }
    }

}

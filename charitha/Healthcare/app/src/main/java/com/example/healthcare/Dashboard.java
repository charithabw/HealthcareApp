//this is dashboard
//after login or sign up goes to this UI
package com.example.healthcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard extends AppCompatActivity {

    private static String name;
    TextView username, password;
    //    Button update, addInformations, viewChart;
    RVAdapder rvAdapder;
    CircleImageView profilePic;
    ArrayList<ImageModel> imageModels;
    CardView addInfo, viewChart, update, example;


    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;
    ImageView sideBarProfileImage;
    TextView sideBarName;
    View hView;

    Menu settingDelete;

    AlertDialog.Builder builder;
    ActivityLoadingPeogressBarDialog progressBarDialog;

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        username = (TextView) findViewById(R.id.txtUsername);
        password = (TextView) findViewById(R.id.txtPassword);
//        update = (Button) findViewById(R.id.btnUpdate);
//        addInformations = (Button) findViewById(R.id.btnAddInfomtion);
        profilePic = (CircleImageView) findViewById(R.id.profile_imageDash);
//        viewChart = (Button) findViewById(R.id.btnChart);

        addInfo = (CardView) findViewById(R.id.addInforCardID);
        viewChart = (CardView) findViewById(R.id.viewChartsChardID);
        update = (CardView) findViewById(R.id.updateCardID);


        sideBarProfileImage = (ImageView) findViewById(R.id.profileImageSideBar);
        sideBarName = (TextView) findViewById(R.id.nameSideBar);

        db = new DBHelper(this);

        //get username form the previous activity
        final Intent intent = getIntent();
        name = intent.getStringExtra("USERNAME");


        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        hView = navigationView.getHeaderView(0);
        sideBarName = (TextView) hView.findViewById(R.id.nameSideBar);
        sideBarProfileImage = (ImageView) hView.findViewById(R.id.profileImageSideBar);


        sideBarName.setText(name);

        builder = new AlertDialog.Builder(this);
        progressBarDialog = new ActivityLoadingPeogressBarDialog(Dashboard.this);


        Cursor cursor = db.getDetails(name);//get data form db with specific username

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Not accont", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {//check the cursor one by one
                String name2 = (cursor.getString(1));
                String blood2 = (cursor.getString(2));
                //set username and blood group in the UI
                username.setText(name2);
                password.setText(blood2);
            }

            cursor.close();
        }


        //click update button
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, UpdateInfo.class);
                intent.putExtra("USERNAME", name);
                startActivity(intent);
            }
        });

        //adding information button
        addInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Dashboard.this, AddInfomation.class);
                intent.putExtra("USERNAME", name);
                startActivity(intent);

            }
        });

        imageModels = db.getProfileImage(name);
        ImageModel model = imageModels.get(0);
        Bitmap bitmap = model.getImage();
        profilePic.setImageBitmap(bitmap);
        sideBarProfileImage.setImageBitmap(bitmap);


        //side bar
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.dashboard:
                        reDerectTo(Dashboard.this, Dashboard.class);
                        break;
                    case R.id.addDetails:
                        reDerectTo(Dashboard.this, AddInfomation.class);
                        break;
                    case R.id.update:
                        reDerectTo(Dashboard.this, UpdateInfo.class);
                        break;
                    case R.id.promo_code:
                        logout();
                        break;
                    case R.id.orders:

                        break;
                    case R.id.setting:

                        break;
                    case R.id.stnDelete:
                        deletePopup();
                        break;
                    case R.id.logout:
                        logout();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

        viewChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentChart = new Intent(Dashboard.this, Charts.class);
                startActivity(intentChart);
            }
        });
    }

    //rederecting function for side bar
    public static void reDerectTo(Activity activity, Class aClass) {
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("USERNAME", name);
        activity.startActivity(intent);
    }

    //logout function
    public void logout() {
        //create alert dialog for asking logout or not
        builder.setMessage("Do you want to logout ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        progressLoading();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SessionManagment sessionManagment = new SessionManagment(Dashboard.this);
                                sessionManagment.removeSession();

                                Intent intent = new Intent(Dashboard.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }, 3000);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();

                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Alert!!!");
        alert.show();

    }

    //account delete function
    public void deletePopup() {
        builder.setMessage("Do you want to delete this account ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        boolean isDeleted = db.deleteAccont(name);
//                        progressLoading();
                        if (isDeleted) {
                            Toast.makeText(Dashboard.this, "Account Deleted", Toast.LENGTH_SHORT).show();
                            SessionManagment sessionManagment = new SessionManagment(Dashboard.this);
                            sessionManagment.removeSession();

                            Intent intent = new Intent(Dashboard.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(Dashboard.this, "Not Deleted", Toast.LENGTH_SHORT).show();
                        }
                        finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
//                        Toast.makeText(getApplicationContext(),"Alert closed.",Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Alert!!!");
        alert.show();


    }

    //loading effect
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

}

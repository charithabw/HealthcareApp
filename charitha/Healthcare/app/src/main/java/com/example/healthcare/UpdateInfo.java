package com.example.healthcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.healthcare.SignupActivity.decodeUriToBitmap;

public class UpdateInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    public static final int GALLRY_REQUEST_CODE = 105;
    TextView username;
    EditText password, rePassword;
    Spinner spinnerBloddGroud;
    Button update;
    CircleImageView profileImage;
    private static String name;
    private String blood;
    ArrayList<ImageModel> imageModels;
    public static Bitmap bitmap;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;
    ImageView sideBarProfileImage;
    TextView sideBarName;
    View hView;

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        db = new DBHelper(this);
        username = (TextView) findViewById(R.id.edtUptdUsername);
        password = (EditText) findViewById(R.id.edtUpdtPassword);
        rePassword = (EditText) findViewById(R.id.edtUpdtRePassword);
        spinnerBloddGroud = (Spinner) findViewById(R.id.drpUpdtBloodGroup);
        update = (Button) findViewById(R.id.btnUpdtUpdate);
        profileImage = (CircleImageView) findViewById(R.id.profile_imageUpdt);

        final Intent intent = getIntent();
        name = intent.getStringExtra("USERNAME");



        drawerLayout=findViewById(R.id.drawer);
        toolbar=findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView=findViewById(R.id.nav_view);
        hView = navigationView.getHeaderView(0);
        sideBarName = (TextView)hView.findViewById(R.id.nameSideBar);
        sideBarProfileImage = (ImageView)hView.findViewById(R.id.profileImageSideBar);
        sideBarName.setText(name);

        Cursor cursor = db.getDetails(name);

        imageModels = db.getProfileImage(name);
        ImageModel model = imageModels.get(0);
         bitmap = model.getImage();
        profileImage.setImageBitmap(bitmap);
        sideBarProfileImage.setImageBitmap(bitmap);

        if(cursor.getCount() == 0){
            Toast.makeText(this, "Not accont", Toast.LENGTH_SHORT).show();
        }
        else{
            while (cursor.moveToNext()){
                String name2 = (cursor.getString(1));
                String password2 = (cursor.getString(3));
                username.setText(name2);
                password.setText(password2);
            }

            cursor.close();
        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLRY_REQUEST_CODE);
            }
        });

        spinnerBloddGroud.setOnItemSelectedListener(this);

        final String[] bloodGroup = getResources().getStringArray(R.array.bloodGroup);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroup);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloddGroud.setAdapter(adapter);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = username.getText().toString();
                String pwd = password.getText().toString();
                String rPwd = rePassword.getText().toString();

                if(name.equals("") || pwd.equals("") || rPwd.equals("")){
                    Toast.makeText(UpdateInfo.this, "Please Enter UN & PW!!! ",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(pwd.equals(rPwd)){
//                        Boolean checkUser = db.checkUsername(name);

                            Boolean isUpdate = db.updateInfo(name, blood, pwd);
                            Boolean isUpdateProfileImage = db.updateProfileImage(name, new ImageModel(bitmap, name));

                            if(isUpdate == true && isUpdateProfileImage == true){
                                Toast.makeText(UpdateInfo.this, "update successefuly", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(UpdateInfo.this, "Error in updating", Toast.LENGTH_SHORT).show();
                            }

                    }
                    else{
                        Toast.makeText(UpdateInfo.this, "Password Not match !!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();

                switch (id)
                {
                    case R.id.dashboard:
                        reDerectTo(UpdateInfo.this, Dashboard.class);
                        break;
                    case R.id.addDetails:
                        reDerectTo(UpdateInfo.this, AddInfomation.class);
                        break;
                    case R.id.update:
                        reDerectTo(UpdateInfo.this, UpdateInfo.class);
                        break;
                    case R.id.promo_code:

                        break;
                    case R.id.orders:

                        break;
                    case R.id.setting:

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

    }

    public static void reDerectTo(Activity activity, Class aClass){
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("USERNAME", name);
        activity.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLRY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                Uri uri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(uri);

                bitmap = decodeUriToBitmap(this, uri);
                profileImage.setImageBitmap(bitmap);

            }
        }
    }
    private String getFileExt(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap typeMap = MimeTypeMap.getSingleton();
        return typeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.drpUpdtBloodGroup) {
            blood = adapterView.getItemAtPosition(i).toString();
            //Toast.makeText(this, blood, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public void logout(){
        SessionManagment sessionManagment = new SessionManagment(UpdateInfo.this);
        sessionManagment.removeSession();

        Intent intent = new Intent(UpdateInfo.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

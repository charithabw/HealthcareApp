//this is add information part
//this UI has camera access
package com.example.healthcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddInfomation extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    String currentPhotoPath;
    public static String name;
    ImageView uploadImage;
    Button camera, saveImg, imageView, apiButton;
    Bitmap bitmap;
    RecyclerView imageRV;
    DBHelper db;
    RVAdapder rvAdapder;
    ArrayList<ImageModel> imageModels;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;
    ImageView sideBarProfileImage;
    TextView sideBarName;
    View hView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_infomation);

        uploadImage = (ImageView) findViewById(R.id.imgUploadImg);
        camera = (Button) findViewById(R.id.btnCamera);
        saveImg = (Button) findViewById(R.id.btnSaveImg);
        imageRV = (RecyclerView) findViewById(R.id.rvImageView);
        imageView = (Button) findViewById(R.id.btnViewImg);
        apiButton = (Button) findViewById(R.id.btnApi);

//        a1 = getResources().getStringArray(R.array.bloodGroup);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermiton();
            }
        });
        db = new DBHelper(this);

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

        imageModels = db.getProfileImage(name);
        ImageModel model2 = imageModels.get(0);
        Bitmap bitmap2 = model2.getImage();
        sideBarProfileImage.setImageBitmap(bitmap2);

        //image saving in DB
        saveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!name.isEmpty() && uploadImage.getDrawable() != null && bitmap != null) {
                    boolean isInsert = db.storeImage(name, new ImageModel(bitmap, name));//call DB class
                    if (isInsert) {
                        Toast.makeText(AddInfomation.this, "image uploaded succesfuly", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddInfomation.this, "Uploding Error...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddInfomation.this, "image not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //view all taken images for particular user
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvAdapder = new RVAdapder(AddInfomation.this, db.getImage(name));//set adapter for the RecyclerView (to check uploaded image)
                if (rvAdapder != null) {
                    Toast.makeText(AddInfomation.this, "....", Toast.LENGTH_SHORT).show();
                    imageRV.setHasFixedSize(true);
                    imageRV.setAdapter(rvAdapder);
                    imageRV.setLayoutManager(new LinearLayoutManager(AddInfomation.this));

//                    imageRV.setAdapter(rvAdapder);

                } else {
                    Toast.makeText(AddInfomation.this, "Error in image", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //side bar actions
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.dashboard:
                        reDerectTo(AddInfomation.this, Dashboard.class);
                        break;
                    case R.id.addDetails:
                        reDerectTo(AddInfomation.this, AddInfomation.class);
                        break;
                    case R.id.update:
                        reDerectTo(AddInfomation.this, UpdateInfo.class);
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

        apiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(AddInfomation.this);
                String url = "http://healthcareapp.pythonanywhere.com/disease/skin/test";

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                Toast.makeText(AddInfomation.this, "Response is: " + response.substring(0, 500), Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddInfomation.this, "That didn't work!", Toast.LENGTH_SHORT).show();
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        });

    }

    //calling other pages within side bar
    public static void reDerectTo(Activity activity, Class aClass) {
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("USERNAME", name);
        activity.startActivity(intent);
    }

    //ask camera permision to open camera
    public void askCameraPermiton() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission is requered!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }
//    public void openCamera(){
//        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(camera, CAMERA_REQUEST_CODE);
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                uploadImage.setImageURI(Uri.fromFile(f));//set imageView with captured photo
                setPic();
            }
        }

    }

    //create taken image in phone memory
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    // static final int REQUEST_TAKE_PHOTO = 1;

    //open camera function
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    //set taken image in imageView
    private void setPic() {
        // Get the dimensions of the View
        int targetW = uploadImage.getWidth();
        int targetH = uploadImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        uploadImage.setImageBitmap(bitmap);
    }

    //logout function
    public void logout() {
        SessionManagment sessionManagment = new SessionManagment(AddInfomation.this);
        sessionManagment.removeSession();

        Intent intent = new Intent(AddInfomation.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

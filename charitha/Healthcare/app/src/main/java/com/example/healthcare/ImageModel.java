//all images upload and retrive through this class
//getters setters for images
package com.example.healthcare;

import android.graphics.Bitmap;

public class ImageModel {
    private Bitmap image;
    private String name;


    public ImageModel(Bitmap image, String name) {
        this.image = image;
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

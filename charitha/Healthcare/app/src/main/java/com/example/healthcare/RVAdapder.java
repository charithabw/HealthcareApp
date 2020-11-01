//this class for recyclerView (element of XML file and this is for show all images and names)
package com.example.healthcare;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RVAdapder extends RecyclerView.Adapter<RVAdapder.RVBuilder> {

    ArrayList<ImageModel> imageModels;
    String a1[];
    Context context;

    //to get names and images(to images create image model class type array)
    public RVAdapder(Context context, ArrayList<ImageModel> imageModel) {
        this.imageModels = imageModel;

        this.context = context;

    }

    //set the recyclerview builder
    @NonNull
    @Override
    public RVBuilder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater.from(context));
        View view = inflater.inflate(R.layout.single_record, parent, false);
        return new RVBuilder(view);
    }

    //setting the text view and images
    @Override
    public void onBindViewHolder(@NonNull RVBuilder holder, int position) {
        ImageModel imageObject = imageModels.get(position);
        holder.imageViewObject.setImageBitmap(imageObject.getImage());
        holder.txt1.setText(imageObject.getName());
//        holder.txt1.setText(a1[position]);

    }

    //get count of images(records)
    @Override
    public int getItemCount() {
        return imageModels.size();



    }

    public static class RVBuilder extends RecyclerView.ViewHolder{

        ImageView imageViewObject;
        TextView txt1;
        public RVBuilder(@NonNull View itemView) {
            super(itemView);
            imageViewObject = itemView.findViewById(R.id.imgSRImage);
            txt1 = itemView.findViewById(R.id.txtSRText);
        }
    }
}

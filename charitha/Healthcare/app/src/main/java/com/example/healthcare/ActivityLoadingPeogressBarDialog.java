package com.example.healthcare;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class ActivityLoadingPeogressBarDialog {
    private Activity activity;
    private AlertDialog dialog;

    ActivityLoadingPeogressBarDialog(Activity activity){
        this.activity = activity;
    }
    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.activityloadingprogress, null));
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }
    public void dismissLoadingDailog(){
        dialog.dismiss();
    }
}

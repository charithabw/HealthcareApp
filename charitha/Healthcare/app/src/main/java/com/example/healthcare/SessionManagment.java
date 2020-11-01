package com.example.healthcare;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManagment {
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static String IS_LOGGED = "isLogin";
    public static final String SESSION_KEY = "session_user";

    public SessionManagment(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("userLoginSession", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public void setSession(User user){
        editor.putBoolean(IS_LOGGED, true);
        String name = user.getName();
        editor.putString(SESSION_KEY, name);
        editor.commit();

    }
    public HashMap<String, String> getSession(){
        HashMap<String, String> userName = new HashMap<>();
        userName.put(SESSION_KEY, sharedPreferences.getString(SESSION_KEY, null));

        return userName;
    }
    public boolean checkLogin(){
        if(sharedPreferences.getBoolean(IS_LOGGED, false)){
            return true;
        }
        else {
            return false;
        }
    }
    public void removeSession(){
        editor.clear();
        editor.commit();
    }
}

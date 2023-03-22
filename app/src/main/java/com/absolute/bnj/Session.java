package com.absolute.bnj;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    SharedPreferences sharedpreferences;
    public Session(Context context) {
        this.sharedpreferences = context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
    }
    public Boolean getIntroSliderStatus() {
        //if already showed then return true
        return  sharedpreferences.getBoolean("INTRO_SLIDER_STATUS", false);
    }

    public void setIntroSliderStatus(boolean status) {
        SharedPreferences.Editor ed = sharedpreferences.edit();
        ed.putBoolean("INTRO_SLIDER_STATUS", status);
        ed.apply();
    }
    public void setFirebaseToken(String token){
        SharedPreferences.Editor ed = sharedpreferences.edit();
        ed.putString("FIREBASE_TOKEN", token);
        ed.apply();
    }

    public String getFirebaseToken() {
        return  sharedpreferences.getString("FIREBASE_TOKEN", "");
    }
    public void saveLoginInfo(String mobile, String userid, String token){
        SharedPreferences.Editor ed = sharedpreferences.edit();
        ed.putString("MOBILE", mobile);
        ed.putString("USERID", userid);
        ed.putString("JWT_TOKEN", token);
        ed.apply();
    }
    public String getMobile(){
        return sharedpreferences.getString("MOBILE", "");
    }
    public String getJWTToken(){
        return sharedpreferences.getString("JWT_TOKEN", "");
    }
    public String getUserid(){
        return sharedpreferences.getString("USERID", "");
    }

    public void logoutSession(){
        SharedPreferences.Editor ed = sharedpreferences.edit();
        ed.clear();
        ed.apply();
    }
}

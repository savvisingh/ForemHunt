package com.example.sarabjeetsingh.beacondetector;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Savvi on 6/22/2015.
 */
public class ZPreferences {

    private static final String KEY = "hjk.prefs";
    private static final String IS_USER_LOGIN = "is_user_login";
    private static final String USER_TOKEN = "user_token";
    private static final String USER_Name = "user_name";
    private static final String USER_EMAIL = "user_email";
    private static final String USER_LAT = "user_lat";
    private static final String USER_LONG = "user_long";
    private static final String USER_GENDER = "user_gender";
    private static final String USER_PicURL = "user_ProfilePicUrl";
    private static final String AUTH_TYPE="auth_type";


    public static void setIsUserLogin(Context context, boolean isUserSignUp) {
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
                .edit();
        editor.putBoolean(IS_USER_LOGIN, isUserSignUp);
        editor.commit();
    }

    public static boolean isUserLogIn(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(KEY,
                Context.MODE_PRIVATE);
        return savedSession.getBoolean(IS_USER_LOGIN, false);
    }

    public static void setUserName(Context context, String name) {
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
                .edit();
        editor.putString(USER_Name, name);
        editor.commit();
    }

    public static String getUserName(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(KEY,
                Context.MODE_PRIVATE);
        return savedSession.getString(USER_Name, "");
    }


    public static void setAuthType(Context context, String name) {
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
                .edit();
        editor.putString(AUTH_TYPE, name);
        editor.commit();
    }

    public static String getAuthType(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(KEY,
                Context.MODE_PRIVATE);
        return savedSession.getString(AUTH_TYPE, "");
    }

    public static void setUserGender(Context context, String gender) {
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
                .edit();
        editor.putString(USER_GENDER, gender);
        editor.commit();
    }

    public static String getUserGender(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(KEY,
                Context.MODE_PRIVATE);
        return savedSession.getString(USER_GENDER, "");
    }

    public static void setUserProfilePicURL(Context context, String url) {
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
                .edit();
        editor.putString(USER_PicURL,url);
        editor.commit();
    }

    public static String getUserProfilePicURL(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(KEY,
                Context.MODE_PRIVATE);
        return savedSession.getString(USER_PicURL, "");
    }

    public static void setUseremail(Context context, String name) {
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
                .edit();
        editor.putString(USER_EMAIL, name);
        editor.commit();
    }

    public static String getUseremail(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(KEY,
                Context.MODE_PRIVATE);
        return savedSession.getString(USER_EMAIL, "");
    }
    public static void setUserToken(Context context, String token) {
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
                .edit();
        editor.putString(USER_TOKEN, token);
        editor.commit();
    }

    public static String getUserToken(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(KEY,
                Context.MODE_PRIVATE);
        return savedSession.getString(USER_TOKEN, "");
    }


    public static void setUserLat(Context context, String token) {
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
                .edit();
        editor.putString(USER_LAT, token);
        editor.commit();
    }

    public static String getUserLat(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(KEY,
                Context.MODE_PRIVATE);
        return savedSession.getString(USER_LAT, "");
    }

    public static void setUserLong(Context context, String token) {
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
                .edit();
        editor.putString(USER_LONG, token);
        editor.commit();
    }

    public static String getUserLong(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(KEY,
                Context.MODE_PRIVATE);
        return savedSession.getString(USER_LONG, "");
    }



}

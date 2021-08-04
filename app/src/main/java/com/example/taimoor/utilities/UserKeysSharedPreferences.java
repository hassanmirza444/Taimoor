package com.example.taimoor.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.taimoor.MainApplication;


public class UserKeysSharedPreferences {

    private SharedPreferences sharedPreferences;
    private static UserKeysSharedPreferences appPreferences;


    private UserKeysSharedPreferences() {
    }

    public static UserKeysSharedPreferences getInstance() {
        if (appPreferences == null) {
            appPreferences = new UserKeysSharedPreferences();
            if (appPreferences.sharedPreferences == null) {
                Context context = MainApplication.getContext();
                appPreferences.sharedPreferences = context.getSharedPreferences("user_keys", Context.MODE_PRIVATE);
            }
        }
        return appPreferences;
    }

    public void removeKey(String key) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().remove(key).apply();
        }
    }

    public void clear() {
        if (sharedPreferences != null) {
            sharedPreferences.edit().clear().apply();
        }
    }

    public boolean contains(String key) {

        if (sharedPreferences.contains(key))
            return true;
        else
            return false;
    }


    public void setString(String key, String value) {
        //  Logger.e("UserKeys", key.concat(" = ").concat(value));
        sharedPreferences.edit().putString(key.trim(), value.trim()).apply();
    }

    public String getString(String key) {
        //  Logger.e("UserKeys", ("Getting value for = ").concat(key));
        return sharedPreferences.getString(key.trim(), "").trim();
    }





    public void setInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public void setLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, -1);
    }

    public void setBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void setFloat(String key, float value) {
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    public float getFloat(String key) {
        return sharedPreferences.getFloat(key, 0);
    }
}

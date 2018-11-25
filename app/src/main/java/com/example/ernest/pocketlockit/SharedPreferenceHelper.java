package com.example.ernest.pocketlockit;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {

    private SharedPreferences sharedPreferences;

    public SharedPreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("TogglePreference", Context.MODE_PRIVATE);
    }

    public void saveToggleValue(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("toggleValue", value);
        editor.apply();
    }

    public boolean getToggleValue() {
        return sharedPreferences.getBoolean("toggleValue", false);
    }

    public void saveLockValue(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("lockValue", value);
        editor.apply();
    }

    public boolean getLockValue() {
        return sharedPreferences.getBoolean("lockValue", false);
    }


}
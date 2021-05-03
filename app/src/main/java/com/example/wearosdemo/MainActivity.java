package com.example.wearosdemo;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends WearableActivity {
    private static final String TAG = "MainActivity";
    private static final String pref_name = "MAIN_PREFERENCE", launch_pref = "FIRST_LAUNCH";
    private static final int requestCode = 101;
    private static final String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        Log.i(TAG, "In onCreate");

        if(_isPermissionsAllowed())
            _launchActivity();
        else
            _launchPermissionsRequst();
        // Enables Always-on
        setAmbientEnabled();
    }

    private boolean _isPermissionsAllowed() {
        Log.i(TAG, "In _isPermissionsAllowed");
        for (String iter : permissions)
        {
            if (ContextCompat.checkSelfPermission(this, iter) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    private void _launchActivity() {
        Log.i(TAG, "In _launchActivity");
        if (_isFirstLaunch())
        {
            Log.i(TAG, "First launch scenario");
            SharedPreferences pref = getSharedPreferences(pref_name, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(launch_pref, false);
            editor.apply();
        }
        else
            Log.i(TAG, "Regular launch scenario");
        finish();
    }

    private boolean _isFirstLaunch() {
        SharedPreferences pref = getSharedPreferences(pref_name, MODE_PRIVATE);
        return pref.getBoolean(launch_pref, true);
    }

    private void _launchPermissionsRequst() {
        Log.i(TAG, "In _launchPermissionsRequst");
        List<String> pList = new ArrayList<>();
        for (String perm : permissions)
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED)
                pList.add(perm);
        if (pList.size() == 0)
            Log.e(TAG, "Permission List is empty");
        else
            ActivityCompat.requestPermissions(this, pList.toArray(new String[pList.size()]), requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MainActivity.requestCode)
        {
            if (grantResults.length > 0)
            {
                for (int result : grantResults)
                {
                    if (result == PackageManager.PERMISSION_DENIED)
                        finish();
                }
                _launchActivity();
            }
        }
    }
}
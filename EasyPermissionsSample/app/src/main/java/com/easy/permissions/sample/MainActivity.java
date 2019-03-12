package com.easy.permissions.sample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.rystal.blaze.easypermission.EasyPermissions;

import java.util.List;

import demo.esay.permissions.sample.R;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*EasyPermissions.requestPermissions(this,
                11,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        );*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        for (String per : perms) {
            Log.e("MainActivity", "onPermissionsGranted --> requestCode = " + requestCode + "-- perms = " + per);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        for (String per : perms) {
            Log.e("MainActivity", "onPermissionsDenied --> requestCode = " + requestCode + "-- perms = " + per);
        }

    }

    @Override
    public void onPermissionsDeniedNeverAskAgain(int requestCode, @NonNull List<String> perms) {
        for (String per : perms) {
            Log.e("MainActivity", "onPermissionsDeniedNeverAskAgain --> requestCode = " + requestCode + "-- perms = " + per);
        }
    }
}

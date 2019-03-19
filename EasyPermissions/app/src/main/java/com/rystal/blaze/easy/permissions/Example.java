package com.rystal.blaze.easy.permissions;

import android.support.annotation.NonNull;
import android.util.Log;

import com.rystal.blaze.easy.permission.PermissionCallbacks;

import java.util.List;

/**
 * com.rystal.blaze.easy.permissions
 * Author: GaoQiang
 * Time: 2019/3/15 9:44
 * Description:
 */
public class Example implements PermissionCallbacks {
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> requestPerms, @NonNull List<String> perms) {
        for (String per : perms) {
            Log.e("Example", "onPermissionsGranted --> requestCode = " + requestCode + "-- perms = " + per);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> requestPerms, @NonNull List<String> perms) {
        for (String per : perms) {
            Log.e("Example", "onPermissionsDenied --> requestCode = " + requestCode + "-- perms = " + per);
        }
    }

    @Override
    public void onPermissionsDeniedNeverAskAgain(int requestCode, @NonNull List<String> requestPerms, @NonNull List<String> perms) {
        for (String per : perms) {
            Log.e("Example", "onPermissionsDeniedNeverAskAgain --> requestCode = " + requestCode + "-- perms = " + per);
        }
    }
}

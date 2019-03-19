package com.rystal.blaze.easy.permission.helper;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

/**
 * com.rystal.blaze.easy.permission.helper
 * Time: 2019/3/19 14:01
 * Description:
 */
public class ActivityPermHelper extends PermissionHelper<Activity> {

    public ActivityPermHelper(Activity activity) {
        super(activity);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        if (getHost() == null) {
            return false;
        }
        return ActivityCompat.shouldShowRequestPermissionRationale(getHost(), permission);
    }

    @Override
    public void requestPermissions(int requestCode, @NonNull String... perms) {
        if (getContext() == null) {
            return;
        }
        String[] denyPerms = getDeniedPermissions(getContext(), perms);
        if (denyPerms.length > 0) {
            ActivityCompat.requestPermissions(getHost(),
                    denyPerms,
                    requestCode);
        }
    }

    @Override
    public Context getContext() {
        return getHost();
    }
}

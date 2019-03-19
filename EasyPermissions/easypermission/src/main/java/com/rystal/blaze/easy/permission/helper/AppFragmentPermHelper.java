package com.rystal.blaze.easy.permission.helper;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

/**
 * com.rystal.blaze.easy.permission.helper
 * Time: 2019/3/19 13:55
 * Description:
 */
public class AppFragmentPermHelper extends PermissionHelper<Fragment> {

    AppFragmentPermHelper(Fragment fragment) {
        super(fragment);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        if (getHost() == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getHost().shouldShowRequestPermissionRationale(permission);
        } else
            return ActivityCompat.shouldShowRequestPermissionRationale(getHost().getActivity(), permission);
    }

    @Override
    public void requestPermissions(int requestCode, @NonNull String... perms) {
        if (getContext() == null) {
            return;
        }
        String[] denyPerms = getDeniedPermissions(getContext(), perms);
        if (denyPerms.length > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getHost().requestPermissions(
                    denyPerms,
                    requestCode);
        }
    }

    @Override
    public Context getContext() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getHost() != null) {
            return getHost().getContext();
        } else if (getHost() != null) {
            return getHost().getActivity();
        } else
            return null;
    }
}

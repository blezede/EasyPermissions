package com.rystal.blaze.easy.permission.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 * com.rystal.blaze.easy.permission.helper
 * Time: 2019/3/19 13:57
 * Description:
 */
public class V4FragmentPermHelper extends PermissionHelper<Fragment> {

    public V4FragmentPermHelper(Fragment fragment) {
        super(fragment);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        if (getHost() == null) {
            return false;
        }
        return getHost().shouldShowRequestPermissionRationale(permission);
    }

    @Override
    public void requestPermissions(int requestCode, @NonNull String... perms) {
        if (getContext() == null) {
            return;
        }
        String[] denyPerms = getDeniedPermissions(getContext(), perms);
        if (denyPerms.length > 0) {
            getHost().requestPermissions(
                    denyPerms,
                    requestCode);
        }
    }

    @Override
    public Context getContext() {
        if (getHost() != null) {
            return getHost().getContext();
        } else
            return null;
    }
}

package com.rystal.blaze.easy.permission;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * com.rystal.blaze.easy.permission
 * Time: 2019/3/15 10:01
 * Description:
 * Callback interface to receive the results of {@code EasyPermissions.requestPermissions()}
 * calls.
 */
public interface PermissionCallbacks {

    /**
     * Permissions granted
     *
     * @param requestCode  the requestCode
     * @param requestPerms all permissions we requested.
     * @param perms        the permissions that user granted.
     */
    void onPermissionsGranted(int requestCode, @NonNull List<String> requestPerms, @NonNull List<String> perms);

    /**
     * Some permissions denied
     *
     * @param requestCode  the requestCode
     * @param requestPerms all permissions we requested.
     * @param perms        the permissions that user denied.
     */
    void onPermissionsDenied(int requestCode, @NonNull List<String> requestPerms, @NonNull List<String> perms);

    /**
     * Permissions denied when user clicked don't ask again item.
     * Denied forever.
     *
     * @param requestCode  the requestCode
     * @param requestPerms all permissions we requested.
     * @param perms        the permissions that user forever denied.
     */
    void onPermissionsDeniedNeverAskAgain(int requestCode, @NonNull List<String> requestPerms, @NonNull List<String> perms);

}

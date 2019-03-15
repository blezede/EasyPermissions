package com.rystal.blaze.easy.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * com.rystal.blaze.easypermission
 * Time: 2019/3/6 14:54
 * Description:Easy permission util
 */
public class EasyPermissions {

    private static final int Zero = 0;
    private static final int DEFAULT_RC = -0x1001;
    private static List<String> sDeniedPermissionList = new ArrayList<>();
    private static int sRequestCode = DEFAULT_RC;
    private static String[] sPermissions;

    private static List<PermissionCallbacks> sPermissionCallbacksList = new ArrayList<>();

    private static List<RationaleCallbacks> sRationaleCallbacksList = new ArrayList<>();

    /**
     * Check whether have some permissions
     *
     * @param permissions
     * @return
     */
    public static boolean checkSelfPermission(Context context, String... permissions) {
        if (context == null || permissions == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPermissionGranted(int[] grantResults) {
        if (grantResults == null || grantResults.length <= 0) {
            return false;
        }
        for (int re : grantResults) {
            if (re != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Whether have some permissions
     *
     * @param permissions
     * @return
     */
    private static boolean hasPermission(Context context, String... permissions) {
        if (context == null || permissions == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        sDeniedPermissionList.clear();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                sDeniedPermissionList.add(permission);
            }
        }
        return sDeniedPermissionList.size() == 0;
    }

    /**
     * Request permissions
     *
     * @param t
     * @param requestCode
     * @param permissions
     * @return
     */
    public static <T, K> void requestPermissions(T t, int requestCode, String[] permissions, K... k) {
        sRequestCode = requestCode;
        sPermissions = permissions;
        if (t == null || permissions == null || permissions.length <= 0) {
            release();
            return;
        }
        sPermissionCallbacksList.clear();
        sRationaleCallbacksList.clear();
        if (k != null && k.length > 0) {
            for (K callBack : k) {
                if (callBack instanceof PermissionCallbacks) {
                    sPermissionCallbacksList.add((PermissionCallbacks) callBack);
                }
                if (callBack instanceof RationaleCallbacks) {
                    sRationaleCallbacksList.add((RationaleCallbacks) callBack);
                }
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (sPermissionCallbacksList != null && sPermissionCallbacksList.size() > Zero) {
                for (PermissionCallbacks callbacks : sPermissionCallbacksList) {
                    if (callbacks != null) {
                        callbacks.onPermissionsGranted(requestCode, Arrays.asList(sPermissions), Arrays.asList(sPermissions));
                        release();
                    }
                }
            }
            return;
        }
        if (t instanceof Activity) {
            requestActivityPerms((Activity) t, sPermissions, requestCode);
        } else if (t instanceof android.support.v4.app.Fragment) {
            requestFragmentPerms((android.support.v4.app.Fragment) t, sPermissions, requestCode);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && t instanceof android.app.Fragment) {
            requestFragmentPerms((android.app.Fragment) t, sPermissions, requestCode);
        }
    }

    private static void requestActivityPerms(Activity act, String[] perms, int requestCode) {
        if (!hasPermission(act, perms)) {
            ActivityCompat.requestPermissions(act,
                    sDeniedPermissionList.toArray(new String[sDeniedPermissionList.size()]),
                    requestCode);
        }
    }

    private static void requestFragmentPerms(android.support.v4.app.Fragment frag, String[] perms, int requestCode) {
        if (!hasPermission(frag.getActivity(), perms)) {
            frag.requestPermissions(sDeniedPermissionList.toArray(new String[sDeniedPermissionList.size()]),
                    requestCode);
        }
    }

    private static void requestFragmentPerms(android.app.Fragment frag, String[] perms, int requestCode) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
            if (!hasPermission(frag.getActivity(), perms)) {
                frag.requestPermissions(sDeniedPermissionList.toArray(new String[sDeniedPermissionList.size()]),
                        requestCode);
            }
    }

    private static boolean shouldShowRequestPermissions(Activity activity, String... permissions) {
        if (activity == null || permissions == null) {
            return false;
        }
        for (String perm : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)) {
                return true;
            }
        }
        return false;
    }

    public static void onRequestPermissionsResult(Activity act, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == sRequestCode) {
            if (isPermissionGranted(grantResults)) {
                if (sPermissionCallbacksList != null && sPermissionCallbacksList.size() > Zero) {
                    for (PermissionCallbacks callbacks : sPermissionCallbacksList) {
                        if (callbacks != null) {
                            callbacks.onPermissionsGranted(requestCode, Arrays.asList(sPermissions), Arrays.asList(permissions));
                        }
                    }
                }
            } else {
                if (sPermissions == null) {
                    release();
                    return;
                }
                List<String> granted = new ArrayList<>();
                List<String> denied = new ArrayList<>();
                List<String> dontAskDenied = new ArrayList<>();
                for (int i = 0; i < sPermissions.length; i++) {
                    String perm = sPermissions[i];
                    if (ContextCompat.checkSelfPermission(act, perm) == PackageManager.PERMISSION_GRANTED) {
                        granted.add(perm);
                    } else {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(act, perm)) {
                            dontAskDenied.add(perm);
                        } else {
                            denied.add(perm);
                        }
                    }
                }
                if (granted.size() > 0 && sPermissionCallbacksList != null && sPermissionCallbacksList.size() > Zero) {
                    for (PermissionCallbacks callbacks : sPermissionCallbacksList) {
                        if (callbacks != null) {
                            callbacks.onPermissionsGranted(requestCode, Arrays.asList(sPermissions), granted);
                        }
                    }
                }
                if (dontAskDenied.size() > 0 && sPermissionCallbacksList != null && sPermissionCallbacksList.size() > Zero) {
                    for (PermissionCallbacks callbacks : sPermissionCallbacksList) {
                        if (callbacks != null) {
                            callbacks.onPermissionsDeniedNeverAskAgain(requestCode, Arrays.asList(sPermissions), dontAskDenied);
                        }
                    }
                }
                if (denied.size() > 0 && sPermissionCallbacksList != null && sPermissionCallbacksList.size() > Zero) {
                    for (PermissionCallbacks callbacks : sPermissionCallbacksList) {
                        if (callbacks != null) {
                            callbacks.onPermissionsDenied(requestCode, Arrays.asList(sPermissions), denied);
                        }
                    }
                }
            }
            release();
        }
    }

    private static void release() {
        sPermissionCallbacksList.clear();
        sRationaleCallbacksList.clear();
        sRequestCode = DEFAULT_RC;
        sPermissions = null;
    }
}

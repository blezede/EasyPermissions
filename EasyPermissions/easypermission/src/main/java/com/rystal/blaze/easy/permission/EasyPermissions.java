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

    private static final int DEFAULT_RC = -0x1001;
    private static List<String> sDeniedPermissionList = new ArrayList<>();
    private static int sRequestCode = DEFAULT_RC;

    private static PermissionCallbacks sPermissionCallbacks;

    private static RationaleCallbacks sRationaleCallbacks;

    /**
     * Callback interface to receive the results of {@code EasyPermissions.requestPermissions()}
     * calls.
     */
    public interface PermissionCallbacks {

        /**
         * All permissions granted
         *
         * @param requestCode
         * @param perms
         */
        void onPermissionsGranted(int requestCode, @NonNull List<String> perms);

        /**
         * Some permissions common denied
         *
         * @param requestCode
         * @param perms
         */
        void onPermissionsDenied(int requestCode, @NonNull List<String> perms);

        /**
         * All permissions denied when user clicked don't ask again item
         *
         * @param requestCode
         * @param perms
         */
        void onPermissionsDeniedNeverAskAgain(int requestCode, @NonNull List<String> perms);
    }

    /**
     * Callback interface to receive button clicked events of the rationale dialog
     *
     * @deprecated
     */
    public interface RationaleCallbacks {
        void onRationaleAccepted(int requestCode);

        void onRationaleDenied(int requestCode);
    }


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
    public static <T> void requestPermissions(T t, int requestCode, String... permissions) {
        sRequestCode = requestCode;
        if (t == null || permissions == null || permissions.length <= 0) {
            return;
        }
        if (t instanceof PermissionCallbacks) {
            sPermissionCallbacks = (PermissionCallbacks) t;
        }
        if (t instanceof RationaleCallbacks) {
            sRationaleCallbacks = (RationaleCallbacks) t;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (sPermissionCallbacks != null) {
                sPermissionCallbacks.onPermissionsGranted(requestCode, Arrays.asList(permissions));
            }
            return;
        }
        if (t instanceof Activity) {
            requestActivityPerms((Activity) t, permissions, requestCode);
        } else if (t instanceof android.support.v4.app.Fragment) {
            requestFragmentPerms((android.support.v4.app.Fragment) t, permissions, requestCode);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && t instanceof android.app.Fragment) {
            requestFragmentPerms((android.app.Fragment) t, permissions, requestCode);

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
                if (sPermissionCallbacks != null) {
                    sPermissionCallbacks.onPermissionsGranted(requestCode, Arrays.asList(permissions));
                }
            } else {
                List<String> granted = new ArrayList<>();
                List<String> denied = new ArrayList<>();
                List<String> dontAskDenied = new ArrayList<>();
                for (int i = 0; i < permissions.length; i++) {
                    String perm = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        granted.add(perm);
                    } else {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(act, perm)) {
                            dontAskDenied.add(perm);
                        }
                        denied.add(perm);
                    }
                }
                if (!shouldShowRequestPermissions(act, permissions)) {
                    if (sPermissionCallbacks != null) {
                        sPermissionCallbacks.onPermissionsDeniedNeverAskAgain(requestCode, dontAskDenied);
                    }
                    return;
                }
                if (sPermissionCallbacks != null) {
                    sPermissionCallbacks.onPermissionsDenied(requestCode, denied);
                }
            }
            sPermissionCallbacks = null;
            sRationaleCallbacks = null;
            sRequestCode = DEFAULT_RC;
        }
    }
}

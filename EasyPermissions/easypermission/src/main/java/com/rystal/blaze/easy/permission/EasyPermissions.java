package com.rystal.blaze.easy.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.rystal.blaze.easy.permission.helper.PermissionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * com.rystal.blaze.easypermission
 * Time: 2019/3/6 14:54
 * Description:Easy permission util
 */
public class EasyPermissions {

    private static final String TAG = "EasyPermissions";

    private static final int Zero = 0;
    private static final int DEFAULT_RC = -0x1001;
    private static int sRequestCode = DEFAULT_RC;
    private static String[] sPermissions;

    private static List<PermissionCallbacks> sPermissionCallbacksList = new ArrayList<>();

    private static List<RationaleCallbacks> sRationaleCallbacksList = new ArrayList<>();


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
     * Request permissions
     *
     * @param t
     * @param requestCode
     * @param permissions
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
            PermissionHelper.newInstance((Activity) t).requestPermissions(requestCode, sPermissions);
        } else if (t instanceof android.support.v4.app.Fragment) {
            PermissionHelper.newInstance((android.support.v4.app.Fragment) t).requestPermissions(requestCode, sPermissions);
        } else if (t instanceof android.app.Fragment) {
            PermissionHelper.newInstance((android.app.Fragment) t).requestPermissions(requestCode, sPermissions);
        }
    }

    public static void onRequestPermissionsResult(Activity act, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == sRequestCode) {
            if (isPermissionGranted(grantResults)) {
                if (sPermissionCallbacksList != null && sPermissionCallbacksList.size() > Zero) {
                    for (PermissionCallbacks callbacks : sPermissionCallbacksList) {
                        if (callbacks != null) {
                            callbacks.onPermissionsGranted(requestCode, Arrays.asList(sPermissions), Arrays.asList(permissions));
                            runAnnotatedMethods(callbacks, requestCode);
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

    private static void runAnnotatedMethods(@NonNull Object object, int requestCode) {
        Class clazz = object.getClass();
        if (isUsingAndroidAnnotations(object)) {
            clazz = clazz.getSuperclass();
        }

        while (clazz != null) {
            for (Method method : clazz.getDeclaredMethods()) {
                AfterAllPermissionsGranted ann = method.getAnnotation(AfterAllPermissionsGranted.class);
                if (ann != null) {
                    // Check for annotated methods with matching request code.
                    if (ann.value() == requestCode) {
                        // Method must be void so that we can invoke it
                        if (method.getParameterTypes().length > 0) {
                            throw new RuntimeException(
                                    "Cannot execute method " + method.getName() + " because it is non-void method and/or has input parameters.");
                        }

                        try {
                            // Make method accessible if private
                            if (!method.isAccessible()) {
                                method.setAccessible(true);
                            }
                            method.invoke(object);
                        } catch (IllegalAccessException e) {
                            Log.e(TAG, "runDefaultMethod:IllegalAccessException", e);
                        } catch (InvocationTargetException e) {
                            Log.e(TAG, "runDefaultMethod:InvocationTargetException", e);
                        }
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Determine if the project is using the AndroidAnnotations library.
     */
    private static boolean isUsingAndroidAnnotations(@NonNull Object object) {
        if (!object.getClass().getSimpleName().endsWith("_")) {
            return false;
        }
        try {
            Class clazz = Class.forName("org.androidannotations.api.view.HasViews");
            return clazz.isInstance(object);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static void release() {
        sPermissionCallbacksList.clear();
        sRationaleCallbacksList.clear();
        sRequestCode = DEFAULT_RC;
        sPermissions = null;
    }
}

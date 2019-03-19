package com.rystal.blaze.easy.permission.helper;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * com.rystal.blaze.easy.permission.helper
 * Time: 2019/3/19 13:52
 * Description:
 */
public abstract class PermissionHelper<T> {

    private T mHost;

    public PermissionHelper(@NonNull T t) {
        this.mHost = t;
    }

    public static PermissionHelper<? extends Activity> newInstance(@NonNull Activity activity) {
        return new ActivityPermHelper(activity);
    }

    public static PermissionHelper<? extends Fragment> newInstance(@NonNull Fragment fragment) {
        return new AppFragmentPermHelper(fragment);
    }

    public static PermissionHelper<? extends android.support.v4.app.Fragment> newInstance(@NonNull android.support.v4.app.Fragment fragment) {
        return new V4FragmentPermHelper(fragment);
    }

    public abstract boolean shouldShowRequestPermissionRationale(@NonNull String permission);

    public abstract void requestPermissions(int requestCode, @NonNull String... perms);

    public T getHost() {
        return mHost;
    }

    public String[] getDeniedPermissions(@NonNull Context context, @NonNull String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return new String[0];
        }
        List<String> deniedPermsList = new ArrayList<>();
        deniedPermsList.clear();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermsList.add(permission);
            }
        }
        return deniedPermsList.toArray(new String[deniedPermsList.size()]);
    }

    public abstract Context getContext();
}

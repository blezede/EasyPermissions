package com.rystal.blaze.easy.permission;

/**
 * com.rystal.blaze.easy.permission
 * Time: 2019/3/15 10:04
 * Description:
 *
 * @deprecated
 */
public interface RationaleCallbacks {

    void onRationaleAccepted(int requestCode);

    void onRationaleDenied(int requestCode);
}

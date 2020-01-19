package com.beacon.location.Utils;

/**
 * Author Qumoy
 * Create Date 2019/7/24
 * Description：PermissionInterface
 * Modifier:
 * Modify Date:
 * Bugzilla Id:
 * Modify Content:
 */
public interface PermissionInterface {
    /**
     * 可得到请求权限请求码
     */
    int getPermissionsRequestCode();

    /**
     * 请求权限成功回调
     */
    void requestPermissionsSuccess();

    /**
     * 请求权限失败回调
     */
    void requestPermissionsFail();

}


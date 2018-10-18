package com.playgilround.schedule.client.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 18-06-20
 * Toast 유틸기능
 */
public class ToastUtils {

    private static Toast mToast;

    public static void showShortToast(Context context, int resId) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        mToast.show();
    }
}

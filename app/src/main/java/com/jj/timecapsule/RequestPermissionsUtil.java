package com.example.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.Manifest;
import android.os.Build;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.annotation.RequiresApi;

public class RequestPermissionsUtil {

    private Context context;
    private final int REQUEST_LOCATION = 1;

    @RequiresApi(Build.VERSION_CODES.Q)
    private String[] permissionsLocationUpApi29Impl = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };

    @TargetApi(Build.VERSION_CODES.P)
    private String[] permissionsLocationDownApi29Impl = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public RequestPermissionsUtil(Context context) {
        this.context = context;
    }

    /** 위치정보 권한 요청 */
    public void requestLocation() {
        if (Build.VERSION.SDK_INT >= 29) {
            if (ActivityCompat.checkSelfPermission(context, permissionsLocationUpApi29Impl[0]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, permissionsLocationUpApi29Impl[1]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, permissionsLocationUpApi29Impl[2]) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        (Activity) context,
                        permissionsLocationUpApi29Impl,
                        REQUEST_LOCATION
                );
            }
        } else {
            if (ActivityCompat.checkSelfPermission(context, permissionsLocationDownApi29Impl[0]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, permissionsLocationDownApi29Impl[1]) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        (Activity) context,
                        permissionsLocationDownApi29Impl,
                        REQUEST_LOCATION
                );
            }
        }
    }
}

package com.axioConsulting.cameracontrol.app.utils;

import android.content.SharedPreferences;
import com.axioConsulting.cameracontrol.app.SettingsActivity;
import com.axioConsulting.cameracontrol.app.bean.CameraLogin;

/**
 * Created by benoit on 19/09/15.
 */
public class CameraLoginUtils {

    public static CameraLogin retrieveLogin(SharedPreferences sharedPreferences){
        CameraLogin cameraLogin=new CameraLogin();
        cameraLogin.setLogin(sharedPreferences.getString(SettingsActivity.PERF_KEY_CREDENTIALS_LOGIN,""));
        cameraLogin.setPassword(sharedPreferences.getString(SettingsActivity.PERF_KEY_CREDENTIALS_PASSWORD,""));
        return cameraLogin;
    }
}

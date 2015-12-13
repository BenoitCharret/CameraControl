package com.axioConsulting.cameracontrol.app.network.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by benoit on 03/09/15.
 */
public class WifiHelper {

    private static final String TAG = WifiHelper.class.getName();

    public static String getCurrentSsid(Context context) {

        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !(connectionInfo.getSSID().equals(""))) {
                //if (connectionInfo != null && !StringUtil.isBlank(connectionInfo.getSSID())) {
                Log.v(TAG, "connectioninfo: " + connectionInfo.getSSID().replace("\"", ""));
                return connectionInfo.getSSID().replace("\"", "");
            }
            // Get WiFi status MARAKANA
            WifiInfo info = wifiManager.getConnectionInfo();
            Log.v(TAG, "wifiinfo: " + info.getSSID().replace("\"", ""));
            return info.getSSID().replace("\"", "");
        }
        return null;
    }
}

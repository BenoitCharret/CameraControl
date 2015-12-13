package com.axioConsulting.cameracontrol.app;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by benoit on 19/09/15.
 */
public class SettingsActivity extends Activity {

    public static final String PREF_KEY_ZABBIX_HOST="pref_key_zabbix_host";
    public static final String PREF_KEY_ZABBIX_PORT="pref_key_zabbix_port";
    public static final String PREF_KEY_WIFI_SSID="pref_key_wifi_ssid";
    public static final String PERF_KEY_CREDENTIALS_LOGIN="perf_key_credentials_login";
    public static final String PERF_KEY_CREDENTIALS_PASSWORD="perf_key_credentials_password";
    public static final String PREF_KEY_ZABBIX_HOSTGROUP="pref_key_zabbix_hostgroup";
    public static final String PREF_KEY_ZABBIX_LOGIN="pref_key_zabbix_login";
    public static final String PREF_KEY_ZABBIX_PASSWORD="pref_key_zabbix_password";

    public static final String PERF_KEY_GATEWAY_HOST="perf_key_gateway_host";
    public static final String PERF_KEY_GATEWAY_PORT="perf_key_gateway_port";
    public static final String PERF_KEY_GATEWAY_LOGIN="perf_key_gateway_login";
    public static final String PERF_KEY_GATEWAY_PASSWORD="perf_key_gateway_password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,new SettingsFragment()).commit();
    }
}

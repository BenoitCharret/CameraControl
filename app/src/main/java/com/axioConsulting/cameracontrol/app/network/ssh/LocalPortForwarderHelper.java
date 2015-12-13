package com.axioConsulting.cameracontrol.app.network.ssh;

import android.content.SharedPreferences;
import android.util.Log;
import com.axioConsulting.cameracontrol.app.SettingsActivity;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.LocalPortForwarder;

import java.io.IOException;

/**
 * Created by benoit on 03/09/15.
 */
public class LocalPortForwarderHelper {


    private static final String TAG = LocalPortForwarderHelper.class.getName();

    String hostname;
    String username;
    String password;
    int port;
    LocalPortForwarder localPortForwarder;
    Connection connection;

    public LocalPortForwarderHelper(SharedPreferences sharedPreferences){
        this.hostname=sharedPreferences.getString(SettingsActivity.PERF_KEY_GATEWAY_HOST,"");
        this.port=Integer.valueOf(sharedPreferences.getString(SettingsActivity.PERF_KEY_GATEWAY_PORT, "80"));
        this.username=sharedPreferences.getString(SettingsActivity.PERF_KEY_GATEWAY_LOGIN, "");
        this.password=sharedPreferences.getString(SettingsActivity.PERF_KEY_GATEWAY_PASSWORD,"");
    }

    private LocalPortForwarderHelper(String hostname, String username, String password) {
        this(hostname, username, password, 22);
    }

    private LocalPortForwarderHelper(String hostname, String username, String password, int port) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public void createLocalRedirection(int localPort,String host,int portDest){
        Log.d(TAG,"creating local redirection");
        try {
            if (localPortForwarder!=null){
                localPortForwarder.close();
            }
            connection=new Connection(hostname,port);
            connection.connect();
            boolean isAuthenticated = connection.authenticateWithPassword(username, password);

            if (isAuthenticated == false)
                throw new IOException("Authentication failed.");
            localPortForwarder=connection.createLocalPortForwarder(localPort,host,portDest);
        } catch (IOException e) {
            Log.e(TAG, "cant open a port", e);
        }
    }

    public void closeLocalRedirection(){
        if (localPortForwarder!=null){
            Log.d(TAG,"closing local forwarding");
            try {
                localPortForwarder.close();
                connection.close();
            } catch (IOException e) {
                Log.e(TAG, "cant close redirection", e);
            }
        }
    }
}

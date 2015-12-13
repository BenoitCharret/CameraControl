package com.axioConsulting.cameracontrol.app.network;

import android.content.SharedPreferences;
import com.axioConsulting.cameracontrol.app.SettingsActivity;

import java.io.Serializable;

/**
 * Created by benoit on 03/09/15.
 */
public class ConnectionHost implements Serializable{

    private String host;
    private int port;


    public ConnectionHost(String host) {
        this(host, 22);
    }

    public ConnectionHost(String host, int port) {
        this(host,port,null,0);
    }

    public ConnectionHost(String host, int port, String gatewayHost) {
        this(host,port,gatewayHost,22);
    }

    public ConnectionHost(String host, int port, String gatewayHost, int gatewayPort) {
        this.host = host;
        this.port = port;
        this.gatewayHost = gatewayHost;
        this.gatewayPort = gatewayPort;
    }

    private String gatewayHost;
    private int gatewayPort;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getGatewayHost() {
        return gatewayHost;
    }

    public void setGatewayHost(String gatewayHost) {
        this.gatewayHost = gatewayHost;
    }

    public int getGatewayPort() {
        return gatewayPort;
    }

    public void setGatewayPort(int gatewayPort) {
        this.gatewayPort = gatewayPort;
    }
}

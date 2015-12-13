package com.axioConsulting.cameracontrol.app.zabbix;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.axioConsulting.cameracontrol.app.SettingsActivity;
import com.axioConsulting.cameracontrol.app.network.ConnectionHost;
import com.axioConsulting.cameracontrol.app.network.ssh.LocalPortForwarderHelper;
import com.axioConsulting.cameracontrol.app.network.ssh.SshHelper;
import com.axioConsulting.cameracontrol.app.zabbix.bean.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by benoit on 30/08/15.
 */
public class ZabbixResolver {

    private static final String TAG = ZabbixResolver.class.getName();
    private static final String VERSION_ENTITY = "{\"jsonrpc\":\"2.0\",\"method\":\"apiinfo.version\",\"id\":1,\"auth\":null,\"params\":{}}";
    private static final String LOGIN_ENTITY = "{\n"
            + "    \"jsonrpc\": \"2.0\",\n"
            + "    \"method\": \"user.login\",\n"
            + "    \"params\": {\n"
            + "        \"user\": \":login\",\n"
            + "        \"password\": \":password\"\n"
            + "    },\n"
            + "    \"id\": 1,\n"
            + "    \"auth\": null\n"
            + "}";

    private static final String HOSTGROUPS_ENTITY = "{\n"
            + "    \"jsonrpc\": \"2.0\",\n"
            + "    \"method\": \"hostgroup.get\",\n"
            + "    \"params\": {\n"
            + "        \"output\": \"extend\",\n"
            + "        \"filter\" : {\n"
            + "        \t\"name\" : \":hostgroup\"\n"
            + "        }\n"
            + "    },\n"
            + "    \"id\":\"2\",\n"
            + "    \"auth\": \":token\"\n"
            + "}";

    private static final String HOST_ENTITY = "{\n"
            + "    \"jsonrpc\": \"2.0\",\n"
            + "    \"method\": \"host.get\",\n"
            + "    \"params\": {\n"
            + "        \"output\": [\n"
            + "            \"hostid\",\n"
            + "            \"host\"\n"
            + "        ],\n"
            + "        \t\"groupids\": \":groupid\"\n"
            + "    },\n"
            + "    \"id\": 2,\n"
            + "    \"auth\": \":token\"\n"
            + "}";

    private ConnectionHost connectionHost;
    private SharedPreferences sharedPreferences;

    public ZabbixResolver(ConnectionHost connectionHost,SharedPreferences sharedPreferences) {
        this.connectionHost = connectionHost;
        this.sharedPreferences=sharedPreferences;
    }

    public List<String> getHosts() {
        String hostZabbix =sharedPreferences.getString(SettingsActivity.PREF_KEY_ZABBIX_HOST,"");
        if (StringUtils.isBlank(hostZabbix)){
            return new ArrayList<String>();
        }
        int zabbixPort=getZabbixPort(sharedPreferences);
        int finalPort=zabbixPort;
        LocalPortForwarderHelper localPortForwarderHelper=null;
        if (StringUtils.isNotEmpty(connectionHost.getGatewayHost())){
            // il faut monter un tunnel d'abord
            localPortForwarderHelper=new LocalPortForwarderHelper(sharedPreferences);
            finalPort=8080;
            localPortForwarderHelper.createLocalRedirection(finalPort,hostZabbix,zabbixPort);
            hostZabbix="localhost";
        }
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://"+hostZabbix+":"+finalPort+"/zabbix/api_jsonrpc.php");
        Log.d(TAG,"url to call:"+httpPost.getURI());
        httpPost.setHeader("content-Type", "application/json");
        List<String> hostnames = new ArrayList<String>();
        try {
            Login login = sendRequest(httpClient, httpPost, new StringEntity(LOGIN_ENTITY.replace(":login",sharedPreferences.getString(SettingsActivity.PREF_KEY_ZABBIX_LOGIN,"")).replace(":password",sharedPreferences.getString(SettingsActivity.PREF_KEY_ZABBIX_PASSWORD,""))), Login.class);
            Log.d(TAG, "token found " + login.getResult());
            // search camera host groups
            HostGroups hostGroups = sendRequest(httpClient, httpPost, new StringEntity(HOSTGROUPS_ENTITY.replace(":token", login.getResult()).replace(":hostgroup",sharedPreferences.getString(SettingsActivity.PREF_KEY_ZABBIX_HOSTGROUP,""))), HostGroups.class);
            if (hostGroups.getResult().length < 1 || hostGroups.getResult().length >= 2) {
                Log.i(TAG, "camera group not found or many groups");
                return hostnames;
            }
            HostGroup hostGroup = hostGroups.getResult()[0];

            StringEntity hostEntity = new StringEntity(HOST_ENTITY.replace(":groupid", "" + hostGroup.getGroupid()).replace(":token", login.getResult()));
            Hosts hosts = sendRequest(httpClient, httpPost, hostEntity, Hosts.class);


            for (Host host : hosts.getResult()) {
                Log.d(TAG, "host found: " + host.getHost());
                hostnames.add(host.getHost());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if (localPortForwarderHelper!=null){
                localPortForwarderHelper.closeLocalRedirection();
            }
        }

        return hostnames;
    }

    private <T> T sendRequest(HttpClient httpClient, HttpPost httpPost, HttpEntity httpEntity, Class<T> resultType) throws IOException {
        httpPost.setEntity(httpEntity);
        HttpContext localContext = new BasicHttpContext();
        HttpResponse response = httpClient.execute(httpPost, localContext);
        HttpEntity entity = response.getEntity();
        String content = getASCIIContentFromEntity(entity);
        Log.d(TAG, content);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(content, resultType);
    }

    protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
        InputStream in = entity.getContent();

        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n > 0) {
            byte[] b = new byte[4096];
            n = in.read(b);

            if (n > 0)
                out.append(new String(b, 0, n));
        }

        return out.toString();
    }

    private int getZabbixPort(SharedPreferences sharedPreferences){
        String port=sharedPreferences.getString(SettingsActivity.PREF_KEY_ZABBIX_PORT,"80");
        return Integer.valueOf(port);
    }
}

package com.axioConsulting.cameracontrol.app;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.axioConsulting.cameracontrol.app.bean.CameraLogin;
import com.axioConsulting.cameracontrol.app.item.HostItem;
import com.axioConsulting.cameracontrol.app.item.HostItems;
import com.axioConsulting.cameracontrol.app.network.ConnectionHost;
import com.axioConsulting.cameracontrol.app.network.ssh.LocalPortForwarderHelper;
import com.axioConsulting.cameracontrol.app.network.ssh.SshHelper;
import com.axioConsulting.cameracontrol.app.utils.CameraLoginUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * A fragment representing a single Camera detail screen.
 * This fragment is either contained in a {@link CameraListActivity}
 * in two-pane mode (on tablets) or a {@link CameraDetailActivity}
 * on handsets.
 */
public class CameraDetailFragment extends Fragment {

    private static final String TAG = CameraDetailFragment.class.getName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_CONNECTION = "connection_host";

    /**
     * The dummy content this fragment is presenting.
     */
    private HostItem mItem;
    private boolean taskInProgress = false;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    private ConnectionHost connectionHost;
    private LocalPortForwarderHelper localPortForwarderHelper;

    View rootView;
    Switch toggleSwitch;
    Button viewButton;

    public CameraDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = HostItems.getMapItems().get(getArguments().getString(ARG_ITEM_ID));
        }
        if (getArguments().containsKey(ARG_CONNECTION)) {
            connectionHost = (ConnectionHost) getArguments().getSerializable(ARG_CONNECTION);
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "Resume the activity");
        super.onResume();
        if (localPortForwarderHelper != null) {
            localPortForwarderHelper.closeLocalRedirection();
            localPortForwarderHelper = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_camera_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.camera_detail)).setText(mItem.getName());
        }
        toggleSwitch = (Switch) rootView.findViewById(R.id.toggleSwitch);
        toggleSwitch.setEnabled(false);
        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (taskInProgress)
                    return;
                new StartStopTask().execute(b);
            }
        });
        viewButton = (Button) rootView.findViewById(R.id.camera_view);
        viewButton.setEnabled(false);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               new LaunchFirefoxTask().execute();
            }
        });
        new StatusTask().execute();
        return rootView;
    }

    public class LaunchFirefoxTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            String host=mItem.getName();
            int port=8081;
            if (StringUtils.isNotEmpty(connectionHost.getGatewayHost())) {
                // il faut monter un tunnel d'abord
                localPortForwarderHelper = new LocalPortForwarderHelper(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()));
                port = 8081;
                localPortForwarderHelper.createLocalRedirection(port, mItem.getName(), 8081);
                host = "localhost";
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + host + ":"+port));
            intent.setComponent(new ComponentName("org.mozilla.firefox", "org.mozilla.firefox.App"));
            getActivity().startActivity(intent);
            return null;
        }
    }

    public class StartStopTask extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected Void doInBackground(Boolean... voids) {
            toggleSwitch.setEnabled(false);
            String command = "start";
            if (!voids[0])
                command = "stop";

            String host=mItem.getName();
            int port=22;
            LocalPortForwarderHelper myLocalPortForwarderHelper=null;
            if (StringUtils.isNotEmpty(connectionHost.getGatewayHost())) {
                // il faut monter un tunnel d'abord
                myLocalPortForwarderHelper = new LocalPortForwarderHelper(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()));
                port = 2223;
                myLocalPortForwarderHelper.createLocalRedirection(port, mItem.getName(), 22);
                host = "localhost";
            }
            CameraLogin cameraLogin= CameraLoginUtils.retrieveLogin(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()));


            SshHelper sshHelper = new SshHelper(host,cameraLogin.getLogin(), cameraLogin.getPassword(),port);
            sshHelper.execute("sudo service motion " + command);
            if (myLocalPortForwarderHelper!=null){
                myLocalPortForwarderHelper.closeLocalRedirection();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new StatusTask().execute();
        }
    }


    public class StatusTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            taskInProgress = true;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            String host=mItem.getName();
            int port=22;
            LocalPortForwarderHelper myLocalPortForwarderHelper=null;
            if (StringUtils.isNotEmpty(connectionHost.getGatewayHost())) {
                // il faut monter un tunnel d'abord
                myLocalPortForwarderHelper = new LocalPortForwarderHelper(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()));
                port = 2223;
                myLocalPortForwarderHelper.createLocalRedirection(port, mItem.getName(), 22);
                host = "localhost";
            }
            CameraLogin cameraLogin= CameraLoginUtils.retrieveLogin(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()));
            SshHelper sshHelper = new SshHelper(host, cameraLogin.getLogin(), cameraLogin.getPassword(),port);
            List<String> result = sshHelper.execute("ps -ef|grep motion|grep -v grep|grep -v tail");
            if (myLocalPortForwarderHelper!=null){
                myLocalPortForwarderHelper.closeLocalRedirection();
            }
            if (result.size() == 0 || !result.get(0).contains("motion")) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            toggleSwitch.setEnabled(true);
            toggleSwitch.setChecked(aBoolean);
            viewButton.setEnabled(aBoolean);
            taskInProgress = false;
        }
    }
}

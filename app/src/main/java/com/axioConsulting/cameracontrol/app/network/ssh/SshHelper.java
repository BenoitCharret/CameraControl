package com.axioConsulting.cameracontrol.app.network.ssh;

import android.util.Log;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.LocalPortForwarder;
import com.trilead.ssh2.Session;
import com.trilead.ssh2.StreamGobbler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by benoit on 31/08/15.
 */
public class SshHelper {

    private static final String TAG = SshHelper.class.getName();

    String hostname;
    String username;
    String password;
    int port;

    public SshHelper(String hostname, String username, String password) {
        this(hostname, username, password, 22);
    }

    public SshHelper(String hostname, String username, String password, int port) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.port = port;
    }



    public List<String> execute(String command) {

        List<String> resultLines = new ArrayList<String>();

        try {
            /* Create a connection instance */

            Connection conn = new Connection(hostname,port);

			/* Now connect */

            conn.connect();

			/* Authenticate.
			 * If you get an IOException saying something like
			 * "Authentication method password not supported by the server at this stage."
			 * then please check the FAQ.
			 */

            boolean isAuthenticated = conn.authenticateWithPassword(username, password);

            if (isAuthenticated == false)
                throw new IOException("Authentication failed.");

			/* Create a session */

            Session sess = conn.openSession();
            Log.d(TAG, "executing " + command);
            sess.execCommand(command);

            InputStream stdout = new StreamGobbler(sess.getStdout());

            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                Log.d(TAG, "result " + line);
                resultLines.add(line);
            }

			/* Show exit status, if available (otherwise "null") */

            Log.d(TAG, "ExitCode: " + sess.getExitStatus());

			/* Close this session */

            sess.close();

			/* Close the connection */

            conn.close();

        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return resultLines;
    }
}

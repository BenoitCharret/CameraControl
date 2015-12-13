package com.axioConsulting.cameracontrol.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import com.axioConsulting.cameracontrol.app.item.HostItem;
import com.axioConsulting.cameracontrol.app.item.HostItems;
import com.axioConsulting.cameracontrol.app.network.ConnectionHost;
import com.axioConsulting.cameracontrol.app.network.wifi.WifiHelper;
import com.axioConsulting.cameracontrol.app.zabbix.ZabbixResolver;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Cameras. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CameraDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link CameraListFragment} and the item details
 * (if present) is a {@link CameraDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link CameraListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class CameraListActivity extends Activity
		implements CameraListFragment.Callbacks {

		/**
		 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
		 * device.
		 */
		private boolean mTwoPane;
		private ConnectionHost connectionHost;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_camera_list);

				if (findViewById(R.id.camera_detail_container) != null) {
						// The detail container view will be present only in the
						// large-screen layouts (res/values-large and
						// res/values-sw600dp). If this view is present, then the
						// activity should be in two-pane mode.
						mTwoPane = true;

						// In two-pane mode, list items should be given the
						// 'activated' state when touched.
						((CameraListFragment) getFragmentManager()
								.findFragmentById(R.id.camera_list))
								.setActivateOnItemClick(true);
				}

		}


	@Override
	protected void onResume() {
		super.onResume();
		String ssid=WifiHelper.getCurrentSsid(getBaseContext());
		SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String ssidRef= defaultSharedPreferences.getString(SettingsActivity.PREF_KEY_WIFI_SSID, "");
		if (StringUtils.isNotEmpty(ssid) && StringUtils.isNotBlank(ssidRef) && ssidRef.equals(ssid)){
			connectionHost=new ConnectionHost(defaultSharedPreferences.getString(SettingsActivity.PREF_KEY_ZABBIX_HOST,""));
		}else{
			connectionHost=new ConnectionHost(defaultSharedPreferences.getString(SettingsActivity.PREF_KEY_ZABBIX_HOST,""),22,defaultSharedPreferences.getString(SettingsActivity.PERF_KEY_GATEWAY_HOST,""),Integer.valueOf(defaultSharedPreferences.getString(SettingsActivity.PERF_KEY_GATEWAY_PORT,"22")));
		}
		new DiscoverHostTask().execute();
	}

	/**
		 * Callback method from {@link CameraListFragment.Callbacks}
		 * indicating that the item with the given ID was selected.
		 */
		@Override
		public void onItemSelected(String id) {
				if (mTwoPane) {
						// In two-pane mode, show the detail view in this activity by
						// adding or replacing the detail fragment using a
						// fragment transaction.
						Bundle arguments = new Bundle();
						arguments.putString(CameraDetailFragment.ARG_ITEM_ID, id);
						arguments.putSerializable(CameraDetailFragment.ARG_CONNECTION,connectionHost);
						CameraDetailFragment fragment = new CameraDetailFragment();
						fragment.setArguments(arguments);
						getFragmentManager().beginTransaction()
								.replace(R.id.camera_detail_container, fragment)
								.commit();

				} else {
						// In single-pane mode, simply start the detail activity
						// for the selected item ID.
						Intent detailIntent = new Intent(this, CameraDetailActivity.class);
						detailIntent.putExtra(CameraDetailFragment.ARG_ITEM_ID, id);
						detailIntent.putExtra(CameraDetailFragment.ARG_CONNECTION,connectionHost);
						startActivity(detailIntent);
				}
		}

		public class DiscoverHostTask extends AsyncTask<Void, Void, List<HostItem>> {

				@Override protected List<HostItem> doInBackground(Void... strings) {

						List<String> hosts = new ZabbixResolver(connectionHost,PreferenceManager.getDefaultSharedPreferences(getApplicationContext())).getHosts();
						List<HostItem> hostItems=new ArrayList<HostItem>();
						for (int i=0;i<hosts.size();i++){
								hostItems.add(new HostItem(""+i,hosts.get(i)));
						}
						HostItems.setItems(hostItems);
						return hostItems;
				}

				@Override protected void onPostExecute(List<HostItem> items) {
						((CameraListFragment) getFragmentManager()
								.findFragmentById(R.id.camera_list)).setListAdapter(new ArrayAdapter<HostItem>(
								((CameraListFragment) getFragmentManager()
										.findFragmentById(R.id.camera_list))
										.getActivity(),
								android.R.layout.simple_list_item_activated_1,
								android.R.id.text1,
								items));
						/*super.onPostExecute(strings);*/

				}
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater=getMenuInflater();
		menuInflater.inflate(R.menu.main_activity_actions,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.menu_settings:
				Intent menuIntent = new Intent(this, SettingsActivity.class);
				startActivity(menuIntent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}

package com.example.arjun.projectx_v1;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by arjun on 19/5/15.
 */
public class wifiConnector extends Fragment {

    public static final String TAG = "wifiConnector";
    private WifiManager mwifiManager;
    private WifiScanReceiver wifiReceiver;
    private static final String ssid = "Nexus 5";
    private static final String password = "mynexus5";
    //private static final String bssid = "c0:cb:38:97:d8:bb";
    private static final String bssid = "02:1a:11:f4:fc:8f";
    private WifiConfiguration mwifiConfig;
    private int netId;

    public wifiConnector() {
        // required default constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wifi_connector);
//        list = (ListView) findViewById(R.id.listView1);

        // get an object for WifiManager
        // ??? to check whether getapplicationcontext required or not ???
        mwifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!mwifiManager.isWifiEnabled()) {
            Toast.makeText(getActivity(), "Enabling WiFi", Toast.LENGTH_SHORT).show();
            mwifiManager.setWifiEnabled(true);
        }

        wifiReceiver = new WifiScanReceiver();

        // get how many networks available
        //this.logNumNetworks();

        // get config nets detail
        //this.logConfiguredNetworks();
//        this.establishConnection();
//
//        this.logConnectionInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        // establish connection
        this.establishConnection();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(wifiReceiver);
        this.destroyConnection();
    }

    @Override
    public void onStop() {
        super.onStop();

        //remove the details of the network: to avoid manual connection
        //this.destroyConnection();
    }

    private void establishConnection() {

        Log.d(TAG, "establishConnection");
        mwifiConfig = new WifiConfiguration();

        mwifiConfig.BSSID = bssid;
        mwifiConfig.SSID = "\"" + ssid + "\"";
        mwifiConfig.preSharedKey = "\"" + password + "\"";
        mwifiConfig.priority = 1;
        mwifiConfig.status = WifiConfiguration.Status.ENABLED;

        netId = mwifiManager.addNetwork(mwifiConfig);

        if (netId == -1) {
            Toast.makeText(getActivity(), "Network failed", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mwifiManager.enableNetwork(netId, true)) {
            Toast.makeText(getActivity(), "Connection failed", Toast.LENGTH_LONG).show();
        }
    }

    private void destroyConnection() {

        Log.d(TAG, "destroyConnection");

        Log.d(TAG, "" + mwifiManager.getWifiState());

        mwifiManager.disconnect();

        if (mwifiManager.disableNetwork(netId)) {
            Log.d(TAG, "network successfully disabled");
        } else {
            Log.d(TAG, "network not disabled");
        }

        if (mwifiManager.removeNetwork(netId)) {
            Log.d(TAG, "network successfully removed");
        } else {
            Log.d(TAG, "network not removed");
        }

        if (mwifiManager.setWifiEnabled(false)) {
            Log.d(TAG, "wifi disabled");
        } else {
            Log.d(TAG, "unable to disable wifi");
        }

    }

    private void logNumNetworks() {

        // perform a scan
        if (!mwifiManager.startScan())
            Log.d(TAG, "Scan failed!");
    }

    private void logConfiguredNetworks() {

        // get currently config networks
        List<WifiConfiguration> mwifiConfigNets = mwifiManager.getConfiguredNetworks();

        Log.d(TAG, "Num of Configured Nets: " + mwifiConfigNets.size());

        String[] configNetss = new String[mwifiConfigNets.size()];

        for (int i = 0; i < mwifiConfigNets.size(); i++) {
            configNetss[i] = ((mwifiConfigNets.get(i)).toString());
            Log.d(TAG, mwifiConfigNets.get(i).SSID + ", " + mwifiConfigNets.get(i).BSSID);
        }

//        list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
//                android.R.layout.simple_list_item_1, configNetss));
    }

    private void logConnectionInfo() {
        Log.d(TAG, "logConnectionInfo");

        // get current connection info
        WifiInfo mwifiInfo = mwifiManager.getConnectionInfo();

        // get dhcp info
        DhcpInfo mdhcpInfo = null;
        if (mwifiInfo != null)
            mdhcpInfo = mwifiManager.getDhcpInfo();

        // log information
        Log.d(TAG, mwifiInfo.getSSID() + " " +
                mwifiInfo.getBSSID() + " " +
                mwifiInfo.getMacAddress() + " " +
                mwifiInfo.getIpAddress() + " " +
                mwifiInfo.toString());
        Log.d(TAG, mdhcpInfo.toString());
    }

    class WifiScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

                List<ScanResult> mwifiScanList = mwifiManager.getScanResults();

                Log.d(TAG, "Num of AP: " + mwifiScanList.size());

                String[] numNetss = new String[mwifiScanList.size()];
                for (int i = 0; i < mwifiScanList.size(); i++) {
                    numNetss[i] = ((mwifiScanList.get(i)).toString());
                    Log.d(TAG, mwifiScanList.get(i).SSID + ", " + mwifiScanList.get(i).BSSID + numNetss[i]);
                }

//                list.setAdapter(new ArrayAdapter<String>(getActivity(),
//                        android.R.layout.simple_list_item_1, numNetss));
            }
        }

    }
}

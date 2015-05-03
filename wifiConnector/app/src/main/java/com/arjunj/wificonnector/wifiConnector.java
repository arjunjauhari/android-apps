package com.arjunj.wificonnector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;


public class wifiConnector extends ActionBarActivity {

    public static final String TAG = "wifiConnector";
    private WifiManager mwifiManager;
    private ListView list;
    private WifiScanReceiver wifiReceiver;
    private static final String ssid = "IOTACH";
    private static final String password = "Vlsi1234";
    private static final String bssid = "c0:cb:38:97:d8:bb";
    private WifiConfiguration mwifiConfig;
    private int netId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connector);
        list = (ListView) findViewById(R.id.listView1);

        // get an object for WifiManager
        mwifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!mwifiManager.isWifiEnabled()) {
            Toast.makeText(this, "Enabling WiFi", Toast.LENGTH_SHORT).show();
            mwifiManager.setWifiEnabled(true);
        }

        wifiReceiver = new WifiScanReceiver();

        // get how many networks available
        //this.logNumNetworks();

        // get config nets detail
        //this.logConfiguredNetworks();
        this.establishConnection();

        this.logConnectionInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wifi_connector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void establishConnection () {
        mwifiConfig = new WifiConfiguration();

        mwifiConfig.BSSID = bssid;
        mwifiConfig.SSID = "\"" + ssid + "\"";
        mwifiConfig.preSharedKey = "\"" + password + "\"";
        mwifiConfig.priority = 1;
        mwifiConfig.status = WifiConfiguration.Status.ENABLED;

        netId = mwifiManager.addNetwork(mwifiConfig);

        if (netId == -1) {
            Toast.makeText(this, "Network failed", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mwifiManager.enableNetwork(netId, true)) {
            Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
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

                list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, numNetss));
            }
        }

    }
}

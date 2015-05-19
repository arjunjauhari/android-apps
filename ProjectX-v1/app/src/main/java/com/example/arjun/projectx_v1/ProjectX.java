package com.example.arjun.projectx_v1;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class ProjectX extends ActionBarActivity {

    public static final String TAG = "AJ_PX";
    private NfcAdapter nfcAdapter;
    private Tag my_tag;
    private Ndef ndef;
    private String tag_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_x);

        // get the adapter for NFC on the phone
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC on device", Toast.LENGTH_LONG).show();
            //finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project_x, menu);
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

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "intent is: " + getIntent().getAction());
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            readNFC();
        }

        // adding the fragment
        getFragmentManager().beginTransaction().add(new wifiConnector(), "wifi").commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // our app intent filter was successful that's why this method is called
        Log.d(TAG, "TagReader intent filter success");

        this.setIntent(intent);
    }

    /*
* reads the data inside NFC tag
*/
    public void readNFC() {

        // capture in logcat
        Log.d(TAG, "AJ:" + " readNFC called");

        my_tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);

        // get ndef from tag
        getNdef();

        if (ndef == null)
            return;

        // get reference to text view inside tag_output
        TextView tv_tag_id = (TextView) findViewById(R.id.tag_id);

        NdefMessage msg;
        try {
            // establish connection with tag
            ndef.connect();

            msg = ndef.getNdefMessage();

            // disconnect
            ndef.close();
            Toast.makeText(this, "Tag read successfully!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "readNFC: Read Successfully");
        } catch (IOException e) {
            Toast.makeText(this, "Connection lost: Reading failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Log.d(TAG, "readNFC: Connection lost: Reading failed");
            return;
        } catch (FormatException e) {
            Toast.makeText(this, "Format Error: Reading failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Log.d(TAG, "readNFC: Connection lost: Reading failed");
            return;
        }

        if (msg == null) {
            Toast.makeText(this, "Tag is empty", Toast.LENGTH_LONG).show();
        }

        // read ndef message
        tag_id = new String(msg.getRecords()[0].getPayload());

        // assign it to textview
        if (tag_id.equals(""))
            tv_tag_id.setText("No id provided");
        else
            tv_tag_id.setText("ID: " + tag_id);
    }

    private void getNdef() {
        // initialize it to null
        ndef = null;

        // get instance of ndef from the tag
        if (my_tag != null) {
            ndef = Ndef.get(my_tag);
        } else {
            Toast.makeText(this, "No tag", Toast.LENGTH_LONG).show();
            Log.d(TAG, " Read key pressed but no tag present!!");
        }
    }

}

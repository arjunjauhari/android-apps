package com.arjunj.tagwriter;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = "Main Activity";
    //public static final String readerName = "com.arjunj.tagreader";
    public static final String readerName = "com.example.arjun.projectx_v1";
    public static final String wifiSSID = "ProjectX";
    public static final String wifiPASS = "smile";
    private EditText mInputID;
    private NfcAdapter nfcAdapter;
    private String id_str;
    private PendingIntent pendingIntent;
    private IntentFilter[] writeTagFilters;
    private Tag my_tag;
    private Ndef ndef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get reference to edit text with id input in layout
        mInputID = (EditText) findViewById(R.id.input_id);

        // this along with foreground dispatcher, is required if you want to open your app yourself instead of android system
        // this disables android dispatcher
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // create intent filter so that android knows this app can handle NFC tags
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};

        // get the adapter for NFC on the phone
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, getString(R.string.no_nfc), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
        Log.d(TAG, getString(R.string.debug_key) + " onResume called, enabled FG dispatch");

        boolean action_ndef = NfcAdapter.ACTION_NDEF_DISCOVERED.equals(this.getIntent().getAction()); // will get this intent if scans already written tag
        boolean action_tech = NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction()); // will get this intent if scans empty tag
        boolean action_tag = NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction()); // will get this intent if app in foreground

        // only process if called because of any NFC intent
        if (action_ndef | action_tech | action_tag) {
            my_tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Toast.makeText(this, getString(R.string.tag_detected), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
        Log.d(TAG, getString(R.string.debug_key) + " onPause called, disabled FG dispatch");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    protected void onNewIntent(Intent intent) {
        // our app intent filter was successful that's why this method is called
        Log.d(TAG, getString(R.string.debug_key) + " TagWriter intent filter success");
        this.setIntent(intent);
    }

    /*
    * called when write button is pressed
    */
    public void writeNFC(View view) {

        // capture in logcat
        Log.d(TAG, getString(R.string.debug_key) + " writeNFC: Write key pressed");

        // get ndef from tag
        getNdef();

        // if ndef null: means there is no tag so just return
        if (ndef == null)
            return;

        NdefMessage mMessage = this.createMessage();

        try {
            // establish connection with tag
            ndef.connect();

            // write the message
            ndef.writeNdefMessage(mMessage);

            // disconnect
            ndef.close();

            Toast.makeText(this, "Tag written", Toast.LENGTH_LONG).show();
            Log.d(TAG, "writeNFC: Tag written");
        } catch (IOException e) {
            Toast.makeText(this, "Connection lost: Writing failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Log.d(TAG, "writeNFC: Connection lost: Writing failed");
        } catch (FormatException e) {
            Toast.makeText(this, "Format Error: Writing failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Log.d(TAG, "writeNFC: Format Error: Writing failed");
        }
    }

    private void getNdef() {

        // initialize it to null
        ndef = null;

        // get instance of ndef from the tag
        if (my_tag != null) {
            ndef = Ndef.get(my_tag);
        } else {
            Toast.makeText(this, "No tag", Toast.LENGTH_LONG).show();
            Log.d(TAG, getString(R.string.debug_key) + " Write key pressed but no tag present!!");
        }
    }

    private NdefMessage createMessage() {

        String packageName = getApplicationInfo().packageName;

        // get the id to be written
        id_str = mInputID.getText().toString();
        Log.d(TAG, getString(R.string.debug_key) + " " + id_str);

        String mimeType = "application/" + packageName;
        Log.d(TAG, getString(R.string.debug_key) + " mimetype: " + mimeType);

        // encapsulate data in array of ndefrecord
        NdefRecord[] ndefRecord = new NdefRecord[]{
                NdefRecord.createMime(mimeType, id_str.getBytes(Charset.forName("US-ASCII"))),
                NdefRecord.createApplicationRecord(readerName),
                NdefRecord.createMime(mimeType, wifiSSID.getBytes(Charset.forName("US-ASCII"))),
                NdefRecord.createMime(mimeType, wifiPASS.getBytes(Charset.forName("US-ASCII")))
        };

        return new NdefMessage(ndefRecord);
    }
}

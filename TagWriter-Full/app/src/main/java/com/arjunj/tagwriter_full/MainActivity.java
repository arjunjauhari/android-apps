package com.arjunj.tagwriter_full;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.annotation.Target;
import java.nio.charset.Charset;


public class MainActivity extends ActionBarActivity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    public static final String TAG = "Main Activity";
    private static final int MESSAGE_SENT = 1;
    private EditText mInput;
    private NfcAdapter nfcAdapter;
    private String str;
    private volatile Handler handler;
    private PendingIntent pendingIntent;
    private IntentFilter[] writeTagFilters;
    private Tag mytag;
    private Ndef ndef;
    private boolean is_tag_output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get reference to edittext with id input in layout
        mInput = (EditText) findViewById(R.id.input);

        // this along with foreground dispatcher, i think is required if you want yourself to open your app instead of android system Confirm!
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // create intent filter so that android knows this app can handle NFC tags
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};

        // get the adapter for NFC on the phone
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, getString(R.string.no_nfc), Toast.LENGTH_LONG).show();
        }

        // can only be used to beam data between two devices p2p
//        if (nfcAdapter == null) {
//            Toast.makeText(this, getString(R.string.no_nfc), Toast.LENGTH_LONG).show();
//        } else {
//            nfcAdapter.setNdefPushMessageCallback(this, this);
//            //Toast.makeText(this, "Bring phone close to tag", Toast.LENGTH_LONG).show();
//            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // enabling foreground dispatch: disables all intent filters(including this app's) except one given in writeTagFilters
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
        // below part can be done directly in onnewintent method as we are not calling any method here
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction())) {
            // only process the NFC if the activity was started by an NFC tag
            // as specified via an ACTION_TAG_DISCOVERED
            mytag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String techList[] = mytag.getTechList();
            int i = techList.length;
            for (int x = 0; x < i; x++) {
                Log.d(TAG, techList[x]);
            }
            Toast.makeText(this, getString(R.string.tag_detected), Toast.LENGTH_LONG).show();
        }
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(this.getIntent().getAction())) {
            Toast.makeText(this, "Action Ndef Discovered", Toast.LENGTH_LONG).show();
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(this.getIntent().getAction())) {
            Toast.makeText(this, "Action tech Discovered", Toast.LENGTH_LONG).show();
        }

//        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
//            // only process the NFC if the activity was started by an NFC intent
//            // as specified via an ACTION_NDEF_DISCOVERED
//            processNFC(getIntent());
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
        Log.d(TAG, getString(R.string.debug_key) + "onPause called");
    }

    @Override
    protected void onStart() {
        super.onStart();
        // You have to be careful with memory leaks with inner classes and
        // handlers
        // In this app we remove the handler and messages in onStop
        // To ensure the handler cannot outlive the Activity object

        /*
        * creating a new handler and
        * overriding handleMessage method(this is mandatory) in handler class
        * */
        handler = new Handler() {
            //this method is called whenever someone calls sendtotarget with target set as this handle
            public void handleMessage(Message msg) {
                // if the message 'what' matches the constant MESSAGE_SENT, it
                // means the callback
                // to onNdefPushComplete happened which means the NFC was a
                // success
                if (msg.what == MESSAGE_SENT) {
                    Toast.makeText(getApplicationContext(), "Tag written successfully!!", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();

        Handler oldHandle = handler;
        //null out the handler
        handler = null;
        if (oldHandle != null) {
            //removes the handle from message queue
            oldHandle.removeMessages(MESSAGE_SENT);
        }
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
        Log.d(TAG, getString(R.string.debug_key) + "TagWriter intent filter success");
        this.setIntent(intent);
    }

    private void getNdef() {

        // initialize it to null
        ndef = null;

        // get instance of ndef fro the tag
        if (mytag != null) {
            ndef = Ndef.get(mytag);
        } else {
            Toast.makeText(this, "No tag", Toast.LENGTH_LONG).show();
        }
    }

    /*
    * called when write button is pressed
    * get the data to be written and invoke a nfc callback
    */
    public void readNFC(View view) {

        // capture in logcat
        Log.d(TAG, getString(R.string.debug_key) + " Read key pressed");

        getNdef();
        if (ndef == null)
            return;

        String set_it;

        boolean canbereadonly = ndef.canMakeReadOnly();
        int maxsize = ndef.getMaxSize();
        String tag_type = ndef.getType();
        boolean connected = ndef.isConnected();
        boolean writable = ndef.isWritable();
        //NdefMessage cached_msg = ndef.getCachedNdefMessage();
        NdefMessage cached_msg = null;
        try {
            // establish connection with tag
            ndef.connect();

            cached_msg = ndef.getNdefMessage();

            // disconnect
            ndef.close();
            Toast.makeText(this, "Tag read", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Connection lost: Writing failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (FormatException e) {
            Toast.makeText(this, "Format Error: Writing failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


        if (cached_msg == null) {
            Toast.makeText(this, "Tag is empty", Toast.LENGTH_LONG).show();
        } else {
            // this can also be done by creating other activity for this layout.tag_output and calling that activity through explicit intent, better way
            setContentView(R.layout.tag_output);
            is_tag_output = true;
        }

        // get reference to text view inside tag_output
        TextView out1 = (TextView) findViewById(R.id.out1);
        TextView v_aar = (TextView) findViewById(R.id.aar);
        TextView prop1 = (TextView) findViewById(R.id.prop1);
        TextView prop2 = (TextView) findViewById(R.id.prop2);
        TextView prop3 = (TextView) findViewById(R.id.prop3);
        TextView prop4 = (TextView) findViewById(R.id.prop4);

        // get string from ndef message
        String user_msg = new String(cached_msg.getRecords()[0].getPayload());
        //String aar = new String(cached_msg.getRecords()[1].getPayload());

        // assign it to textview
        if (user_msg.equals(""))
            out1.setText("User didn't provide data");
        else
            out1.setText(user_msg);
        //v_aar.setText("AAR is: " + aar);
        v_aar.setText("AAR is: ");

        if (canbereadonly) {
            Log.d(TAG, getString(R.string.debug_key) + "Can be Read only");
            set_it = "Can be Read only";
        } else {
            Log.d(TAG, getString(R.string.debug_key) + "Can't be Read only");
            set_it = "Can't be Read only";
        }
        prop1.setText(set_it);

        if (writable) {
            Log.d(TAG, getString(R.string.debug_key) + "writable");
            set_it = "Writable";
        } else {
            Log.d(TAG, getString(R.string.debug_key) + "Not writable");
            set_it = "Not Writable";
        }
        prop2.setText(set_it);

        if (connected) {
            Log.d(TAG, getString(R.string.debug_key) + "Connected");
        } else {
            Log.d(TAG, getString(R.string.debug_key) + "Not Connected");
        }

        Log.d(TAG, getString(R.string.debug_key) + "maxsize: " + maxsize + "tag_type: " + tag_type);
        prop3.setText("MaxSize: " + maxsize);
        prop4.setText("TagType: " + tag_type);

    }

    /*
    * called when write button is pressed
    * get the data to be written and invoke a nfc callback
    */
    public void writeNFC(View view) {

        // capture in logcat
        Log.d(TAG, getString(R.string.debug_key) + " Write key pressed");

        getNdef();
        if (ndef == null)
            return;

        NdefMessage mMessage = this.createMessage();

        try {
            // establish connection with tag
            ndef.connect();
            //if (ndef.isConnected()) {
                // write the message
                ndef.writeNdefMessage(mMessage);
            //}
            // disconnect
            ndef.close();
            Toast.makeText(this, "Tag written", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Connection lost: Writing failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (FormatException e) {
            Toast.makeText(this, "Format Error: Writing failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


        // get the edit text string
//        str = mInput.getText().toString();
//        //Log.d(TAG, getString(R.string.debug_key) + " " + str);
//
//
        // can only be used to beam data between two devices p2p
//        if (nfcAdapter == null) {
//            Toast.makeText(this, getString(R.string.no_nfc), Toast.LENGTH_LONG).show();
//        } else {
//            nfcAdapter.setNdefPushMessageCallback(this, this);
//            //Toast.makeText(this, "Bring phone close to tag", Toast.LENGTH_LONG).show();
//            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
//        }
    }

    private NdefMessage createMessage() {

        String packageName = getApplicationInfo().packageName;

        // get the edit text string
        str = mInput.getText().toString();
        //Log.d(TAG, getString(R.string.debug_key) + " " + str);

        String mimeType = "application/" + packageName;
        Log.d(TAG, getString(R.string.debug_key) + "mimetype" + mimeType);
        //byte[] mimeByte = mimeType.getBytes(Charset.forName("US-ASCII"));

        // encapsulate data in array of ndefrecord
        NdefRecord[] ndefRecord = new NdefRecord[]{
                NdefRecord.createMime(mimeType, str.getBytes(Charset.forName("US-ASCII"))),
                //NdefRecord.createApplicationRecord(packageName)
        };

        return new NdefMessage(ndefRecord);
    }

    @Override
    public void onBackPressed() {
        if (is_tag_output) {
            setContentView(R.layout.activity_main);
            is_tag_output = false;
        } else {
            finish();
        }
    }

    /*
    * callback for method setNdefPushMessageCallback
    * called whenever we come near a nfc capable device
    * provided write button was pressed
    */
    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {

        String packageName = getApplicationInfo().packageName;

        // get the edit text string
        str = mInput.getText().toString();
        //Log.d(TAG, getString(R.string.debug_key) + " " + str);

        String mimeType = "application/" + packageName;
        Log.d(TAG, getString(R.string.debug_key) + "mimetype" + mimeType);
        byte[] mimeByte = mimeType.getBytes(Charset.forName("US-ASCII"));

        // encapsulate data in array of ndefrecord
        NdefRecord[] ndefRecord = new NdefRecord[]{
                NdefRecord.createMime(mimeType, str.getBytes(Charset.forName("US-ASCII"))),
                NdefRecord.createApplicationRecord(packageName)
        };

        return new NdefMessage(ndefRecord);
    }

    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent) {
        // A handler is needed to send a message to this activity
        // because NFC happens in a Binder thread.
        // The target is created by the OS, so we just have to send it.
        // We give obtainMessage the parameter MESSAGE_SENT so we can check when
        // we handle
        // the message that message was sent from this method
        Handler h = handler;
        if (h != null)
            h.obtainMessage(MESSAGE_SENT).sendToTarget();
    }


}

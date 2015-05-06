package com.example.arjun.httpclient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;


public class httpClient extends ActionBarActivity {

    private static final String url = "http://www.google.com";
    private static final String DEBUG_TAG = "httpClient";
    private TextView textView_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_client);
        textView_out = (TextView) findViewById(R.id.textView_out);

        // Check for network availability
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network available, initiate a async task in background
            new DownloadMenuTask().execute(url); //equivalent to declaring an object of type Down.. and calling the method execute() on it
        } else {
            Toast.makeText(this, "No network connection available", Toast.LENGTH_LONG).show();
        }
    }

    private class DownloadMenuTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0].toString());
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Object result) {
            textView_out.setText(result.toString());
        }

    }

    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        int len = 1500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000); // milliseconds: time before read expires, def is 0(indefinitely)
            conn.setConnectTimeout(15000); //milliseconds
            conn.setRequestMethod("GET"); //this is def method also
            conn.setDoInput(true);

            //Start the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);

            is = conn.getInputStream(); //this is currently in byte format, therefore needs to be converted into one or the readable type

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;
        } finally {
            //this code always run after try exited no matter at what point the try code exited
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            if (is != null) {
                is.close();
            }
        }
    }

    private String readIt(InputStream stream, int len) throws UnsupportedEncodingException, IOException {
        Reader reader = null; // this is base class for any reader type
        reader = new InputStreamReader(stream, "UTF-8"); //converts the bytes stream to character stream, format used for decoding is UTF-8
        char[] buffer = new char[len];
        reader.read(buffer);    //reads a character from reader and puts it into buffer array starting from offset 0
        return new String(buffer);  //construct a string based on buffer and return it
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_http_client, menu);
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
}

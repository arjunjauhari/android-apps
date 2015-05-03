package com.apps101.assigntwo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.view.View;



public class JwkyActivity extends ActionBarActivity {

    MediaPlayer scary;
    WebView jwky;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jwky);

        jwky = (WebView) findViewById(R.id.webView_jwky);

        jwky.loadUrl("file:///android_asset/webcontent/jabberwocky.html");
        scary = MediaPlayer.create(this, R.raw.jaguar);

    }

    protected void onResume() {
        scary.start();
        scary.setLooping(true);
        super.onResume();
    }

    protected void onPause() {
        scary.pause();
        super.onPause();
    }

    protected void onDestroy() {
        scary.stop();
        scary.release();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_jwky, menu);
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

    public void openWiki(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://en.wikipedia.org/wiki/Jabberwocky"));

        startActivity(intent);

    }

    public void openPic(View v) {
        jwky.loadUrl("file:///android_asset/vampire.jpg");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && jwky.canGoBack()) {
            jwky.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
}

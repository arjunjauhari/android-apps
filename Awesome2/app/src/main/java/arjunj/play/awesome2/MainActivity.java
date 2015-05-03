package arjunj.play.awesome2;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    MediaPlayer xl_ki_kudiyan;
    String url1 = "http://www.bodhitree.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("Banana", "onCreate!!");
        //Log.d("Dhoka", "its bad!!");
        //Log.wtf("Fuck", "you!!");
        //xl_ki_kudiyan.start();
    }

    @Override
    protected void onResume() {
        Log.e("Banana", "onResume!!");
        xl_ki_kudiyan = MediaPlayer.create(this, R.raw.xl_ki_kudiyan_v02);
        xl_ki_kudiyan.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e("Banana", "onPause!!");
        xl_ki_kudiyan.stop();
        xl_ki_kudiyan.release();
        super.onPause();
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

    public void openBT(View v) {
        // Opens bodhitree website upon click of YES button
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url1));
        startActivity(i);
    }
}

package com.arjunj.survey;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import java.util.TooManyListenersException;


public class MainActivity extends ActionBarActivity {

    private EditText mName;
    private EditText mEmail;
    private EditText mComments;
    private EditText mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mName = (EditText) findViewById(R.id.name);
        mEmail = (EditText) findViewById(R.id.email);
        mPhone = (EditText) findViewById(R.id.phone);
        mComments = (EditText) findViewById(R.id.comments);

        mPhone.requestFocus();

        //defining an object and creating a class at same time..
        //called anonymous inner class
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String comments = s.toString();
                boolean valid = comments.length() > 0 && comments.toLowerCase().indexOf("bike") == -1;
                View bike = findViewById(R.id.imageButton);

                boolean isVisible = bike.getVisibility() == View.VISIBLE;

                if (isVisible == valid) {
                    return;
                }

                Animation anim;
                if (valid) {
                    bike.setVisibility(View.VISIBLE);
                    anim = AnimationUtils.makeInAnimation(MainActivity.this, true);
                } else {
                    bike.setVisibility(View.INVISIBLE);
                    anim = AnimationUtils.makeOutAnimation(MainActivity.this, true);
                }
                bike.startAnimation(anim);

            }
        };
        mComments.addTextChangedListener(watcher);

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

    public void processForm(View bike) {
        Log.d("MainActivity", "processForm");

        String comments = mComments.getText().toString();
        String email = mEmail.getText().toString();
        String phone = mPhone.getText().toString();
        String name = mName.getText().toString();

        ///simplest way to send some text..
//        Intent i = new Intent(Intent.ACTION_SEND);
//        i.setType("text/plain");
//        i.putExtra(Intent.EXTRA_TEXT, "What a wonderful app!!");
//        startActivity(i);

        //sending a sms message..
//        Intent i = new Intent(Intent.ACTION_VIEW);
//        i.setData(Uri.parse("sms:" + phone));
//        i.putExtra("sms_body", comments);
//        startActivity(i);

        //sending an email
        //Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "someone@xyz.com", null));

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.fromParts("mailto", "someone@xyz.com", null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "important news");
        intent.putExtra(Intent.EXTRA_TEXT, name + " says " + comments);

        if (intent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this.getApplicationContext(), "Setup your email client!" ,Toast.LENGTH_SHORT).show();
        } else {
            startActivity(Intent.createChooser(intent, "Please choose your email app"));
        }

//        try {
//            startActivity(intent);
//        } catch (Exception ex) {
//            Toast.makeText(this.getApplicationContext(), "Could not send an email", Toast.LENGTH_SHORT).show();
//            Log.e("MainActivity", "Could not send an email", ex);
//
//        }


    }

    public void processFormOld(View bike) {

        String comments = mComments.getText().toString();
        String email = mEmail.getText().toString();
        String phone = mPhone.getText().toString();
        String name = mName.getText().toString();

        int position = email.indexOf("@");

        if (position == -1) {
            Toast.makeText(this.getApplicationContext(), "Enter valid email address!!", Toast.LENGTH_LONG).show();
            mEmail.requestFocus();
            return;
        }

        int len = comments.length();

        if (len == 0) {
            Toast.makeText(this.getApplicationContext(), "Give me some comments!!", Toast.LENGTH_LONG).show();
            return;
        }

        if (name.equalsIgnoreCase("Arjun")) {
            Toast.makeText(this.getApplicationContext(), "Great to see you dude!!", Toast.LENGTH_LONG).show();
        }

        try {
            int value = Integer.parseInt(phone);
            Log.d("MainActivity", "Phone number: " + value);
        } catch(Exception e) {
            Log.d("ManiActivity", "Invalid Phone Number " + phone);

        }

        Animation anim = AnimationUtils.makeOutAnimation(this, true);
        bike.startAnimation(anim);

        String username = email.substring(0, position);
        String thank = "ThankYou " + username + "!";

        Toast.makeText(this.getApplicationContext(), thank, Toast.LENGTH_LONG).show();

        bike.setVisibility(View.INVISIBLE);
        Toast.makeText(this.getApplicationContext(), R.string.app_name, Toast.LENGTH_LONG).show();
    }
}

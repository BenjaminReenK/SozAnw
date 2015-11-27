package com.example.benni.deaddrop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

public class IdentityActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnOK;
    private EditText edName;
    private EditText edMail;
    private SharedPreferences prefs;
    private boolean previouslyStarted = false;

    public static final String EXTRANAME = "name";
    public static final String EXTRAMAIL = "mail";
    public static final String FIRSTRUN = "firstrun";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity);

        btnOK = (Button) findViewById(R.id.btn_id_ok);
        btnOK.setOnClickListener(this);

        edName = (EditText) findViewById(R.id.edit_id_name);
        edMail = (EditText) findViewById(R.id.edit_id_mail);

        // first app run check
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        previouslyStarted = prefs.getBoolean(FIRSTRUN, false);

/*
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(FIRSTRUN, false);
        edit.commit();*/

        // first start
        if(!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(FIRSTRUN, true);
            edit.commit();
        }
        // fill out form with values from sharedpreferences if it's not the first start
        else {
            // create new intent and start mainactivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(EXTRANAME, prefs.getString(EXTRANAME, "NAMEPLACEHOLDER"));
            intent.putExtra(EXTRAMAIL, prefs.getString(EXTRAMAIL, "MAILPLACEHOLDER"));
            startActivity(intent);
            finish();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_identity, menu);
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
    public void onClick(View v) {
        if (v.getId() == R.id.btn_id_ok) {

            // save name and mail for later usage
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(EXTRANAME, edName.getText().toString());
            edit.putString(EXTRAMAIL, edMail.getText().toString());
            edit.commit();

            // create new intent and start mainactivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(EXTRANAME, edName.getText());
            intent.putExtra(EXTRAMAIL, edMail.getText());
            startActivity(intent);
            finish();

        }
    }

}

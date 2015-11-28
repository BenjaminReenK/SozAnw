package com.example.benni.deaddrop;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.filesystem.FSSharkKB;
import net.sharkfw.knowledgeBase.sync.SyncKP;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.peer.AndroidSharkEngine;
import net.sharkfw.system.SharkException;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;


    /*private CustomAndroidSharkEngine sharkEngine;
    private FSSharkKB kb;
    private PeerSemanticTag ownPeerTag;
    private WifiListenerKp wifiKP;*/
    private String appDir;
    public static Handler handler;
    private KbTextViewWriter logger;
    private WifiPeer wifiPeer;

    public static final String KBNAME = "testKB";
    public static final String KBPATH = "kbstorage/";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // handler for logging function
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMsg) {
                super.handleMessage(inputMsg);
                logger.appendToKbText((String) inputMsg.obj);
            }
        };

        logger = KbTextViewWriter.getInstance();

        // extract Username and mail for Peer
        Intent intent = getIntent();
        String nickName = intent.getStringExtra(IdentityActivity.EXTRANAME);
        String email = intent.getStringExtra(IdentityActivity.EXTRAMAIL);

        // Get AppDir
        File aDir = getFilesDir();
        appDir = aDir.getPath() + "/" + KBPATH;

        // Create Wifipeer
        wifiPeer = new WifiPeer(appDir, KBNAME, nickName, email, "", this);
        try {
            wifiPeer.init();
        } catch (SharkKBException e) {
            e.printStackTrace();
        }

    }




    @Override
    public void onNavigationDrawerItemSelected(int position) {


        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            // Contacts
            case 0:
                ContactsFragment contactFragment = new ContactsFragment();
                //contactFragment.setActivity(this);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, contactFragment)
                        .commit();
                break;

            // Messages
            case 1:
                MessagesFragment messagesFragment = new MessagesFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, messagesFragment)
                        .commit();
                break;

            // Logs
            case 2:
                LogFragment logFragment = new LogFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, logFragment)
                        .commit();
                break;

            // Settings
            case 3:
                SettingsFragment settingsFragment = new SettingsFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, settingsFragment)
                        .commit();
                break;
        }
    }



    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    public WifiPeer getWifiPeer() {
        return wifiPeer;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifiPeer.stopWifi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wifiPeer.stopWifi();
    }

    public static synchronized void log(String text, final int logType) {
        final String logText = text;
        handler.post(new Runnable() {
            public void run() {
                if (logType == KbTextViewWriter.LOG_CON) {
                    KbTextViewWriter.getInstance().appendToLogText(logText);
                }
                else if (logType == KbTextViewWriter.LOG_KB) {
                    KbTextViewWriter.getInstance().appendToKbText(logText);
                }

            }
        });
    }

    public KbTextViewWriter getLogger() {
        return logger;
    }
}


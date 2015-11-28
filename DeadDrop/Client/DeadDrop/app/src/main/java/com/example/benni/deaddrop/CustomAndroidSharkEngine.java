package com.example.benni.deaddrop;

import android.content.Context;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.peer.AndroidSharkEngine;
import net.sharkfw.protocols.wifidirect.WifiDirectStreamStub;

import java.io.IOException;

/**
 * Created by Benni on 28.11.2015.
 */
public class CustomAndroidSharkEngine extends AndroidSharkEngine
{
    private boolean isWifiRunning;

    public CustomAndroidSharkEngine(Context context) {
        super(context);
        this.isWifiRunning = false;
    }

    @Override
    public void startWifiDirect() throws SharkProtocolNotSupportedException, IOException {
        super.startWifiDirect();
        this.isWifiRunning = true;
    }

    @Override
    public void stopWifiDirect() throws SharkProtocolNotSupportedException {
        super.stopWifiDirect();
        this.isWifiRunning = false;
    }

    public boolean isWifiRunning() {
        return isWifiRunning;
    }

    public WifiDirectStreamStub getWifiStreamStub() {
        return _wifi;
    }
}

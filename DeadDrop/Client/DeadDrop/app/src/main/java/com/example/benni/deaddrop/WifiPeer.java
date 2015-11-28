package com.example.benni.deaddrop;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.widget.Toast;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.filesystem.FSSharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.protocols.wifidirect.WifiDirectStreamStub;
import net.sharkfw.system.SharkException;

import java.io.IOException;
import java.util.List;

/**
 * Created by Benni on 28.11.2015.
 */
public class WifiPeer implements ConnectionListener{

    private CustomAndroidSharkEngine sharkEngine;
    private FSSharkKB kb;
    private PeerSemanticTag ownPeerTag;
    private WifiKP wifiKP;
    private Context ctx;


    private String kbPath;
    private String kbName;
    private String peerName;
    private String peerSI;
    private String peerAdr;


    public WifiPeer(String kbPath, String kbName, String peerName, String peerSI, String peerAdr, Context ctx) {
        this.kbPath = kbPath;
        this.kbName = kbName;
        this.peerName = peerName;
        this.peerSI = peerSI;
        this.peerAdr = peerAdr;
        this.ctx = ctx;
    }

    public void init() throws SharkKBException {
        MainActivity main = (MainActivity) ctx;

        // Create sharkengine
        sharkEngine = new CustomAndroidSharkEngine(ctx);

        // Create KnowledgeBase
        kb = new FSSharkKB(kbPath + kbName);

        // Create own Peertag
        ownPeerTag = kb.createPeerSemanticTag(peerName, peerSI, peerAdr);

        // Create KnowledgePort
        wifiKP = new WifiKP(sharkEngine, ownPeerTag, kb);
        wifiKP.setConnectionListener(this);

        // Listener for logger
        kb.addListener(main.getLogger());
    }

    public void stopWifi() {
        if (sharkEngine.isWifiRunning()) {
            try {
                sharkEngine.stopWifiDirect();
            } catch (SharkProtocolNotSupportedException e) {
                MainActivity.log("Error stopping Wifi", KbTextViewWriter.LOG_CON);
            }
            Toast.makeText(ctx, "Wifi stopped", Toast.LENGTH_SHORT).show();
        }
    }

    public void startWifi() {
        try {
            sharkEngine.startWifiDirect();
        } catch (IOException e) {
            MainActivity.log("Error starting Wifi", KbTextViewWriter.LOG_CON);
        } catch (SharkProtocolNotSupportedException e) {
            MainActivity.log("Error starting Wifi", KbTextViewWriter.LOG_CON);
        }
        Toast.makeText(ctx, "Wifi started", Toast.LENGTH_SHORT).show();

    }

    /*public CustomAndroidSharkEngine getSharkEngine() {
        return sharkEngine;
    }*/

    @Override
    public void onConnectionEstablished(KEPConnection connection) {
        MainActivity.log("Peer Connection established", KbTextViewWriter.LOG_CON);
        //Toast.makeText(ctx, sharkEngine.getWifiStreamStub().getConnectionStr(), Toast.LENGTH_SHORT).show();

        String deviceName = "";
        String deviceAddress = "";
        String tcpAddress = "";

        WifiDirectStreamStub stub = sharkEngine.getWifiStreamStub();
        tcpAddress = stub.getConnectionStr();
        List<WifiP2pDevice> deviceList = stub.getDeviceList();

        if (deviceList.size() != 0) {
            WifiP2pDevice device = deviceList.get(0);
            deviceName = device.deviceName;
            deviceAddress = device.deviceAddress;
        }



        // Contact Request
        SemanticTag contactReqTopic = InMemoSharkKB.createInMemoSemanticTag("ContactReqTopic", WifiKP.CONTACTREQ_TOPIC_SI);
        ContextCoordinates cc = InMemoSharkKB.createInMemoContextCoordinates(
                contactReqTopic,
                ownPeerTag,
                ownPeerTag,
                null,
                null,
                null,
                SharkCS.DIRECTION_OUT
        );

        ContextPoint cp = InMemoSharkKB.createInMemoContextPoint(cc);
        cp.addInformation(ownPeerTag.toString());
        Knowledge k = kb.createKnowledge();
        k.addContextPoint(cp);

        try {
            PeerSemanticTag sender = InMemoSharkKB.createInMemoPeerSemanticTag(deviceName, deviceAddress, tcpAddress);
            connection.insert(k, sender.getAddresses());
        } catch (SharkKBException e) {
            e.printStackTrace();
        } catch (SharkException e) {
            e.printStackTrace();
        }
    }
}

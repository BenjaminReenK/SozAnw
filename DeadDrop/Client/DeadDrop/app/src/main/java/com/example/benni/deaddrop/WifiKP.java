package com.example.benni.deaddrop;



import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.app.Activity;


import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.filesystem.FSSharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;

/**
 * An example KP which will send an interest to the connecting device
 *
 * @author jgig
 */
public class WifiKP extends KnowledgePort {
    private SharkCS myInterest;
    private SharkCS contactInterest;
    private ConnectionListener connectionListener;

    public static final String CONTACTREQ_TOPIC_SI = "CONTACTREQ";


    /**
     * @param se       the shark engine
     */
    public WifiKP(SharkEngine se, PeerSemanticTag myIdentity, SharkKB kb) throws SharkKBException {
        super(se);

        this.kb = kb;
        this.myInterest = new InMemoSharkKB().createInterest(null, myIdentity, null, null, null, null, SharkCS.DIRECTION_INOUT);
        //this.myInterest = getInterest();

       /* STSet topics = InMemoSharkKB.createInMemoSTSet();
        SemanticTag contactReq = InMemoSharkKB.createInMemoSemanticTag("stname", MainActivity.T_ADD_CONTACTS);
        topics.merge(contactReq);
        contactInterest = new InMemoSharkKB().createInterest(topics, myIdentity, null, null, null, null, SharkCS.DIRECTION_INOUT);*/
    }

    @Override
    protected void doInsert(Knowledge knowledge, KEPConnection kepConnection) {
        System.out.print("Knowledge received: (");
        System.out.println(L.knowledge2String(knowledge));
        L.knowledge2String(knowledge);

    }

    @Override
    protected void doExpose(SharkCS interest, KEPConnection kepConnection) {


        MainActivity.log("interest received " + L.contextSpace2String(interest), KbTextViewWriter.LOG_CON);
        if (isAnyInterest(interest)) {
            Log.d("WifiKp", "any interest");
            MainActivity.log("any interest received " + L.contextSpace2String(interest), KbTextViewWriter.LOG_CON);

            try {
                MainActivity.log("WifiListenerKp - trying to send interest\n" + L.contextSpace2String(myInterest), KbTextViewWriter.LOG_CON);
                kepConnection.expose(myInterest);

            } catch (SharkException ex) {
                MainActivity.log("WifiListenerKp - problems:" + ex.getMessage(), KbTextViewWriter.LOG_CON);
            }
        }
        if (isPeerInterest(interest)){
            MainActivity.log("Peer interest received " + L.contextSpace2String(interest), KbTextViewWriter.LOG_CON);
            connectionListener.onConnectionEstablished(kepConnection);
        }
        else if (isContactInterest(interest)) {
            MainActivity.log("contact interest received " + L.contextSpace2String(interest), KbTextViewWriter.LOG_CON);
        }

    }




    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }
    private boolean isAnyInterest(SharkCS theInterest) {
        return (theInterest.isAny(SharkCS.DIM_TOPIC) && theInterest.isAny(SharkCS.DIM_ORIGINATOR) &&
                theInterest.isAny(SharkCS.DIM_LOCATION) && theInterest.isAny(SharkCS.DIM_DIRECTION) &&
                theInterest.isAny(SharkCS.DIM_PEER) && theInterest.isAny(SharkCS.DIM_REMOTEPEER) &&
                theInterest.isAny(SharkCS.DIM_TIME)) ;
    }
    private boolean isPeerInterest(SharkCS theInterest) {
        return (theInterest.isAny(SharkCS.DIM_TOPIC) && !theInterest.isAny(SharkCS.DIM_ORIGINATOR) &&
                theInterest.isAny(SharkCS.DIM_LOCATION) && theInterest.isAny(SharkCS.DIM_DIRECTION) &&
                theInterest.isAny(SharkCS.DIM_PEER) && theInterest.isAny(SharkCS.DIM_REMOTEPEER) &&
                theInterest.isAny(SharkCS.DIM_TIME)) ;
    }

    private boolean isContactInterest(SharkCS theInterest) {
        STSet topics = null;
        try {
            topics = theInterest.getSTSet(0);
            //SemanticTag blub = topics.getSemanticTag(MainActivity.T_ADD_CONTACTS);
            //String[] blub1 = blub.getSI();
        } catch (SharkKBException e) {
            e.printStackTrace();
        }


/*
        return (theInterest.getTopics(SharkCS.DIM_TOPIC) && !theInterest.isAny(SharkCS.DIM_ORIGINATOR) &&
                theInterest.isAny(SharkCS.DIM_LOCATION) && theInterest.isAny(SharkCS.DIM_DIRECTION) &&
                theInterest.isAny(SharkCS.DIM_PEER) && theInterest.isAny(SharkCS.DIM_REMOTEPEER) &&
                theInterest.isAny(SharkCS.DIM_TIME)) ;*/
        return false;
    }
}


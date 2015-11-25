package com.example.benni.deaddrop;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.peer.KEPConnection;

/**
 * Created by jetmir on 24.03.2015.
 */
public interface ConnectionListener {

    public void onConnectionEstablished(KEPConnection connection);

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import java.io.IOException;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.system.SharkException;

/**
 *
 * @author @author s0546862 / s0546935
 */
public interface RoutingKPListener {
    /**
     * gets triggered if a new message was received 
     * @param message message content as String
     * @param originator original sender of this message
     * @param sender actual sender of this message
     * @throws SharkException
     * @throws IOException 
     */
    public void messageReceived(String message, PeerSemanticTag originator, PeerSemanticTag sender) throws SharkException, IOException;
    
    /**
     * gets triggered if a new message was received 
     * @param message message content as byte[]
     * @param originator original sender of this message
     * @param sender actual sender of this message
     * @throws SharkException
     * @throws IOException 
     */
    public void messageReceived(byte[] message, PeerSemanticTag originator, PeerSemanticTag sender) throws SharkException, IOException;
    
    /**
     * gets triggered if a new contact was added to the contactlist
     * @param contact Peer
     */
    public void addedContact(RoutingPeer contact);
}

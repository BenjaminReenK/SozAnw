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
 * @author s0546862 / s0546935
 * Simple Routing Interface 
 */
public interface RoutingInterface {
    
    /**
     * send message from sender to recipient with specific time to live and max amount of hops
     * @param sender who sends this message
     * @param receiver who should receive this message
     * @param message message content as string. Message won't get encrypted
     * @param ttl time to live value in msec
     * @param hops max hop count, gets decremented by every peer
     * @throws SharkException
     * @throws IOException 
     */
    public void sendMessage(PeerSemanticTag sender, PeerSemanticTag receiver, String message, int ttl, int hops) throws SharkException, IOException;

    /**
    * send message from sender to recipient with specific time to live and max amount of hops
    * @param sender who sends this message
    * @param receiver who should receive this message
    * @param message message content as byte[]. YOU SHOULD ENCRYPT IT BEFORE SENDING!
    * @param ttl time to live value in msec
    * @param hops max hop count, gets decremented by every peer
    * @throws SharkException
    * @throws IOException 
    */
    public void sendMessage(PeerSemanticTag sender, PeerSemanticTag receiver, byte[] message, int ttl, int hops) throws SharkException, IOException;
}


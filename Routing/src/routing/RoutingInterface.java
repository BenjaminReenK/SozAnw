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
 * @author Benni
 */
public interface RoutingInterface {
    //send message from sender to recipient with specific time to live and max amount of hops
    public void sendMessage(PeerSemanticTag sender, PeerSemanticTag reciever, String message, int ttl, int hops) throws SharkException, IOException;

    //same as sendMsg just vice versa
    public void recieveMessage(PeerSemanticTag sender, PeerSemanticTag reciever, String message, int ttl, long msgTime, int hops) throws SharkException, IOException;
}


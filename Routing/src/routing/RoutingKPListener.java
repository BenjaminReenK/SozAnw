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
public interface RoutingKPListener {
    public void messageReceived(String message, PeerSemanticTag sender) throws SharkException, IOException;
}

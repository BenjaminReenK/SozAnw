/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import java.io.IOException;
import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.system.SharkException;

/**
 *
 * @author Benni
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SharkProtocolNotSupportedException, IOException, SharkException {
        RoutingPeer alice = new RoutingPeer("alice", "http://sharksystems.net/alice", "tcp://localhost:7000", 7000);
        RoutingPeer bob = new RoutingPeer("bob", "http://sharksystems.net/bob", "tcp://localhost:7001", 7001);
        RoutingPeer chris = new RoutingPeer("chris", "http://sharksystems.net/chris", "tcp://localhost:7002", 7002);
        
        alice.routingKP.sendMessage(alice.getOwnPeerTag(), bob.getOwnPeerTag(), "hallo", RoutingKP.TTL_HOUR, RoutingKP.MAX_HOPS);
        
        
        
        alice.stop();
        bob.stop();
        chris.stop();
    }
    
}

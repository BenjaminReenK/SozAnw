/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.SharkException;

/**
 *
 * @author Benni
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SharkProtocolNotSupportedException, IOException, SharkException, SharkKBException, NoSuchAlgorithmException, ParseException {
        RoutingPeer alice = new RoutingPeer("alice", "http://sharksystems.net/alice", "tcp://localhost:7000", 7000);
        RoutingPeer bob = new RoutingPeer("bob", "http://sharksystems.net/bob", "tcp://localhost:7001", 7001);
        RoutingPeer chris = new RoutingPeer("chris", "http://sharksystems.net/chris", "tcp://localhost:7002", 7002);
        RoutingPeer dan = new RoutingPeer("dan", "http://sharksystems.net/dan", "tcp://localhost:7003", 7003);
        RoutingPeer erica = new RoutingPeer("erica", "http://sharksystems.net/erica", "tcp://localhost:7004", 7004);
        
        ArrayList<RoutingPeer> peers = new ArrayList<>();
        peers.add(alice);
        peers.add(bob);
        peers.add(chris);
        peers.add(dan);
        // dont merge erica, so only dan knows about erica
        //peers.merge(erica.getOwnPeerTag());
       
        
        alice.routingKP.addPeers(peers);
        bob.routingKP.addPeers(peers);
        chris.routingKP.addPeers(peers);
        dan.routingKP.addPeers(peers);
        // only dan knows our receiver erica, so alice can't send the
        // message directly to erica
        dan.routingKP.addPeerContact(erica);
        erica.routingKP.addPeers(peers);
        
        alice.routingKP.sendMessage(alice.getOwnPeerTag(), erica.getOwnPeerTag(), "hallo", RoutingKP.TTL_HOUR, RoutingKP.MAX_HOPS);
        
    }
}

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
import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.SharkException;

/**
 *
 * @author s0546862 / s0546935
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SharkProtocolNotSupportedException, IOException, SharkException, SharkKBException, NoSuchAlgorithmException, ParseException {
        
        // Creating some RoutingPeers
        RoutingPeer alice = new RoutingPeer("alice", "http://sharksystems.net/alice", "tcp://localhost:7000", 7000);
        RoutingPeer bob = new RoutingPeer("bob", "http://sharksystems.net/bob", "tcp://localhost:7001", 7001);
        RoutingPeer chris = new RoutingPeer("chris", "http://sharksystems.net/chris", "tcp://localhost:7002", 7002);
        RoutingPeer dan = new RoutingPeer("dan", "http://sharksystems.net/dan", "tcp://localhost:7003", 7003);
        RoutingPeer erica = new RoutingPeer("erica", "http://sharksystems.net/erica", "tcp://localhost:7004", 7004);
        
        // Creating our contactlist
        // we don't add erica to this contact list, so no one knows about erica for the moment
        ArrayList<RoutingPeer> peers = new ArrayList<>();
        peers.add(alice);
        peers.add(bob);
        peers.add(chris);
        peers.add(dan);
        
        // publish our contact list to every peer
        alice.routingKP.addPeers(peers);
        bob.routingKP.addPeers(peers);
        chris.routingKP.addPeers(peers);
        dan.routingKP.addPeers(peers);
        // only dan knows our receiver erica, so alice can't send the
        // message directly to erica
        dan.routingKP.addPeerContact(erica);
        erica.routingKP.addPeers(peers);
        
        // send string message
        alice.routingKP.sendMessage(alice.getOwnPeerTag(), erica.getOwnPeerTag(), "hallo", RoutingKP.TTL_HOUR, RoutingKP.MAX_HOPS);
        
        /*
        // the msg we want to send
        String msg = "bytetest";
        // the msg "encryption" password
        String key = "geheimespasswort";
        // "encrypt" the message
        byte[] encrypted = xor(msg.getBytes(), key.getBytes());
        // send "encrypted" byte[] message
        alice.routingKP.sendMessage(alice.getOwnPeerTag(), erica.getOwnPeerTag(), encrypted, RoutingKP.TTL_HOUR, RoutingKP.MAX_HOPS);
        */   
    }
    
    public static byte[] xor(byte[] a, byte[] b) {
        byte[] result = new byte[Math.min(a.length, b.length)];

        for (int i = 0; i < result.length; i++) {
          result[i] = (byte) (((int) a[i]) ^ ((int) b[i]));
        }

        return result;
    }
}

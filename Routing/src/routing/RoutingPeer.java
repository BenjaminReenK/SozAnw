/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import java.io.IOException;
import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.SharkException;

/**
 *
 * @author Benni
 */
public class RoutingPeer implements RoutingKPListener{
    private SharkEngine se = null;
    private PeerSemanticTag ownPeerTag = null;
    private SharkKB kb = null;
    public RoutingKP routingKP;
    
    public RoutingPeer(String peerName, String peerSI, String peerAdress, int port) throws SharkProtocolNotSupportedException, IOException {
        // create SharkEngine
        this.se = new J2SEAndroidSharkEngine();
        
        // create KB
        this.kb = new InMemoSharkKB();
        
        // create own peer tag
        this.ownPeerTag = InMemoSharkKB.createInMemoPeerSemanticTag(peerName, peerSI, peerAdress);
        
        // create routing knowledgeport
        this.routingKP = new RoutingKP(this.se, this.ownPeerTag, kb);
        
        // add listener
        this.routingKP.addListener(this);
        
        // start tcp listening
        this.se.startTCP(port);
    }
    
    public void stop() {
        this.se.stop();
    }
    
    public PeerSemanticTag getOwnPeerTag() {
        return this.ownPeerTag;
    }

    @Override
    public void messageReceived(String message, PeerSemanticTag sender) throws SharkException, IOException {
        System.out.println("listener msg: " + message);
        System.out.println("msg sender: " + sender.getName());
        
    }
}

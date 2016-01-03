/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.security.key.SharkKeyGenerator;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.security.pki.Certificate;
import net.sharkfw.security.pki.storage.SharkPkiStorage;
import net.sharkfw.system.SharkException;
import net.sharkfw.security.pki.SharkCertificate;
import net.sharkfw.system.SharkSecurityException;

/**
 *
 * @author Benni
 */
public class RoutingPeer implements RoutingKPListener{
    private J2SEAndroidSharkEngine se = null;
    private PeerSemanticTag ownPeerTag = null;
    private SharkKB kb = null;
    private SharkKeyGenerator keyGenerator = null;
    private SharkPkiStorage pkiStorage = null;
    private Date date = null;
    private final String DATE_TIME = "01.01.2100";
    public RoutingKP routingKP = null;
    
    public RoutingPeer(String peerName, String peerSI, String peerAdress, int port) throws SharkProtocolNotSupportedException, IOException, SharkKBException, NoSuchAlgorithmException, ParseException {
        // create SharkEngine
        this.se = new J2SEAndroidSharkEngine();
        
        // create KB
        this.kb = new InMemoSharkKB();
                
        // create own peer tag
        this.ownPeerTag = InMemoSharkKB.createInMemoPeerSemanticTag(peerName, peerSI, peerAdress);
        
        // create keygenerator
        this.keyGenerator = new SharkKeyGenerator(SharkKeyPairAlgorithm.RSA, 1024);
        
        // create PKI Storage
        this.pkiStorage = new SharkPkiStorage(this.kb, this.ownPeerTag, this.keyGenerator.getPrivateKey());
        
        // creating date for usage in certificate
        this.date = new Date();
        this.date.setTime(new SimpleDateFormat("dd.MM.yyyy").parse(DATE_TIME).getTime());
        
        // create routing knowledgeport
        this.routingKP = new RoutingKP(this.se, this.ownPeerTag, kb);
        
        // add listener
        this.routingKP.addListener(this);
        
        // set connection timeout
        this.se.setConnectionTimeOut(10000);
        
        // start tcp listening
        this.se.startTCP(port);
    }
    
    public void stop() {
        this.se.stop();
    }
    
    public PeerSemanticTag getOwnPeerTag() {
        return this.ownPeerTag;
    }
    
    public PublicKey getPublicKey() {
        return this.keyGenerator.getPublicKey();
    }

    @Override
    public void messageReceived(String message, PeerSemanticTag originator, PeerSemanticTag sender) throws SharkException, IOException {
        System.out.println("listener msg: " + message);
        System.out.println("msg Originator: " + originator.getName());
        System.out.println("msg sender: " + sender.getName());
        
    }

    @Override
    public void addedContact(RoutingPeer contact) {
        try {
            LinkedList<PeerSemanticTag> peerList = new LinkedList<>();
            peerList.addFirst(this.ownPeerTag);
            // create certificate for new contact
            this.pkiStorage.addSharkCertificate(new SharkCertificate(contact.getOwnPeerTag(), this.ownPeerTag, peerList, Certificate.TrustLevel.FULL, contact.getPublicKey(), date));
            // init security stuff
            this.se.initSecurity(this.ownPeerTag, this.pkiStorage, SharkEngine.SecurityLevel.MUST, SharkEngine.SecurityLevel.NO, SharkEngine.SecurityReplyPolicy.SAME, false);
        } catch (SharkKBException ex) {
            System.out.println(ex);
        } catch (SharkSecurityException ex) {
            System.out.println(ex);
        }
    }
}

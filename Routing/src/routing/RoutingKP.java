/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.SharkException;


/**
 *
 * @author Benni
 */
public class RoutingKP extends KnowledgePort implements RoutingInterface{

    public static final String ROUTING_TOPIC_SI = "http://sharksystems.net/routing";
    public static final int MAX_HOPS = 100;
    // minute in msec
    public static final int TTL_MINUTE = 1000 * 60;
    // hour in msec
    public static final int TTL_HOUR = TTL_MINUTE * 60;
    // day in msec
    public static final int TTL_DAY = TTL_HOUR * 24;
    private SemanticTag routingTopic = null;
    private PeerSemanticTag owner = null;
    private RoutingKPListener listener = null;
    // every peer / kp needs a "contactlist" to forward the received msg
    private ArrayList<RoutingPeer> contactList = null;
    
    public RoutingKP(SharkEngine se, PeerSemanticTag owner, SharkKB kb) {
        super(se);
        this.owner = owner;
        this.kb = kb;
        this.kb.setOwner(owner);
        this.routingTopic = InMemoSharkKB.createInMemoSemanticTag("RoutingTopic", RoutingKP.ROUTING_TOPIC_SI);
        this.contactList = new ArrayList<>();
       
    }
    
    @Override
    protected void doInsert(Knowledge knwldg, KEPConnection kepc) {
        System.out.println("------------------------");
        System.out.println("incoming knowledge - KP Owner:" + this.owner.getName());
        // get contextpoint  
        Enumeration<ContextPoint> cpEnum = knwldg.contextPoints();
        // if contextpoint is found 
        if (cpEnum != null) {
            ContextPoint cp = cpEnum.nextElement();
            ContextCoordinates cc = cp.getContextCoordinates();
            SemanticTag msgTopic = cc.getTopic();
            // cancel if msg / topic isn't about routing
            if (!SharkCSAlgebra.identical(msgTopic, this.routingTopic)) {
                return;
            }
            // extract peer informations
            PeerSemanticTag originator = cc.getOriginator();
            PeerSemanticTag sender = cc.getPeer();
            PeerSemanticTag receiver = cc.getRemotePeer();
            // extract msg informations
            Iterator<Information> infoIter = cp.getInformation();
            if (infoIter != null) {
                try {
                    String msg = infoIter.next().getContentAsString();
                    int ttl = Integer.valueOf(infoIter.next().getContentAsString());
                    int hops = Integer.valueOf(infoIter.next().getContentAsString());
                    long time = Long.valueOf(infoIter.next().getContentAsString());
                    // trigger receiveMessage
                    this.recieveMessage(originator, sender, receiver, msg, ttl, time, hops);
                } catch (SharkKBException ex) {
                    Logger.getLogger(RoutingKP.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SharkException ex) {
                    Logger.getLogger(RoutingKP.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(RoutingKP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    protected void doExpose(SharkCS scs, KEPConnection kepc) {
        // not used
    }

    // used for initial msg sending, set msgtime to "now"
    @Override
    public void sendMessage(PeerSemanticTag sender, PeerSemanticTag reciever, String message, int ttl, int hops) throws SharkException, IOException{
        Long now = System.currentTimeMillis();
        forwardMessage(sender, reciever, message, ttl, now, hops);
    }
    
    // similiar to sendMessage, just using original msgTime as parameter for ttl handling
    // and seperate originator / sender
    private void forwardMessage(PeerSemanticTag originator, PeerSemanticTag reciever, String message, int ttl, long msgTime, int hops) throws SharkException, IOException{
               
        // Creating context coordinates in kb
        ContextCoordinates cc = this.kb.createContextCoordinates(
                routingTopic,
                originator, // set originator of this msg
                this.owner, // set this peer as sender
                reciever, // set final reciever of this msg
                null,
                null,
                SharkCS.DIRECTION_OUT);
        // create context point for these context coordinates                
        ContextPoint cp = this.kb.createContextPoint(cc);
        // add message
        cp.addInformation(message);
        // add ttl
        cp.addInformation(Integer.toString(ttl));
        // add hops
        cp.addInformation(Integer.toString(hops));
        // add message create time for ttl
        cp.addInformation(Long.toString(msgTime));
        // create the knowledge      
        Knowledge k = this.kb.createKnowledge();
        // add our context point with all the message informations
        k.addContextPoint(cp);
        // send knowledge to all known contacts
        for (int i = 0; i < this.contactList.size(); i++) {
            this.sendKnowledge(k, this.contactList.get(i).getOwnPeerTag());
        }
    }

    @Override
    public void recieveMessage(PeerSemanticTag originator, PeerSemanticTag sender, PeerSemanticTag reciever, String message, int ttl,  long msgTime, int hops) throws SharkException, IOException{
        // notifiy listener about new message
        this.listener.messageReceived(message, originator, sender);
        // decrease hop count
        hops -= 1;
        // print out some msg informations
        System.out.println("TTL: " + ttl);
        System.out.println("Remaining Hops: " + hops);
        System.out.println("Creation time: " + msgTime);
        Date date = new Date(msgTime);
        System.out.println(date);
        // get current time in msec
        long now = System.currentTimeMillis();
        // timedifference between msg sending and receiving in msec
        long timeDiff = now - (msgTime);
        System.out.println("Current time: " + now);
        System.out.println("timediff: " + timeDiff);
        
        // check if the msg is for this peer, if yes abort
        if (reciever.getSI()[0].equals(this.owner.getSI()[0])) {
            System.out.println("msg reached the receiver - abort msg forwarding");
            return;
        }
        
        // check if we already processed this msg, temporary context coordinates
        ContextCoordinates cc = InMemoSharkKB.createInMemoContextCoordinates(
                routingTopic,
                originator, // set originator of this msg
                this.owner, // set this peer as sender
                reciever, // set final reciever of this msg
                null,
                null,
                SharkCS.DIRECTION_OUT);
        // check if we have a contextpoint for these context coordinates
        ContextPoint p = this.kb.getContextPoint(cc);
        if (p != null) {
            System.out.println("cp found - KP Owner: " + this.owner.getName());
            Iterator<Information> infoIter = p.getInformation();
            // extract msg informations from found context point
            if(infoIter != null) {
                String cpMsg;
                long cpMsgTime;
                // message content
                cpMsg = infoIter.next().getContentAsString();
                // skip ttl
                infoIter.next();
                // skip hops
                infoIter.next();
                // message creation time
                cpMsgTime = Long.valueOf(infoIter.next().getContentAsString());
                // message content and time are equal, so it's "safe" to say we already stored this msg
                if (cpMsg.equals(message) && cpMsgTime == msgTime) {
                    System.out.println("Already processed this msg - abort forwarding");
                    return;
                }
            }
        }
               
        // ttl exceeded - abort
        if (timeDiff > ttl) {
            System.out.println("ttl exceeded");
            return;
        }
        
        System.out.println("------------------------");
        
        
        // spread msg to all known contacts if hop count allows it
        if (hops > 0) {
            forwardMessage(originator, reciever, message, ttl, msgTime, hops);
        }
        else {
            System.out.println("Hop count too low - stop forwarding the msg");
        }
    }
    
    public void addListener(RoutingKPListener listener) {
        this.listener = listener;
    }
    
    /*
    // add a single peer contact
    public void addPeerContact(PeerSemanticTag p) {
        this.contactList.add(p);
    }
    // delete a single peer contact
    public void delPeerContact(PeerSemanticTag p) {
        this.contactList.remove(p);
    }
    // add multiple peer contacts
    public void addPeers(PeerSTSet peers) {
        Enumeration<PeerSemanticTag> enumPeers = peers.peerTags();
        while (enumPeers.hasMoreElements()) {
            PeerSemanticTag tempPeer = enumPeers.nextElement();
            // don't add own peertag to contactlist
            if (!tempPeer.getSI()[0].equals(this.owner.getSI()[0])) {
                this.contactList.add(tempPeer);
            }
        }
    }
    */
    // add a single peer contact
    public void addPeerContact(RoutingPeer p) {
        this.contactList.add(p);
        this.listener.addedContact(p);
    }
    // delete a single peer contact
    public void delPeerContact(RoutingPeer p) {
        this.contactList.remove(p);
    }
    // add multiple peer contacts
    public void addPeers(ArrayList<RoutingPeer> peers) {
        
        for (int i = 0; i < peers.size(); i++) {
            PeerSemanticTag tempPeer = peers.get(i).getOwnPeerTag();
            if (!tempPeer.getSI()[0].equals(this.owner.getSI()[0])) {
                this.contactList.add(peers.get(i));
                this.listener.addedContact(peers.get(i));
            }
        }
    }
}

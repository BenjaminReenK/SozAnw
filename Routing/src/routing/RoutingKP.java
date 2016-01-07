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
import java.util.Timer;
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
 * @author @author s0546862 / s0546935
 * RoutingKP Class which implements our RoutingInterface
 * sendMessage() is realized by sending Knowledge
 */
public class RoutingKP extends KnowledgePort implements RoutingInterface{
    // Routing Subject Identifier
    public static final String ROUTING_TOPIC_SI = "http://sharksystems.net/routing";
    public static final int CONTENT_STRING = 0;
    public static final int CONTENT_BYTE = 1;
    // max hop count
    public static final int MAX_HOPS = 100;
    // minute in msec
    public static final int TTL_MINUTE = 1000 * 60;
    // hour in msec
    public static final int TTL_HOUR = TTL_MINUTE * 60;
    // day in msec
    public static final int TTL_DAY = TTL_HOUR * 24;
    // SemanticTag used as "Flag" for routing messages
    private SemanticTag routingTopic = null;
    private PeerSemanticTag owner = null;
    private RoutingKPListener listener = null;
    // every peer / kp needs a "contactlist" to forward the received msg
    private ArrayList<RoutingPeer> contactList = null;
    // check ttl for every message in a defined time interval
    private Timer ttlScheduler = null;
    private TTLCheck ttlCheck = null;
    // used for msg forwarding.. if we already processed the msg but 
    // got a new contact, we still forward the message
    private boolean freshContact = false;
    
    public RoutingKP(SharkEngine se, PeerSemanticTag owner, SharkKB kb) {
        super(se);
        this.owner = owner;
        this.kb = kb;
        this.kb.setOwner(owner);
        this.routingTopic = InMemoSharkKB.createInMemoSemanticTag("RoutingTopic", RoutingKP.ROUTING_TOPIC_SI);
        this.contactList = new ArrayList<>();
        this.ttlScheduler = new Timer();
        this.ttlCheck = new TTLCheck(this.kb);
        // delay start for 1min
        this.ttlScheduler.schedule(ttlCheck, 60000, 60000);
    }
    
    @Override
    protected void doInsert(Knowledge knwldg, KEPConnection kepc) {
        System.out.println("------------------------");
        System.out.println("incoming knowledge - KP Owner: " + this.owner.getName());
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
                    String msg = "";
                    byte[] msgContent = null;
                    String msgID = infoIter.next().getContentAsString();
                    int contentType = Integer.valueOf(infoIter.next().getContentAsString());
                    // extract msg content, depending on content type
                    if (contentType == CONTENT_STRING) {
                        msg = infoIter.next().getContentAsString();
                    } else if (contentType == CONTENT_BYTE) {
                        msgContent = infoIter.next().getContentAsByte();
                    }
                    // extract ttl and some other stuff
                    int ttl = Integer.valueOf(infoIter.next().getContentAsString());
                    int hops = Integer.valueOf(infoIter.next().getContentAsString());
                    long time = Long.valueOf(infoIter.next().getContentAsString());
                    // process this msg
                    if (contentType == CONTENT_STRING) {
                        this.receivedMessage(originator, sender, receiver, msg, ttl, time, hops, msgID);
                    } else if (contentType == CONTENT_BYTE) {
                        this.receivedMessage(originator, sender, receiver, msgContent, ttl, time, hops, msgID);
                    }
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
        // not used, we don't expect any incoming interests
        System.out.println("------------------------");
        System.out.println("incoming interest - KP Owner:" + this.owner.getName());
    }

    // used for initial msg sending, set msgtime to "now"
    // and create msgid
    @Override
    public void sendMessage(PeerSemanticTag sender, PeerSemanticTag receiver, String message, int ttl, int hops) throws SharkException, IOException{
        Long now = System.currentTimeMillis();
        // create unique message id from originator SI, timestamp and random number, in case
        // someone achieves it to send more than one message in 1 msec
        String msgID = sender.getSI()[0] + now + RoutingPeer.randInt(1, 10000);
        forwardMessage(sender, receiver, message, ttl, now, hops, msgID);
    }
    
    // used for initial msg sending, set msgtime to "now"
    // and create msgid
    @Override
    public void sendMessage(PeerSemanticTag sender, PeerSemanticTag receiver, byte[] message, int ttl, int hops) throws SharkException, IOException {
        Long now = System.currentTimeMillis();
        // create unique message id from originator SI, timestamp and random number, in case
        // someone achieves it to send more than one message in 1 msec
        String msgID = sender.getSI()[0] + now + RoutingPeer.randInt(1, 10000);
        forwardMessage(sender, receiver, message, ttl, now, hops, msgID);
    }
    
    
    // similiar to sendMessage, just using original msgTime as parameter for ttl handling
    // and seperate originator / sender
    private void forwardMessage(PeerSemanticTag originator, PeerSemanticTag receiver, String message, int ttl, long msgTime, int hops, String msgID) throws SharkException, IOException{
        
        // Creating context coordinates in kb
        ContextCoordinates cc = this.kb.createContextCoordinates(
                routingTopic,
                originator, // set originator of this msg
                this.owner, // set this peer as sender
                receiver, // set final reciever of this msg
                null,
                null,
                SharkCS.DIRECTION_OUT);
        // create context point for these context coordinates                
        ContextPoint cp = this.kb.createContextPoint(cc);

        /* not working atm, receiver doesn't get the knowledge.. don't know why
        Information msgInfo = new MessageInformation();
        msgInfo.setContent(message);
        msgInfo.setName("msgcontent");
        cp.addInformation(msgInfo);*/
        
        // add msgid
        cp.addInformation(msgID);
        // add contentType
        cp.addInformation(Integer.toString(CONTENT_STRING));
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
        // check if we know the receiver, if yes, send msg only to this one
        if (isContactInList(receiver)) {
            this.sendKnowledge(k, receiver);
        }
        // send knowledge to all known contacts if reciever is unknown
        else {
            for (int i = 0; i < this.contactList.size(); i++) {
                this.sendKnowledge(k, this.contactList.get(i).getOwnPeerTag());
            }  
        }
    }
    
    // similiar to sendMessage, just using original msgTime as parameter for ttl handling
    // and seperate originator / sender
    private void forwardMessage(PeerSemanticTag originator, PeerSemanticTag receiver, byte[] message, int ttl, long msgTime, int hops, String msgID) throws SharkException, IOException{
        
        // Creating context coordinates in kb
        ContextCoordinates cc = this.kb.createContextCoordinates(
                routingTopic,
                originator, // set originator of this msg
                this.owner, // set this peer as sender
                receiver, // set final reciever of this msg
                null,
                null,
                SharkCS.DIRECTION_OUT);
        // create context point for these context coordinates                
        ContextPoint cp = this.kb.createContextPoint(cc);

        /* not working atm, receiver doesn't get the knowledge.. don't know why
        Information msgInfo = new MessageInformation();
        msgInfo.setContent(message);
        msgInfo.setName("msgcontent");
        cp.addInformation(msgInfo);*/
        
        // add msgid
        cp.addInformation(msgID);
        // add contentType
        cp.addInformation(Integer.toString(CONTENT_BYTE));
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
        // check if we know the receiver, if yes, send msg only to this one
        if (isContactInList(receiver)) {
            this.sendKnowledge(k, receiver);
        }
        // send knowledge to all known contacts if reciever is unknown
        else {
            for (int i = 0; i < this.contactList.size(); i++) {
                this.sendKnowledge(k, this.contactList.get(i).getOwnPeerTag());
            }  
        }
    }

    // called from doInsert() for further message processing
    public void receivedMessage(PeerSemanticTag originator, PeerSemanticTag sender, PeerSemanticTag receiver, String message, int ttl,  long msgTime, int hops, String msgID) throws SharkException, IOException{
        // notifiy listener about new message
        if (this.listener != null) {
            this.listener.messageReceived(message, originator, sender);
        }
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
        if (SharkCSAlgebra.identical(receiver, this.owner))
        {
            System.out.println("msg reached the receiver - abort msg forwarding");
            return;
        }
              
        // check if we already processed this msg, temporary context coordinates
        ContextCoordinates cc = InMemoSharkKB.createInMemoContextCoordinates(
                routingTopic,
                originator, // set originator of this msg
                this.owner, // set this peer as sender
                receiver, // set final reciever of this msg
                null,
                null,
                SharkCS.DIRECTION_OUT);
        // check if we have one or more contextpoints for these context coordinates
        Enumeration<ContextPoint> enumCP = this.kb.getContextPoints(cc);
        if (enumCP != null) {
            while (enumCP.hasMoreElements()) {
                ContextPoint p = enumCP.nextElement();
                Iterator<Information> infoIter = p.getInformation();
                // extract msg informations from found context point
                if(infoIter != null) {
                    // extract msgID
                    String cpMsgID = infoIter.next().getContentAsString();
                    // check if we already processed this msgid and we have no new contacts for msg spreading
                    if (cpMsgID.equals(msgID) && !this.freshContact) {
                        System.out.println("Already processed this msg - abort forwarding");
                        return;
                    }
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
            forwardMessage(originator, receiver, message, ttl, msgTime, hops, msgID);
        }
        else {
            System.out.println("Hop count too low - stop forwarding the msg");
        }
        // all message sending stuff is done, so reset freshcontact
        this.freshContact = false;
    }
    
    // called from doInsert() for further message processing
    public void receivedMessage(PeerSemanticTag originator, PeerSemanticTag sender, PeerSemanticTag receiver, byte[] message, int ttl,  long msgTime, int hops, String msgID) throws SharkException, IOException{
        // notifiy listener about new message
        if (this.listener != null) {
            this.listener.messageReceived(message, originator, sender);
        }
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
        if (SharkCSAlgebra.identical(receiver, this.owner))
        {
            System.out.println("msg reached the receiver - abort msg forwarding");
            return;
        }
              
        // check if we already processed this msg, temporary context coordinates
        ContextCoordinates cc = InMemoSharkKB.createInMemoContextCoordinates(
                routingTopic,
                originator, // set originator of this msg
                this.owner, // set this peer as sender
                receiver, // set final reciever of this msg
                null,
                null,
                SharkCS.DIRECTION_OUT);
        // check if we have one or more contextpoints for these context coordinates
        Enumeration<ContextPoint> enumCP = this.kb.getContextPoints(cc);
        if (enumCP != null) {
            while (enumCP.hasMoreElements()) {
                ContextPoint p = enumCP.nextElement();
                Iterator<Information> infoIter = p.getInformation();
                // extract msg informations from found context point
                if(infoIter != null) {
                    // extract msgID
                    String cpMsgID = infoIter.next().getContentAsString();
                    // check if we already processed this msgid
                    if (cpMsgID.equals(msgID)) {
                        System.out.println("Already processed this msg - abort forwarding");
                        return;
                    }
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
            forwardMessage(originator, receiver, message, ttl, msgTime, hops, msgID);
        }
        else {
            System.out.println("Hop count too low - stop forwarding the msg");
        }
        // all message sending stuff is done, so reset freshcontact
        this.freshContact = false;
    }
    
    // add a listener, currently only one listener is supported
    public void addListener(RoutingKPListener listener) {
        this.listener = listener;
    }
    
    // add a single peer contact
    public void addPeerContact(RoutingPeer p) {
        // if contact isn't in our contact list already
        if (!isContactInList(p.getOwnPeerTag())) {
            this.contactList.add(p);
            this.freshContact = true;
            if (this.listener != null) {
                this.listener.addedContact(p);
            }
        }
    }
    
    // delete a single peer contact
    public void delPeerContact(RoutingPeer p) {
        this.contactList.remove(p);
    }
    
    // add multiple peer contacts
    public void addPeers(ArrayList<RoutingPeer> peers) {
        for (int i = 0; i < peers.size(); i++) {
            PeerSemanticTag tempPeer = peers.get(i).getOwnPeerTag();
            // don't add own peer tag, to prevent message loop
            if (!SharkCSAlgebra.identical(tempPeer, this.owner)) {
                // if contact isn't in our contact list already
                if (!isContactInList(peers.get(i).getOwnPeerTag())) {
                    this.contactList.add(peers.get(i));
                    this.freshContact = true;
                    if (this.listener != null) {
                        this.listener.addedContact(peers.get(i));
                    }
                }
            }
        }
    }
    
    // check if a contact is in our contactlist
    private boolean isContactInList(PeerSemanticTag p) {
        boolean result = false;
        for (int i = 0; i < this.contactList.size(); i++) {
            if (this.contactList.get(i).getOwnPeerTag().getSI()[0].equals(p.getSI()[0])) {
                result = true;
                break;
            }
        }
        return result;
    }
}

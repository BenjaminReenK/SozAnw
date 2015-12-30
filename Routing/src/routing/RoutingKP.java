/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
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
import net.sharkfw.system.SharkSecurityException;

/**
 *
 * @author Benni
 */
public class RoutingKP extends KnowledgePort implements RoutingInterface{

    public static final String ROUTING_TOPIC_SI = "http://sharksystems.net/routing";
    public static final int MAX_HOPS = 100;
    public static final int TTL_MINUTE = 1000 * 60;
    public static final int TTL_HOUR = TTL_MINUTE * 60;
    public static final int TTL_DAY = TTL_HOUR * 24;
    private SemanticTag routingTopic;
    private PeerSemanticTag owner;
    private RoutingKPListener listener = null;
    
    public RoutingKP(SharkEngine se, PeerSemanticTag owner, SharkKB kb) {
        super(se);
        this.owner = owner;
        this.kb = kb;
        this.routingTopic = InMemoSharkKB.createInMemoSemanticTag("RoutingTopic", RoutingKP.ROUTING_TOPIC_SI);
    }
    
    @Override
    protected void doInsert(Knowledge knwldg, KEPConnection kepc) {
        System.out.println("incoming knowledge");
        
        Enumeration<ContextPoint> cpEnum = knwldg.contextPoints();
        
        if (cpEnum != null) {
            ContextPoint cp = cpEnum.nextElement();
            ContextCoordinates cc = cp.getContextCoordinates();
            SemanticTag msgTopic = cc.getTopic();
            // cancel if msg / topic isn't about routing
            if (!SharkCSAlgebra.identical(msgTopic, this.routingTopic)) {
                return;
            }
            
            PeerSemanticTag sender = cc.getPeer();
            // extract msg informations
            Iterator<Information> infoIter = cp.getInformation();
            if (infoIter != null) {
                try {
                    String msg = infoIter.next().getContentAsString();
                    int ttl = Integer.valueOf(infoIter.next().getContentAsString());
                    int hops = Integer.valueOf(infoIter.next().getContentAsString());
                    long time = Integer.valueOf(infoIter.next().getContentAsString());
                    this.recieveMessage(sender, sender, msg, ttl, time, hops);
                } catch (SharkKBException ex) {
                    Logger.getLogger(RoutingKP.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SharkException ex) {
                    Logger.getLogger(RoutingKP.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(RoutingKP.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
        }
        
        
        
        

        //this.re
    }

    @Override
    protected void doExpose(SharkCS scs, KEPConnection kepc) {
        
    }

    @Override
    public void sendMessage(PeerSemanticTag sender, PeerSemanticTag reciever, String message, int ttl, int hops) throws SharkException, IOException{
        ContextCoordinates cc = InMemoSharkKB.createInMemoContextCoordinates(
                routingTopic,
                this.owner,
                this.owner,
                reciever,
                null,
                null,
                SharkCS.DIRECTION_OUT);
        ContextPoint cp = InMemoSharkKB.createInMemoContextPoint(cc);
        // add message
        cp.addInformation(message);
        // add ttl
        cp.addInformation(Integer.toString(ttl));
        // add hops
        cp.addInformation(Integer.toString(hops));
        // add message create time for ttl as unix timestamp
        // convert to seconds, because unix time is in seconds, java in ms
        cp.addInformation(Long.toString((System.currentTimeMillis() / 1000L)));
        // create the knowledge      
        Knowledge k = InMemoSharkKB.createInMemoKnowledge();
        // add our context point with all the message informations
        k.addContextPoint(cp);
        this.sendKnowledge(k, reciever);
      
        
        
    }

    @Override
    public void recieveMessage(PeerSemanticTag sender, PeerSemanticTag reciever, String message, int ttl,  long msgTime, int hops) throws SharkException, IOException{
        System.out.println("TTL: " + ttl);
        System.out.println("Hops: " + hops);
        System.out.println("Creation time: " + msgTime);
        // convert unix timestamp back to java time (sec -> msec) 
        Date date = new Date(msgTime * 1000);
        System.out.println(date);
        // TODO: handle ttl and hops
        
        
        // store message in kb
        STSet topic = InMemoSharkKB.createInMemoSTSet();
        topic.createSemanticTag(this.routingTopic.getName(), this.routingTopic.getSI());
        
        Interest interest = InMemoSharkKB.createInMemoInterest(
                topic,
                null,
                null,
                null,
                null,
                null,
                SharkCS.DIRECTION_INOUT);
        
        FragmentationParameter[] fp = new FragmentationParameter[SharkCS.MAXDIMENSIONS];
        for (int i = 0; i < SharkCS.MAXDIMENSIONS; i++) {
            fp[i] = FragmentationParameter.getZeroFP();
        }
        SharkCSAlgebra.assimilate(this.kb, interest, fp, null, true, true);
        
        // notifiy listener about new message
        this.listener.messageReceived(message, sender);
      
    }
    
  
    
    public void addListener(RoutingKPListener listener) {
        this.listener = listener;
    }
}

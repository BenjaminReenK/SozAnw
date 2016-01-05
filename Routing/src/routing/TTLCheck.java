/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

/**
 *
 * @author Benni
 */
public class TTLCheck extends TimerTask{

    private SharkKB kb;
    
    public TTLCheck(SharkKB kb) {
        this.kb = kb;
    }
    
    @Override
    public void run() {
        SemanticTag routingTopic = InMemoSharkKB.createInMemoSemanticTag("RoutingTopic", RoutingKP.ROUTING_TOPIC_SI);
        // get all msg's with routingtopic
        ContextCoordinates cc = InMemoSharkKB.createInMemoContextCoordinates(
                routingTopic,
                null, 
                null, 
                null, 
                null,
                null,
                SharkCS.DIRECTION_OUT);
        // check if we have one or more contextpoints for these context coordinates
        Enumeration<ContextPoint> enumCP = null;
        try {
            enumCP = this.kb.getContextPoints(cc);
        } catch (SharkKBException ex) {
            System.out.println(ex);
        }
        if (enumCP != null) {
            while (enumCP.hasMoreElements()) {
                ContextPoint p = enumCP.nextElement();
                Iterator<Information> infoIter = p.getInformation();
                // extract msg informations from found context point
                if(infoIter != null) {
                    try {
                        long cpMsgTime, timediff, now;
                        int cpTTL;
                        // skip msg content
                        infoIter.next();
                        // ttl
                        cpTTL = Integer.valueOf(infoIter.next().getContentAsString());
                        // skip hops
                        infoIter.next();
                        // message creation time
                        cpMsgTime = Long.valueOf(infoIter.next().getContentAsString());
                        now = System.currentTimeMillis();
                        timediff = now - cpMsgTime;
                        // if ttl is exceeded, remove this contextpoint
                        if (timediff > cpTTL) {
                            this.kb.removeContextPoint(p.getContextCoordinates());
                        }
                    } catch (SharkKBException ex) {
                        System.out.println(ex);
                    }
                   
                }
            }
        }
    }
    
}

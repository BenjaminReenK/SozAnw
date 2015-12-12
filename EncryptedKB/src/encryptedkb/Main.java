/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptedkb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author Benjamin Kneer
 */
public class Main {

    public static final String KBNAME = "encryptedKB";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, SharkKBException, FileNotFoundException, IOException {
        //AESEncrypt enc = new AESEncrypt();
        //enc.saveKeyToFile(PATH);
        //enc.loadKeyFromFile(PATH);
        AESEncrypt enc = new AESEncrypt(AESEncrypt.PATH);
        File in = new File(AESEncrypt.PATH + "in.txt");
        //File out = new File(PATH + "out.txt");
        //enc.encryptFile(in, out);
        //enc.encryptFile(in);
        //enc.decryptFile(in);
        //File decrypted = new File(PATH + "decrypted.txt");
        //enc.decryptFile(out, decrypted);
        
        //String s = "test encryption";
        //String dec = "";
        //byte[] encrypted;
        //encrypted = enc.encrypt(s);
        //dec = enc.decrypt(encrypted);
        //System.out.println(dec);
        
        EncFSSharkKB kb = new EncFSSharkKB(KBNAME);
        PeerSemanticTag pTag = kb.createPeerSemanticTag("Alice", "http://www.sharksystem.net/alice.html", "tcp://localhost:1234");
        SemanticTag st = kb.createSemanticTag("semantictag", "semantictagSI");
        ContextCoordinates cc = kb.createContextCoordinates(st, pTag, pTag, pTag, null, null, SharkCS.DIRECTION_INOUT);
        ContextPoint cp = kb.createContextPoint(cc);
        Knowledge k = kb.createKnowledge();
        k.addContextPoint(cp);
        
        
    }
    
}

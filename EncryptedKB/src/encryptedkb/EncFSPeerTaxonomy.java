/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptedkb;

import net.sharkfw.knowledgeBase.filesystem.FSPeerTaxonomy;
import net.sharkfw.knowledgeBase.inmemory.InMemoPeerSemanticNet;
import net.sharkfw.knowledgeBase.inmemory.InMemoPeerTaxonomy;

/**
 *
 * @author Benjamin Kneer
 */
public class EncFSPeerTaxonomy extends InMemoPeerTaxonomy{

   
    
     public EncFSPeerTaxonomy(String foldername, Encryption encryptor) {
        super(
            new InMemoPeerSemanticNet(
                new EncFSGenericTagStorage(foldername)
            )
        );
    }
    
}

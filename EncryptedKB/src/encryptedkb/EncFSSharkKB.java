/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptedkb;

import java.io.File;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.PeerTaxonomy;
import net.sharkfw.knowledgeBase.SemanticNet;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.filesystem.FSKnowledge;
import net.sharkfw.knowledgeBase.filesystem.FSSharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoPeerSemanticNet;
import net.sharkfw.knowledgeBase.inmemory.InMemoPeerTaxonomy;
import net.sharkfw.knowledgeBase.inmemory.InMemoSemanticNet;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoSpatialSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoTimeSTSet;
import net.sharkfw.system.L;

/**
 *
 * @author Benjamin Kneer
 */
public class EncFSSharkKB extends InMemoSharkKB implements SharkKB{

    private final String rootFolder;
    private EncFSKnowledge fsk;
    
    public static final String TOPIC_FOLDER = "/topics";
    public static final String PEERS_FOLDER = "/peers";
    public static final String LOCATIONS_FOLDER = "/locations";
    public static final String TIMES_FOLDER = "/times";
    public static final String KNOWLEDGE_FOLDER = "/knowledge";
    
    private static String chosenName = null;
   
    
    
    private static String uniqueFileName(String proposedName) {
        if(chosenName == null) {
            EncFSSharkKB.chosenName = EncFSSharkKB.emptyFilename(proposedName);
        }
        
        return EncFSSharkKB.chosenName;
    }
    
    public EncFSSharkKB(String rootFolder) throws SharkKBException {
        
        this(
        // topics
            new InMemoSemanticNet(
                new EncFSGenericTagStorage(rootFolder + TOPIC_FOLDER)),
        
        // peers
        new InMemoPeerTaxonomy(new InMemoPeerSemanticNet(
                        new EncFSGenericTagStorage(rootFolder + PEERS_FOLDER))
                        ),
        
        // locations
        new InMemoSpatialSTSet(
                new EncFSGenericTagStorage(rootFolder + LOCATIONS_FOLDER)),
        
        // times
        new InMemoTimeSTSet(
                new EncFSGenericTagStorage(rootFolder + TIMES_FOLDER)),
        
        
        // folder
        rootFolder
       
        );
     
    }
    
    public EncFSSharkKB(SemanticNet topics, PeerTaxonomy peers,
                 SpatialSTSet locations, TimeSTSet times, String rootFolder) 
            throws SharkKBException {
        
        super(topics, peers, locations, times);
        
        // this as knowledge background.
        String kFolderName = rootFolder + KNOWLEDGE_FOLDER;
        
        this.fsk = new EncFSKnowledge(this, kFolderName);
        this.fsk.refreshStatus();
        this.setKnowledge(fsk);
        
        this.rootFolder = rootFolder;
       
        
        EncFSPropertyHolder fsph = new EncFSPropertyHolder(rootFolder);
        fsph.restore();
        this.setPropertyHolder(fsph);
        this.refreshStatus();
    }
    
    private static void removeFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    EncFSSharkKB.removeFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
    
    /**
     * That methode removes directory and all its content.  Take care.
     * @param folderName 
     */
    public static void removeFSStorage(String folderName) {
        File folder = new File(folderName);
        
        try {
            EncFSSharkKB.removeFolder(folder);
        }
        catch(Exception e) {
            // ignore
            L.l("cannot remove folder: " + e.getMessage(), null);
        }
    }
    
    static String emptyFilename(String proposedName) {
        File propFolder = new File(proposedName);
        if(!propFolder.exists()) {
            return proposedName;
        }
        
        int n = 0;

        // remember canonical name
        String fName = proposedName;
        do {
            // try another name
            proposedName = fName + "_" + String.valueOf(n++);
            propFolder = new File(proposedName);

        } while(propFolder.exists());
        
        return proposedName;
    }
    
    public static EncFSPropertyHolder createFSPropertyHolder(String foldername) {
        // calculate empty file name
        foldername = emptyFilename(foldername);
        
        File propFolder = new File(foldername);

        // create folder
        propFolder.mkdirs();

        return new EncFSPropertyHolder(foldername);
    }
    
    @Override
    public ContextPoint createContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        ContextPoint cp = this.getContextPoint(coordinates);
        if(cp != null) {
            return cp;
        }
        
        return this.fsk.createContextPoint(coordinates);
    }
    
    public String getFoldername() {
        return this.rootFolder;
    }
    
}

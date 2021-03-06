/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptedkb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.filesystem.FSPropertyHolder;
import net.sharkfw.knowledgeBase.inmemory.InMemoPropertyHolder;
import net.sharkfw.system.L;

/**
 *
 * @author Benjamin Kneer
 */
public class EncFSPropertyHolder extends InMemoPropertyHolder{

    private final String folderName;
    private HashMap<String,String> systemProperties = null;
   
    
    public EncFSPropertyHolder(String folderName) {
        this.folderName = folderName;
        this.systemProperties = new HashMap<String,String>();
    }
 
    public boolean exists() {
        File folder = new File(this.getFolderName());
        return folder.exists();
    }
    
    private HashMap<String,String> getSystemProperties() {
        return this.systemProperties;
    }

    @Override
    public String getSystemProperty(String name) {
        return this.systemProperties.get(name);
    }
    
    public String getFolderName() {
        return this.folderName;
    }
    
    @Override
    public void setProperty(String name, String value, boolean transfer) {    
        super.setProperty(name, value, transfer);

        try {
            if(transfer) {
                this.persistProperties();
            } else {
                this.persistHiddenProperties();
            }
        }
        catch(FileNotFoundException fnfe) {
            L.e("couldn't open file to write (hidden) properties: " + fnfe, this);
        }
        catch(IOException fnfe) {
            L.e("couldn't open file to write (hidden) properties: " + fnfe, this);
        }
    }
    
    @Override
    public void setSystemProperty(String name, String value) {
        if(value == null) {
            this.systemProperties.remove(name);
        } else {
            this.systemProperties.put(name, value);
        }
        
        try {
            this.persistSystemProperties();
        }
        catch(FileNotFoundException fnfe) {
            L.w("couldn't open file to write system properties: " + fnfe, this);
        }
        catch(IOException fnfe) {
            L.w("couldn't open file to write system properties: " + fnfe, this);
        }
    }
    
    private String getSystemPropertyFilename() {
        return this.folderName + "/.sharkfw_st_systemProperties";
    }
    
    private String getHiddenPropertyFilename() {
        return this.folderName + "/.sharkfw_st_hiddenProperties";
    }
    
    private String getPropertyFilename() {
        return this.folderName + "/.sharkfw_st_properties";
    }
    
    private void persistSystemProperties() throws FileNotFoundException, IOException {
        String filename = this.getSystemPropertyFilename();
        EncFSPropertyHolder.persistToFile(this.getSystemProperties(), filename);
    }

    private void persistHiddenProperties() throws FileNotFoundException, IOException {
        String filename = this.getHiddenPropertyFilename();
        EncFSPropertyHolder.persistToFile(this.getHiddenProperties(), filename);
    }
    
    private void persistProperties() throws FileNotFoundException, IOException {
        String filename = this.getPropertyFilename();
        EncFSPropertyHolder.persistToFile(this.getUnhiddenProperties(), filename);
    }
    
    public static final String DELIMITER = ":\t";
    
    static void persistToFile(HashMap<String,String> properties, String filename) throws FileNotFoundException, IOException {
        if(properties == null || properties.size() < 1) { 
            // remove file
            File f = new File(filename);
            f.delete();
            return; 
        }
        
        // open File
        FileOutputStream fos = new FileOutputStream(filename);
        PrintStream ps = new PrintStream(fos);
        
        Iterator<String> nameIter = properties.keySet().iterator();
     
        while(nameIter.hasNext()) {
            String name = nameIter.next();
            
            String value = properties.get(name);

            // write properties
            ps.print(name);
            ps.print(DELIMITER);
            ps.println(value);
        }
        
        fos.close();
        ps.close();
        File f = new File(filename);
        Encryption e;
        try {
            e = new AESEncrypt(AESEncrypt.PATH);
            e.encryptFile(f);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(EncFSPropertyHolder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(EncFSPropertyHolder.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    static void restoreFromFile(HashMap<String,String> properties, String filename) 
            throws SharkKBException {
        
        try {
            
            File tmpOutput = null;
            // temporary decrypt the file
            if (new File(filename).exists()) {
                tmpOutput = new File(filename + "_tmp"); 
                Encryption e;
                try {
                    e = new AESEncrypt(AESEncrypt.PATH);
                    e.decryptFile(new File(filename), tmpOutput); 
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(EncFSPropertyHolder.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchPaddingException ex) {
                    Logger.getLogger(EncFSPropertyHolder.class.getName()).log(Level.SEVERE, null, ex);
                }
                 
            }
            
            // read from decrypted file
            FileReader fr = new FileReader(filename + "_tmp");
            BufferedReader reader = new BufferedReader(fr);
        
            String line = null;
            line = reader.readLine();
                    
            // there is something - clean up properties
            properties.clear();
            while(line != null) {
                // split into name value
                
                // StringTokenizer didn't work - made it by my own.
                
                String name;
                String value;
                int indexDelimiter = line.indexOf(DELIMITER);
                if(indexDelimiter >= 0 && indexDelimiter <= line.length()  ) {
                    name = line.substring(0, indexDelimiter);
                    value = line.substring(indexDelimiter + DELIMITER.length());
                   // store
                    properties.put(name, value);
                }
                
                // take next line
                line = reader.readLine();
                
            }
            reader.close();
            fr.close();
            
            // delete decrypted file
            if (tmpOutput.exists()) {
                tmpOutput.delete();
            }
        } catch (FileNotFoundException ex) {
            // ignore
        } 
        catch(IOException ioe) {
            throw new SharkKBException("cannot read from file: " + ioe.getMessage()); 
        }
    }
    
    public void persist() throws SharkKBException {
        try {
            this.persistHiddenProperties();
            this.persistProperties();
            this.persistSystemProperties();
        }
        catch(FileNotFoundException fnfe) {
            throw new SharkKBException("couldn't open file to write properties: " + fnfe.getMessage());
        }
        catch(IOException fnfe) {
            throw new SharkKBException("couldn't open file to write properties: " + fnfe.getMessage());
        }
    }
    
    public void restore() throws SharkKBException {
        EncFSPropertyHolder.restoreFromFile(this.getHiddenProperties(), this.getHiddenPropertyFilename());
        EncFSPropertyHolder.restoreFromFile(this.getUnhiddenProperties(), this.getPropertyFilename());
        EncFSPropertyHolder.restoreFromFile(this.getSystemProperties(), this.getSystemPropertyFilename());
    }

    void remove() {
        File f = new File(this.getHiddenPropertyFilename());
        f.delete();
        f = new File(this.getPropertyFilename());
        f.delete();
        f = new File(this.getSystemPropertyFilename());
        f.delete();
    }
    
}

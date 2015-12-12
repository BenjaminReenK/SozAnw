/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptedkb;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import java.io.File;
import java.io.OutputStream;

/**
 *
 * @author Benjamin Kneer
 */
public interface Encryption {
    
    public byte[] encrypt(String msg);
    public void encryptFile(File inFile, File outFile);
    public void encryptFile(File inFile);
    
    public String decrypt(byte[] in);
    public void decryptFile(File inFile, File outFile);
    public void decryptFile(File inFile);
}


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.*;
import javax.crypto.spec.*;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.filesystem.FSSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.pki.SharkPublicKeyStorage;
import net.sharkfw.security.key.SharkKeyGenerator;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.security.key.storage.filesystem.FSSharkKeyStorage;
import net.sharkfw.security.pki.*;
import net.sharkfw.security.pki.Certificate;
import net.sharkfw.security.pki.storage.SharkPkiStorage;
import net.sharkfw.security.pki.storage.filesystem.FSSharkPkiStorage;

/*
 * Für Keysize > 128
 * http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
 * *.jar nach jdk1.8.0_51\jre\lib\security kopieren
 */

/**
 *
 * @author Benni
 */
public class Cryptography {
    
    public static final int AES_KEY_SIZE = 256;
    public static final int RSA_KEY_SIZE = 1024;
    
    Cipher aesCipher;
    byte[] aesKey;
    SecretKeySpec aesKeySpec;
    
    J2SEAndroidSharkEngine se;
    FSSharkKB baseKB;
    SharkPkiStorage ks;
    SharkKeyGenerator sKeyGen;
    
    public Cryptography() throws GeneralSecurityException, SharkKBException {
        aesCipher = Cipher.getInstance("AES");
        
        se = new J2SEAndroidSharkEngine();
        baseKB = new FSSharkKB("testKB");
        sKeyGen = new SharkKeyGenerator(SharkKeyPairAlgorithm.RSA, RSA_KEY_SIZE);
        //SharkKeyGenerator keyBob = new SharkKeyGenerator(SharkKeyPairAlgorithm.RSA, RSA_KEY_SIZE);
        /*
        Date date = new Date();

        try {
            date.setTime(new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2100").getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        //System.out.println("public: " + sKeyGen.getPublicKey());
        //System.out.println("private: " + sKeyGen.getPrivateKey());
        PeerSemanticTag pTag = baseKB.createPeerSemanticTag("Alice", "http://www.sharksystem.net/alice.html", "");
        //PeerSemanticTag pTag1 = baseKB.createPeerSemanticTag("Bob", "http://www.sharksystem.net/bob.html", "");
        baseKB.setOwner(pTag);

        ks = new SharkPkiStorage(baseKB, pTag, sKeyGen.getPrivateKey());
        FSSharkPkiStorage fks = new FSSharkPkiStorage(Main.PATH + "pki.txt");


        //LinkedList<PeerSemanticTag> peerList = new LinkedList<>();
        //peerList.addFirst(pTag);
        //SharkCertificate certificate = new SharkCertificate(pTag1, pTag, peerList, Certificate.TrustLevel.FULL, keyBob.getPublicKey(), date);
        //ks.addSharkCertificate(certificate);
        fks.save(ks);

    }
    
    public void createAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE);
        SecretKey sKey = keyGen.generateKey();
        aesKey = sKey.getEncoded();
        aesKeySpec = new SecretKeySpec(aesKey, "AES");
    }
    
    public void encryptAES(File inFile, File outFile) {
               
        try {
            
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKeySpec);
            FileInputStream in = new FileInputStream(inFile);
            CipherOutputStream out = new CipherOutputStream(new FileOutputStream(outFile), aesCipher);
            copy(in, out);
            out.close();
            
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (InvalidKeyException ex) {
            System.out.println(ex.getMessage());
        }
        
    }
    
    public void decryptAES(File inFile, File outFile){
        
        try {
            
            aesCipher.init(Cipher.DECRYPT_MODE, aesKeySpec);
            CipherInputStream in = new CipherInputStream(new FileInputStream(inFile), aesCipher);
            FileOutputStream out = new FileOutputStream(outFile);
            copy(in, out);
            in.close();
            out.close();
            
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (InvalidKeyException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    
    private void copy(InputStream is, OutputStream os) throws IOException {
        int i;
        byte[] b = new byte[1024];
        while((i=is.read(b))!=-1) {
                os.write(b, 0, i);
        }
    }
}

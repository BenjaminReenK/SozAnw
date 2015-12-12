/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptedkb;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Benjamin Kneer
 */
public class AESEncrypt implements Encryption{

    public static final int AES_KEY_SIZE = 256;
    public static final String KEYFILENAME = "keyfile";
    public static final String PATH = "I:\\Win10\\Dropbox\\Studium\\WiSe 2015\\Soziale Anwendungen\\Cryptographie\\EncryptedKB\\";
    
    private Cipher aesCipher;
    private byte[] aesKey;
    private SecretKeySpec aesKeySpec;
    
    
    public AESEncrypt() throws NoSuchAlgorithmException, NoSuchPaddingException {
        
        aesCipher = Cipher.getInstance("AES");
        createAESKey();
    }
    
   
    public AESEncrypt(String PathToKeyFile) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException {
        aesCipher = Cipher.getInstance("AES");
        loadKeyFromFile(PathToKeyFile);
        
        
    }
    
    public void saveKeyToFile(String pathToFile) throws FileNotFoundException {
        FileOutputStream out = new FileOutputStream(pathToFile + KEYFILENAME);
        try {
            out.write(aesKey);
        } catch (IOException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public void loadKeyFromFile(String pathToFile) throws IOException {
        Path p = FileSystems.getDefault().getPath(pathToFile, KEYFILENAME);
        byte [] fileData = Files.readAllBytes(p);
        aesKey = fileData;
        aesKeySpec = new SecretKeySpec(aesKey, "AES");
    }
    
    private void createAESKey() throws NoSuchAlgorithmException {
        
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE);
        SecretKey sKey = keyGen.generateKey();
        aesKey = sKey.getEncoded();
        aesKeySpec = new SecretKeySpec(aesKey, "AES");
    }
    
    private void copy(InputStream is, OutputStream os) throws IOException {
        int i;
        byte[] b = new byte[1024*10];
        while((i=is.read(b))!=-1) {
                os.write(b, 0, i);
        }
    }
    
    @Override
    public void encryptFile(File inFile, File outFile) {
               
        try {
            
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKeySpec);
            FileInputStream in = new FileInputStream(inFile);
            CipherOutputStream out = new CipherOutputStream(new FileOutputStream(outFile), aesCipher);
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
    
    @Override
    public void decryptFile(File inFile, File outFile){
        
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
    
    @Override
    public void encryptFile(File inFile) {
        String pathOnly = inFile.getAbsolutePath();
        
        pathOnly = pathOnly.replace(inFile.getName(), "");
        File tmpOutput = new File(pathOnly + "tmpOutput.txt");
        encryptFile(inFile, tmpOutput);
        inFile.delete();
              
        try {
            tmpOutput.renameTo(inFile.getCanonicalFile());
        } catch (IOException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void decryptFile(File inFile) {
        String pathOnly = inFile.getAbsolutePath();
        
        pathOnly = pathOnly.replace(inFile.getName(), "");
        File tmpOutput = new File(pathOnly + "tmpOutput.txt");
        decryptFile(inFile, tmpOutput);
        inFile.delete();
              
        try {
            tmpOutput.renameTo(inFile.getCanonicalFile());
        } catch (IOException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public byte[] encrypt(String msg) {
        
        byte[] result = null;
        byte[] toEncrypt = msg.getBytes();
        
        try {
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKeySpec);
            result = aesCipher.doFinal(toEncrypt);
            
        } catch (InvalidKeyException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }

    @Override
    public String decrypt(byte[] in) {
        
        String result = null;
        byte decrypted[];
        
        try {
            aesCipher.init(Cipher.DECRYPT_MODE, aesKeySpec);
            decrypted = aesCipher.doFinal(in);
            result = new String(decrypted);
            
            
        } catch (InvalidKeyException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
    
    
}

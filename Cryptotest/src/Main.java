import net.sharkfw.knowledgeBase.SharkKBException;

import java.io.File;
import java.security.GeneralSecurityException;

/**
 * Created by Benni on 20.11.2015.
 */
public class Main {

    public static final String PATH = "I:\\Win10\\Dropbox\\Studium\\WiSe 2015\\Soziale Anwendungen\\Cryptographie\\Cryptotest\\src\\";

    public static void main(String[] args) throws GeneralSecurityException, SharkKBException {
        File in = new File(PATH + "test.txt");
        File out = new File(PATH + "out.txt");


        Cryptography crypto;
        crypto = new Cryptography();


        crypto.createAESKey();
        crypto.encryptAES(in, out);

        File decrypted = new File(PATH + "decrypted.txt");
        crypto.decryptAES(out, decrypted);
    }
}

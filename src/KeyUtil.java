import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Contains all the functionality/mechanism related to key store management. The keystore gets saved in a file
 * called keystore.bks in the root folder of this project.
 */

public class KeyUtil {
    char[] storePW;
    String storePath;
    String storePathTest = "keystoretest.bks";
    KeyStore ks;

    KeyUtil(String keyStorePath){
        this.storePath = keyStorePath;
    }

    public boolean fileCheck(){
        File f = new File(storePath);
        return f.isFile();
    }

    public void createKeyStore(String storePW) {
        this.storePW = storePW.toCharArray();

        try {
            ks = KeyStore.getInstance("BKS", "BC");
            ks.load(null, null);
            store();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void generateAndAddKey(String KeyPW, String alias) {
        try {
            // generating random bytes
            SecureRandom secureRandom = SecureRandom.getInstance("DEFAULT", "BC");
            byte[] keyBytes = new byte[16];
            secureRandom.nextBytes(keyBytes);

            // generating key
            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

            // adding key to keystore
            KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(key);
            KeyStore.ProtectionParameter protection = new KeyStore.PasswordProtection(KeyPW.toCharArray());
            ks.setEntry(alias, entry, protection);

        } catch (Exception e) { e.printStackTrace(); }
    }

    public void store() {
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(storePath);
            ks.store(fOut, storePW);
            fOut.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void deleteKeyStore(){
        File f = new File(storePath);
        f.delete();
    }

    public KeyStore load(String storePW) {
        try {
            ks = KeyStore.getInstance("BKS", "BC");
            char[] pw = storePW.toCharArray();
            this.storePW = pw;
            FileInputStream fis = new FileInputStream(storePath);
            ks.load(fis, pw);
            fis.close();
        }
        catch (Exception e) {e.printStackTrace();}
        return ks;
    }

    public void printContent() throws KeyStoreException {
        System.out.println("Number of keys in store: " + ks.size());
        System.out.println("Key aliase: ");
        for (Iterator<String> i = ks.aliases().asIterator(); i.hasNext(); ) {
            System.out.println(i.next());
        }
    }

    public boolean checkStorePW(String storePW) {
        if (storePW.length()<1){
            return  false;
        }
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("BKS", "BC");
            char[] pw = storePW.toCharArray();
            FileInputStream fis = new FileInputStream(storePath);
            try {
                ks.load(fis, pw);
            } catch (Exception e) {
                if (e instanceof IOException){
                    return false;
                }
                else { e.printStackTrace();}
            }
            fis.close();
        }
        catch (Exception e) {e.printStackTrace();}
        return true;
    }

    public String getStorePW(){
        return Arrays.toString(storePW);
    }

    public SecretKeySpec getKey(String alias, String pw) {
        SecretKeySpec key = null;
        try {
            key = (SecretKeySpec) ks.getKey(alias, pw.toCharArray());
        } catch (UnrecoverableKeyException e) { AlertBox.display("Wrong Password", "Wrong password");}catch (Exception e) { e.printStackTrace(); }
        return key;
    }

    public void deleteKey(String alias){
        try {
            ks.deleteEntry(alias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public boolean checkKeyPW (String alias, String pw){
        try{
            ks.getKey(alias, pw.toCharArray());
        } catch (Exception e) {
            if(e instanceof UnrecoverableKeyException) {
                return false;}
            else e.printStackTrace();
        }
        return true;
    }

    public int size(){
        int size = 0;
        try {
            size = ks.size();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return size;
    }

    public Enumeration<String> getKeyAliases() throws KeyStoreException {
    return ks.aliases();
    }
}

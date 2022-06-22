import org.bouncycastle.util.encoders.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 This class contains all the functionality related to cryptography.
 **/
public class CryptoUtil {

    String algorithm;
    byte[] keyBytes = Hex.decode("000102030405060708090a0b0c0d0e0f");
    byte[] iv;
    int ivLength;

    /**
     * This constructor only generates a random IV, which is used for adding randomness to the encryption.
     */

    CryptoUtil() {
        SecureRandom secureRandom = null;
        try {
            secureRandom = SecureRandom.getInstance("DEFAULT", "BC");
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        iv = new byte[16];
        assert secureRandom != null;
        secureRandom.nextBytes(iv);
        ivLength = iv.length;
    }

    /**
     * This method encrypts a file and stores it. It appends a hash to the plaintext before encrypting
     * to ensure integrity.
     */

    public void encryptFile(String filepath, String algorithm, SecretKeySpec key) {
        this.algorithm = algorithm.toUpperCase();
        try {
            // reading plaintext file
            byte[] input = FileUtil.readAllBytes(filepath);

            // computing hash value of plaintext
            MessageDigest digest = MessageDigest.getInstance("SHA-256", "BC");
            byte[] hashValue = digest.digest(input);

            // Appending hash value to input
            byte[] inputWithHash = new byte[input.length + hashValue.length];
            System.arraycopy(input, 0, inputWithHash, 0, input.length);
            System.arraycopy(hashValue, 0, inputWithHash, input.length, hashValue.length);

            // encrypting input::hashvalue
            Cipher cipher = Cipher.getInstance(algorithm + "/CBC/PKCS5Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] ciphertext = cipher.doFinal(inputWithHash);
            byte[] ciphertext_iv = new byte[ciphertext.length + iv.length];
            System.arraycopy(ciphertext, 0, ciphertext_iv, 0, ciphertext.length);
            System.arraycopy(iv, 0, ciphertext_iv, ciphertext.length, iv.length);

            // writing
            FileUtil.write(filepath + "." + algorithm, ciphertext_iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method decrypts a file. It also computes a hash from the plaintext it gets after decryption. If this hash
     * is not the same as the appended hash value, it returns "FileChanged".
     */

    public String decryptFile(String filepath, String algorithm, SecretKeySpec key, boolean test) {

        this.algorithm = algorithm.toUpperCase();
        try {

            // Reading
            byte[] input = FileUtil.readAllBytes(filepath);

            //Tampering test
            if(test) input[1] = 10;

            byte[] iv = Arrays.copyOfRange(input, input.length - ivLength, input.length);
            byte[] ciphertext = Arrays.copyOfRange(input, 0, input.length - ivLength);

            // Decrypting
            Cipher cipher = Cipher.getInstance(algorithm + "/CBC/PKCS5Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] output = cipher.doFinal(ciphertext);

            //Splitting the hash value from the plaintext
            byte[] hashValue = Arrays.copyOfRange(output, output.length - 32, output.length);
            byte[] plainText = Arrays.copyOfRange(output, 0, output.length - 32);

            // Checking the integrity before writing
            if(verifySHA256(hashValue, plainText)) {
                FileUtil.write(filepath.substring(0, filepath.length() - algorithm.length()) + "test", output);
                return "FileGood";
            }
            else return "FileChanged";
        } catch (BadPaddingException e) {return "WrongKey";} catch (Exception e) { e.printStackTrace();}
        return "WrongKey";
    }

    /**
     * This method compares the original append hashvalue with the computed hashvalue of the decrypted plaintext.
     */

    boolean verifySHA256(byte[] hashvalue, byte[] plaintext){
        try {
            // Verifying hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256", "BC");
            byte[] computedHashValue = digest.digest(plaintext);

            //for testing
            System.out.println("Original hash:  " + Hex.toHexString(hashvalue));
            System.out.println("Decrypted hash: " + Hex.toHexString(computedHashValue));

            if (MessageDigest.isEqual(computedHashValue, hashvalue)) {
                System.out.println("Hash values are equal");
                return true;
            }
            else {
                System.out.println("Hash values are not equal");
                return false;
            }
        }
        catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}

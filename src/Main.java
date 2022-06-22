import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.crypto.spec.SecretKeySpec;
import java.io.File;

/**
 * This is the main class that is run to run the program.
 *
 * In the following I will explain the different file paths and how/where to change them here in the Main class.
 * However, it should run fine without changing them as they are only related to this specific project folder.
 *
 * First the program will run 2 tests to check if the hashing works as it should. The location for the test file
 * that is used for these tests are located here: "files/test files/Very confidential file_testing.pdf". This
 * path can be changed by changing the variable "testFile" in this class.
 *
 * The test as well as the program generates a keystore which is called "keystoretest.bks" for the test
 * and "keystore.bks" for the program. Both of these files are by default stored in the root folder of this
 * project. This path can be changed by changing the variables "testKeystorePath" and "keystorePath" in
 * this class.
 *
 * When choosing which file to encrypt in the program, the program opens up the directory "files" by default.
 * This can be changed by changing the variable "fileDir" in this class.
 *
 */

public class Main extends Application {
    String testFile = "files/test files/Very confidential file_testing.pdf";
    String testKeystorePath = "keystoretest.bks";
    String fileDir = "files";
    String keystorePath = "keystore.bks";
    Stage window;
    Scene cryptoScene;
    CryptoUtil cryptoUtil;
    KeyUtil keyUtil;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        test1();
        test2();
        window = primaryStage;
        window.setTitle("File encrypter by Magnus Johannsen");
        cryptoUtil = new CryptoUtil();
        keyUtil = new KeyUtil(keystorePath);

        if(keyUtil.fileCheck()) {
            LoginView loginView = new LoginView(keyUtil);
            loginView.show();
            if(loginView.isLoggedIn) initCryptoView();
        }

        if(!keyUtil.fileCheck()){
            RegisterView registerView = new RegisterView(keyUtil);
            registerView.show();
            if (registerView.isRegistered) initCryptoView();
        }
    }

    public void initCryptoView() {
        CryptoView cryptoView = new CryptoView(cryptoUtil, keyUtil, fileDir);
        cryptoScene = new Scene(cryptoView, 500, 70);
        window.setScene(cryptoScene);
        window.setOnCloseRequest(e -> {
            cryptoView.close();
        });
        window.show();
    }

    /**
     Test case 1: Test that checks if hash gets rejected when the encrypted file is tampered.
     The results are printed in the terminal. The files that are used for testing are found in "files/test files". The
     keystore used for the test is called "keystoretest.bks" and gets stored in the root folder of this project.
     **/

    public void test1(){
        System.out.println("Test case 1: Test that checks if hash gets verified when encrypted file is not tampered");
        CryptoUtil ct_test = new CryptoUtil();
        KeyUtil kt_test = new KeyUtil(testKeystorePath);
        kt_test.createKeyStore("storepassword");
        kt_test.generateAndAddKey("keypassword", "key");
        SecretKeySpec testKey = kt_test.getKey("key", "keypassword");
        ct_test.encryptFile(testFile, "AES", testKey);
        ct_test.decryptFile(testFile + ".AES", "AES", testKey, true);
        new File(testFile + ".AES").delete();
    }

    /**
     Test case 2: Test that checks if hash gets verified when the encrypted file is not tampered with.
     The results are printed in the terminal. The files that are used for testing are found in "files/test files". The
     keystore used for the test is called "keystoretest.bks" and gets stored in the root folder of this project.
     **/

    public void test2(){
        System.out.println("Test case 2: Test if hash gets verified when encrypted file is not tampered");
        CryptoUtil ct_test = new CryptoUtil();
        KeyUtil kt_test = new KeyUtil(testKeystorePath);
        kt_test.createKeyStore("storepassword");
        kt_test.generateAndAddKey("keypassword", "key");
        SecretKeySpec testKey = kt_test.getKey("key", "keypassword");
        ct_test.encryptFile(testFile, "AES", testKey);
        ct_test.decryptFile(testFile + ".AES", "AES", testKey, false);
        new File(testFile + ".AES").delete();
        new File(testFile + ".test").delete();
    }
}


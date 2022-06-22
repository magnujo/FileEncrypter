import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Main view of the program containing all the buttons for the cryptographic functions and key management and
 * the functions they call when pressed.
 */

public class CryptoView extends HBox {
    CryptoUtil cryptoUtil;
    String dir;
    FileChooser fileChooser;
    KeyUtil keyUtil;
    KeysBox keysBox;
    CreateKeyBox createKeyBox;

    /**
     * Constructor setting up all the buttons and some object initialization.
     */

    public CryptoView(CryptoUtil cryptoUtil, KeyUtil keyUtil, String fileDirectory) {
        this.dir = fileDirectory;
        this.cryptoUtil = cryptoUtil;
        this.keyUtil = keyUtil;
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(dir));
        keysBox = new KeysBox(keyUtil);
        createKeyBox = new CreateKeyBox();

        setSpacing(10);
        getChildren().add(new EncryptButton());
        getChildren().add(new DecryptButton());
        getChildren().add(new CreateKeyButton());
        getChildren().add(new DeleteKeyButton());
        setAlignment(Pos.CENTER);
    }

    //The if statements in the following methods are there to make sure there are no weird behavior of the
    //program when closing using the X button.
    void createKey(){
        createKeyBox.display("New Key", "Enter password for new key");
        if (!createKeyBox.isClosed() && createKeyBox.getPW().length() > 8) {
            keyUtil.generateAndAddKey(createKeyBox.getPW(), createKeyBox.getAlias());
            AlertBox.display("Success!", "Key was created successfully");
        }
        else if (createKeyBox.buttonpressed){
            AlertBox.display("Error", "Password must be more than 8 characters long");
        }
    }

    void deleteKey(){
        if (keyUtil.size() > 0){
            keysBox.display("Delete", "Select key to delete", true);
            if (!keysBox.isClosed() && keysBox.isPWCorrect) {
                AlertBox.display("Success!", "Key was deleted successfully");
                }
            }
        else AlertBox.display("Error", "No keys were found");
        }

    void encrypt(){
        if (keyUtil.size() > 0){
            keysBox.display("Select key", "Select key", false);
            if (!keysBox.isClosed() && keysBox.isPWCorrect) {
                File selectedFile = null;
                selectedFile = fileChooser.showOpenDialog(new Stage());
                if (selectedFile != null) {
                    cryptoUtil.encryptFile(selectedFile.getPath(), "AES", keysBox.getKey());
                    AlertBox.display("Success!", "File was encrypted successfully");
                }
            }
        }
        else AlertBox.display("Error", "Please create key before encrypting");
    }

    void decrypt(){
        if (keyUtil.size() > 0){
            keysBox.display("Select key", "Select key", false);
            if (!keysBox.isClosed()) {
                File selectedFile = null;
                selectedFile = fileChooser.showOpenDialog(new Stage());
                if (selectedFile != null) {
                    String res = cryptoUtil.decryptFile(selectedFile.getPath(), "AES", keysBox.getKey(), false);
                    if (res.equalsIgnoreCase("WrongKey")) AlertBox.display("Wrong Key", "Failed to decrypt. Try using another key");
                    else if(res.equalsIgnoreCase("FileChanged")) AlertBox.display("Warning", "WARNING: Decryption failed: File has been changed by an untrusted party");
                    else AlertBox.display("Success", "Decryption was successful");
                }
            }
        }
        else AlertBox.display("Error", "Please create key before encrypting");
    }

    void close(){
        if(keyUtil.storePW != null){
            keyUtil.store();
        }
    }
}

class CreateKeyButton extends Button {
    CreateKeyButton(){
        setText("Create Key");
        setOnAction(e -> {
            CryptoView cv = (CryptoView) getParent();
            cv.createKey();
        });
    }
}

class DeleteKeyButton extends Button {
    DeleteKeyButton(){
        setText("Delete Key");
        setOnAction(e -> {
            CryptoView cv = (CryptoView) getParent();
            cv.deleteKey();
        });
    }
}

class EncryptButton extends Button {
    EncryptButton() {
        setText("Encrypt File");
        setOnAction(e -> {
            CryptoView cv = (CryptoView) getParent();
            cv.encrypt();
        });
    }
} // LoadButton

class DecryptButton extends Button {
    DecryptButton() {
        setText("Decrypt File");
        setOnAction(e -> {
            CryptoView cv = (CryptoView) getParent();
            cv.decrypt();
        });
    }
}

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.crypto.spec.SecretKeySpec;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Iterator;

public class KeysBox {
    Stage window;
    KeyUtil keyUtil;
    boolean closed;
    SecretKeySpec key;
    boolean buttonPressed;
    PWBox pwBox;
    boolean isPWCorrect;

    KeysBox (KeyUtil keyUtil){

        this.keyUtil = keyUtil;
        pwBox = new PWBox();
    }

    /**
     * Opens a separate window that prompts the user to select a key for encryption or deletion.
     * General idea and some code in this method is from
     * https://www.youtube.com/watch?v=SpL3EToqaXA&list=PL6gx4Cwl9DGBzfXLWLSYVy8EbTdpGbUIG&index=5
     */

    public void display(String title, String message, boolean deleteFunction){
        buttonPressed = false;
        closed = false;
        window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        ArrayList<Button> items = new ArrayList<>();

        if (keyUtil.size() > 0) {
            if(deleteFunction) { //for deleting key
                try {
                    for (Iterator<String> i = keyUtil.getKeyAliases().asIterator(); i.hasNext(); ) {
                        String alias = i.next();
                        Button b = new Button(alias);
                        b.setOnAction(e -> {
                            pwBox.display("Enter password", "Enter password for key: " + alias);
                            if (!pwBox.isClosed()) {
                                buttonPressed = true;
                                String pw = pwBox.getPW();
                                isPWCorrect = keyUtil.checkKeyPW(alias, pw);

                                if (isPWCorrect) {
                                    keyUtil.deleteKey(alias);
                                }
                                else {
                                    AlertBox.display("Wrong Password", "Wrong Password");
                                    window.close();
                                }
                            }
                            if (pwBox.buttonPressed) window.close();
                        });
                        items.add(b);
                    }
                }catch (KeyStoreException e) {e.printStackTrace();}
            }

            else{ //for selecting key
                try {
                    for (Iterator<String> i = keyUtil.getKeyAliases().asIterator(); i.hasNext(); ) {
                        String alias = i.next();
                        Button b = new Button(alias);
                        b.setOnAction(e -> {

                            pwBox.display("Enter password", "Enter password for key: " + alias);
                            if (!pwBox.isClosed()) {
                                buttonPressed = true;
                                String pw = pwBox.getPW();
                                isPWCorrect = keyUtil.checkKeyPW(alias, pw);

                                if (isPWCorrect) {
                                    key = keyUtil.getKey(alias, pw);
                                }
                                else {
                                    AlertBox.display("Wrong Password", "Wrong Password");
                                    window.close();
                                }
                            }

                            if (pwBox.buttonPressed) window.close();
                        });
                        items.add(b);
                    }
                }catch (KeyStoreException e) {e.printStackTrace();}
            }
        }

        Label label = new Label(message);
        VBox layout = new VBox();
        layout.getChildren().add(label);
        layout.getChildren().addAll(items);
        Scene scene = new Scene(layout);

        window.setScene(scene);
        window.showAndWait();
        if (!buttonPressed) closed = true;
    }

    public boolean isClosed(){
        return closed;
    }
    public SecretKeySpec getKey(){return key;}
}


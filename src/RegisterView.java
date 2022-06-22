import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * The view where the user registers. Is only shown if the keystore file is not found when the program loads, otherwise
 * LoginView is shown.
 */

public class RegisterView extends VBox {
    KeyUtil keyUtil;
    PasswordField registerPWField;
    boolean isRegistered;
    Stage registerStage;
    Scene registerScene;

    RegisterView(KeyUtil keyUtil){
        this.keyUtil = keyUtil;
        registerPWField = new PasswordField();
        registerPWField.setAlignment(Pos.CENTER);
        registerPWField.setMaxWidth(100);

        Label registerLabel = new Label("Please enter a master password to use for this application");
        registerLabel.setAlignment(Pos.CENTER);
        registerLabel.setTextAlignment(TextAlignment.CENTER);

        getChildren().add(registerLabel);
        getChildren().add(registerPWField);
        getChildren().add(new RegisterButton());

        setSpacing(10);
        setAlignment(Pos.CENTER);

    }

    public void show(){
        registerStage = new Stage();
        registerScene = new Scene(this, 350, 100);
        registerStage.setScene(registerScene);
        registerStage.showAndWait();
    }

    public void close(){
        registerStage.close();
    }

    public void register(){
        if (registerPWField.getText().length() <= 8) {
            AlertBox.display("Password error", "Password must be more than 8 characters long");
            isRegistered = false;
        }

        else {
            keyUtil.createKeyStore(registerPWField.getText());
            isRegistered = true;
            close();
        }
    }
}

class RegisterButton extends Button {
    RegisterButton() {
        setText("Register");
        setOnAction(e -> {
            RegisterView rv = (RegisterView) getParent();
            rv.register();
        });
    }
}

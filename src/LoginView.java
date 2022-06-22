import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * The view where the user logs in. Is only shown if the keystore file is found when the program loads, otherwise
 * RegisterView is shown.
 */

public class LoginView extends VBox {
    KeyUtil keyUtil;
    PasswordField loginPWField;
    boolean isRegistered;
    Stage loginStage;
    Scene loginScene;
    boolean isLoggedIn;

    LoginView(KeyUtil keyUtil){
        this.keyUtil = keyUtil;
        loginPWField = new PasswordField();
        loginPWField.setAlignment(Pos.CENTER);
        loginPWField.setMaxWidth(100);
        isLoggedIn = false;

        Label loginLabel = new Label("Login");
        loginLabel.setAlignment(Pos.CENTER);
        loginLabel.setTextAlignment(TextAlignment.CENTER);

        getChildren().add(loginLabel);
        getChildren().add(loginPWField);
        getChildren().add(new LoginButton());

        setSpacing(10);
        setAlignment(Pos.CENTER);
    }

    public void show(){
        loginStage = new Stage();
        loginScene = new Scene(this, 150, 100);
        loginStage.setScene(loginScene);
        loginStage.showAndWait();
    }

    public void close(){
        loginStage.close();
    }

    public void login(){
        String pwInput = loginPWField.getText();
        if(keyUtil.checkStorePW(pwInput)){
            keyUtil.load(pwInput);
            close();
            isLoggedIn = true;
        }
        else AlertBox.display("Warning!", "Wrong Password, try again");
    }
}

class LoginButton extends Button {
    LoginButton() {
        setText("Login");
        setOnAction(e -> {
            LoginView lv = (LoginView) getParent();
            lv.login();
        });
    }
}

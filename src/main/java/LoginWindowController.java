import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class LoginWindowController {
    public Button lbutton, rbutton;
    public PasswordField rPassword1, rPassword2, lPassword;
    public TextField rMail, lLogin, rLogin;
    public Text register,incorrect;
    public void pushRegisterButton(ActionEvent event) throws IOException {
        register();
    }
    private void register() throws IOException {
        Message message = new Message();
        boolean succes = TransmissionManager.register((message.registerMessage(new User(rMail.getText(),rPassword1.getText(),rPassword2.getText(),rLogin.getText()))));
        if(succes)
        {
            register.setText("Correct registration");
        }
        else
        {
            //textu
            register.setText("Check your passwords textfields or validity of emial");
        }
        register.setVisible(true);
    }

    ////////////////////////LOGOWANIE/////////////////
    public void pushlButton(ActionEvent event) throws IOException {
        login();
    }
    public void login() throws IOException {
        Message message = new Message();
        boolean succes =TransmissionManager.login((message.loginMessage(new User(lLogin.getText(),lPassword.getText()))));
        System.out.println(succes);
        if(succes)
        {
            FXMLLoader loader = new FXMLLoader((getClass().getResource("/CommunicationWindowController.fxml")));
            Main.stage.setScene(new Scene(loader.load(),900,600));
            CommunicationWindowController communicationWindowController = loader.getController();
            communicationWindowController.configurationCommunicationWindowController(lLogin.getText());
            Main.stage.show();
            CommunicationWindowController.setMe(lLogin.getText());
            communicationWindowController.loadFriends();
            communicationWindowController.serverListiner();
            TransmissionManager.setWaitForRespond(false);
            Video.setCommunicationWindowController(communicationWindowController);
            Audio.setCommunicationWindowController(communicationWindowController);
        }
    }
}

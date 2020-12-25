
import com.sun.mail.iap.ByteArray;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.net.util.SSLSocketUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Main extends Application {
    static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.stage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/LoginWindowController.fxml"));
        Main.stage.setTitle("J-AVC");
        Main.stage.initStyle(StageStyle.UNDECORATED);
        Main.stage.setScene(new Scene(root, 600, 400));
        Main.stage.setResizable(false);
        Main.stage.show();
    }

    public static void main(String[] args) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        launch(args);

    }
}

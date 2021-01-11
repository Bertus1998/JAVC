package JAVC;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main extends Application {

    private static Stage stage;

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        Main.stage = stage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.setStage(primaryStage);
        Parent root = FXMLLoader.load(getClass().getResource("/LoginWindowController.fxml"));
        Main.getStage().setTitle("J-AVC");
        Main.getStage().initStyle(StageStyle.UNDECORATED);
        Main.getStage().setScene(new Scene(root, 600, 400));
        Main.getStage().setResizable(false);
        Main.getStage().show();
    }

    public static void main(String[] args) throws Exception {
       launch(args);
     /*   String elo = "elo";

        byte[] b = new byte[1000];
        byte[] a = elo.getBytes();
        System.out.println(a.toString());
        ByteBuffer byteBuffer = ByteBuffer.wrap(b);
        byteBuffer.put(a);
        System.out.println(byteBuffer.array().length);
        for(int i =0;i<byteBuffer.array().length; i ++)
        {
            System.out.println(i+". "+byteBuffer.array()[i]);
        }
       // byteBuffer.put(dupa.getBytes());
        System.out.println(byteBuffer.array().toString());
        byte [] napis =Arrays.copyOfRange(byteBuffer.array(),0,3);
        for(int i =0;i<napis.length; i ++)
        {
            System.out.println(i+". "+napis[i]);
            System.out.println(i+". "+a[i]);
        }
        System.out.println(new String(napis, StandardCharsets.UTF_8));
        System.out.println(new String(a, StandardCharsets.UTF_8));
       byte [] test = new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
               1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
               1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
               1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
               1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
               1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,};
        byte[] encrypted =JAVC.EncryptionManager.encrypt(test);
        byte[] decrypted = JAVC.EncryptionManager.decrypt(encrypted);
        for(int i =0; i<test.length;i++)
        {
            System.out.println("TEST : "+test[i]);
        }
        for(int i =0; i<encrypted.length;i++)
        {
            System.out.println("ENCRYPTED : "+encrypted +" " + i);
        }
        for(int i =0; i<decrypted.length;i++)
        {
            System.out.println("DECRYPTED " +decrypted[i]);
        }
    */

    }
}

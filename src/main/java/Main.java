
import com.sun.mail.iap.ByteArray;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/*
 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String message = reader.readLine();
            if(message.substring(0,3).equals("Call"))
            {
                int port = Integer.getInteger(message.substring(4,7));
                InetAddress inetAddress = client.getInetAddress();
                String nazwa = message.substring(8,message.length()-1);
                //Szablon wysyłany do klienta osoby do której dzwonimy, dwie wiadomości
                //CallPORTIPADDRESS
                //User****
                InetAddress receiver =   inetTreeMap.get(nazwa);
                String messageToTarget1 = message + inetAddress.toString();
                String messageToTarget2 = "User" + nameTreeMap.get(inetAddress);
                //wysyłanie danych
                Socket client2 = new Socket(receiver,port);
                OutputStream outputStream = client2.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(outputStream);
                BufferedWriter bw = new BufferedWriter(osw);
                bw.write(messageToTarget1);
                bw.flush();
                bw.write(messageToTarget2);
                bw.flush();
                //Odbieranie danych od
                InputStream inputStream1 = client2.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream1);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                System.out.println("Send completely...");
                System.out.println("Wait for client...");
                message = bufferedReader.readLine();
                String port2 = message.substring(4,7);
                String ipAdress = client2.getInetAddress().toString();
                //Wysyłanie danych do osoby dzwoniącej
                String messageToCaller = "Recall" + port2 + ipAdress ;
                OutputStream outputStream1 = client.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream1);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                bufferedWriter.write(messageToCaller);
                bufferedWriter.flush();
 */
public class Main extends Application {
    static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.stage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/LoginWindowController.fxml"));
        Main.stage.setTitle("J-AVC");

        Main.stage.setScene(new Scene(root, 600, 400));
        Main.stage.setResizable(false);
        Main.stage.show();
    }
    public static void main(String[] args) {
        launch(args);
//        byte[] temp = new byt
    }
}

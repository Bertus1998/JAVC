import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.sound.sampled.LineUnavailableException;
import javax.transaction.Transactional;
import javax.xml.crypto.Data;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Optional;

public class TransmissionManager {
    static CommunicationWindowController communicationWindowController;

    public static CommunicationWindowController getCommunicationWindowController() {
        return communicationWindowController;
    }

    public static void setCommunicationWindowController(CommunicationWindowController communicationWindowController) {
        TransmissionManager.communicationWindowController = communicationWindowController;
    }

    static InetAddress severAddress;

    static {
        try {
            severAddress = InetAddress.getByName("192.168.1.3");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    static Socket client;

    static {
        try {
            client = new Socket(severAddress,5003);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    public static Socket getClient() {
        return client;
    }

    public static void setClient(Socket client) {
        TransmissionManager.client = client;
    }

    public static boolean login(String message) throws IOException{
        if (client==null) {
            client = new Socket(severAddress,5003);
        }
            TransmissionManager.sendMessageToServer(client,message);
            String receivedMessage = TransmissionManager.getMessageFromServer(client);
            if (receivedMessage.equals("LOGIN ACCEPT")) {
                return true;
            }


        return false;
    }

    public static boolean register(String message) throws IOException {
        TransmissionManager.sendMessageToServer(client,message);
        System.out.println(message);
        String receivedMessage =  getMessageFromServer(client);
        System.out.println(receivedMessage);
        if(receivedMessage.equals("REGISTER ACCEPT"))
        {
           return  true;
        }
        return false;
    }
    public static  void sendMessageToServer(Socket client, String message) throws IOException{

        System.out.println("Wiadomość wysłana do serwera :"+message);
        OutputStream outputStream = client.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeUTF(message);
        dataOutputStream.flush();
    }
    public static String getMessageFromServer(Socket client) throws IOException {

        
           InputStream inputStream = client.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            String message = dataInputStream.readUTF();
            return message;

    }
    public  static void callToFriend(String friend, String me) throws IOException, LineUnavailableException {
        Message message = new Message();
        TransmissionManager.sendMessageToServer(TransmissionManager.getClient(), message.callMessage(friend, me));


    }
    public static void stopTransmission()
    {
        Audio.setTransmission(false);
        Video.setTransmission(false);
        Audio.resetAudio();
        Video.getWebcam().close();
        Platform.runLater(()->{ TransmissionManager.communicationWindowController.getRimg().setImage(SwingFXUtils.toFXImage(new BufferedImage(
                communicationWindowController.getRimg().fitWidthProperty().intValue(),
                communicationWindowController.getRimg().fitWidthProperty().intValue(),
                BufferedImage.TYPE_INT_RGB),null));
            TransmissionManager.communicationWindowController.getTimg().setImage(SwingFXUtils.toFXImage(new BufferedImage(
                    communicationWindowController.getTimg().fitWidthProperty().intValue(),
                    communicationWindowController.getTimg().fitWidthProperty().intValue(),
                    BufferedImage.TYPE_INT_RGB),null));});
    }
    public static void startTransmission(String []message,boolean caller) throws IOException, LineUnavailableException {
      // 1 videoreceive/ 2 audiooreceive /3 audiotransmit /4 video transmit // 5 socket

        ServerSocket serwerSocketVideoReceive;
        Socket socketReceive;
        Socket socketTransmit;
        int portReceive,portTransmit;
        InetAddress inetAddress =InetAddress.getByName(message[4]);
        if(caller)
        {
            portReceive = Integer.parseInt(message[1]);
            portTransmit = Integer.parseInt(message[2]);
            ServerSocket serwerSocketReceive = new ServerSocket(Integer.parseInt(message[0]));
            socketReceive =  serwerSocketReceive.accept();
            socketTransmit = new Socket(inetAddress,Integer.parseInt(message[3]));

        }
        else
        {
            portReceive = Integer.parseInt(message[2]);
            portTransmit = Integer.parseInt(message[1]);
            socketTransmit = new Socket(inetAddress,Integer.parseInt(message[0]));
            serwerSocketVideoReceive = new ServerSocket(Integer.parseInt(message[3]));
            socketReceive = serwerSocketVideoReceive.accept();

        }
        Video.configureWebcam();
        Audio.configureAudioSend(10000,inetAddress, portTransmit);
        Audio.configureAudioReceive(10000,portReceive);

        sendData(socketTransmit);
        getData(socketReceive, portReceive);
    }
    public static ArrayList<Integer> checkPorts(int [] ports)
    {
        ArrayList<Integer> portToReturn= new ArrayList<>();
        for(int i =0 ;i<ports.length;i++)
        {
            if(isPortAvailable(ports[i]))
            {
                portToReturn.add(ports[i]);
            }
        }
        return portToReturn;
    }
    public static boolean isPortAvailable(int port) {
        try (var ss = new ServerSocket(port); var ds = new DatagramSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    public static void getData(Socket socket,int port) throws LineUnavailableException {
       Runnable runnableVideo = () -> {
            while(true) {
                try {

                    if(Video.isTransmission())
                    {
                        Video.getWebcam().open();
                        Video.receiveAndShowImageFromWebcam(socket);
                    }
                    else
                    {
                        Video.getWebcam().close();
                        socket.close();
                        break;
                    }
                } catch (IOException ioException) {
                stopTransmission();
                    ioException.printStackTrace();
                }
            }
        };
        Runnable runnableAudio = () -> {
                try {

                    Audio.receiveAndStreamToLouder(port);
                }
                catch (IOException ioException) {
                   stopTransmission();
                    ioException.printStackTrace();
                }

        };
        Thread threadGetAudio = new Thread(runnableAudio);
        Thread threadGetVideo = new Thread(runnableVideo);
        threadGetAudio.start();
        threadGetVideo.start();

    }
    public static void sendData(Socket socket)
    {
            Runnable runnableVideo = () -> {
                DataConverter.configureDataConverter();
                while(true) {
                    try {

                        if(Video.isTransmission())
                        {
                            Video.captureAndSendFromWebcam(socket);

                        }
                        else
                        {
                            Video.getWebcam().close();
                            socket.close();
                            break;
                        }
                    } catch (IOException ioException) {
                        stopTransmission();
                        ioException.printStackTrace();
                    }
                }

            };
        Runnable runnableAudio = () -> {
                try {
                    Audio.captureAndSendFromMicro();
                } catch (IOException ioException) {
                    stopTransmission();
                    ioException.printStackTrace();
                }

        };
        Thread threadSendAudio= new Thread(runnableAudio);
        Thread threadSendVideo = new Thread(runnableVideo);
        threadSendAudio.start();
        threadSendVideo.start();

    }
    public static void messageExecutor(String messega) throws IOException, LineUnavailableException {
        System.out.println("Wiadomość odebrana od serwera :" +messega);
        String[] arrayOfPartsMessage = messega.split(" ");
        if(arrayOfPartsMessage.length>0)
        {
        switch (arrayOfPartsMessage[0]) {
            case "CALLACCEPT": {
                callAcceptExecutor(arrayOfPartsMessage);
                break;
            }
            case "REQUESTFRIEND": {
                requestFriendExecutor(arrayOfPartsMessage);
                break;
            }
            case "ACCEPTFRIEND": {
                acceptFriendExecutor(arrayOfPartsMessage);
                break;
            }
            case "NOACTIVEFRIENDS": {
                    noActiveFriendsExecutor(arrayOfPartsMessage);
                break;
            }
            case "DELETEFRIEND": {
                 deleteFriendExecutor(arrayOfPartsMessage);
                break;
            }
            case "LOGOUT": {
                logoutExecutor(arrayOfPartsMessage);
                break;
            }
            case "LISTDELETEFRIENDS":
            {
                listDeleteFriendsExecutor(arrayOfPartsMessage);
                break;
            }
            case "ACTIVEFRIENDS": {
                loadFriendsExecutor(arrayOfPartsMessage);
                break;
            }
            case "RESPONDCALL":{
                respondCallExecutor(arrayOfPartsMessage);
                break;
            }
            case "CHANGESTATUS":{
                changeStatusExecutor(arrayOfPartsMessage);
                break;
            }
            case "UPLOAD":{
                uploadAudioExecutor(arrayOfPartsMessage);
                break;
            }
        }
        }
    }

    private static void uploadAudioExecutor(String[] arrayOfPartsMessage) throws LineUnavailableException {
        if(communicationWindowController.choosenFriend.getText().equals(arrayOfPartsMessage[1])) {
            Audio.sourceDataLine.close();
            int a = Integer.parseInt(arrayOfPartsMessage[2]);
            System.out.println(a);
            Audio.reconfigureAudioReceive(a);
        }
    }

    private static void changeStatusExecutor(String[] arrayOfPartsMessage) throws IOException {
        communicationWindowController.loadFriends();
    }

    private static void respondCallExecutor(String[] finalArrayOfmessage) {
        Platform.runLater(()->{ Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Call dialog");
            alert.setHeaderText(finalArrayOfmessage[2] + " call!");
            alert.setContentText("Do you want answer?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                communicationWindowController.choosenFriend.setText(finalArrayOfmessage[2]);
                Message messageAccept = new Message();
                int []ports = new int[4];
                try {
                    int counter =0;
                    for(int i =5;i<finalArrayOfmessage.length;i++)
                    {
                        System.out.println(finalArrayOfmessage[i]);
                        if(TransmissionManager.isPortAvailable(Integer.parseInt(finalArrayOfmessage[i])))
                        {
                            ports[counter] =Integer.parseInt(finalArrayOfmessage[i]);
                            counter++;
                            if(counter == 4)
                            {break;}
                        }
                    }
                    System.out.println(finalArrayOfmessage.toString());
                    String [] messegaeToStartTransmission= new String[5];
                    for(int i =0;i<4;i++)
                    {
                        messegaeToStartTransmission[i] = String.valueOf(ports[i]);
                        System.out.println(ports[i]);
                    }
                    messegaeToStartTransmission[4] = finalArrayOfmessage[finalArrayOfmessage.length-1];
                    TransmissionManager.sendMessageToServer(TransmissionManager.getClient(),messageAccept.callAcceptMessage(finalArrayOfmessage[2],CommunicationWindowController.getMe(),ports));
                    TransmissionManager.startTransmission(messegaeToStartTransmission,false);
                } catch (IOException | LineUnavailableException ioException) {
                    ioException.printStackTrace();
                }

            } else {
                try {
                    TransmissionManager.sendMessageToServer(TransmissionManager.getClient(), "RESPONDCALL NOPE");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    private static void loadFriendsExecutor(String[] message) {
        if (message.length> 1) {
            String [] groupOfFriends = new String[message.length-1];
            for(int i =1 ;i<message.length;i++) {
                groupOfFriends[i-1] = message[i];
                System.out.println(groupOfFriends[i-1]);
            }
            System.out.println(groupOfFriends.length);

            Platform.runLater(() -> {
                int k = 0;
                ObservableList<Node> childrens = communicationWindowController.gridPaneFriend.getChildren();
                communicationWindowController.gridPaneFriend.getChildren().removeAll(childrens);
                for (int i = 0; i < groupOfFriends.length; i = i + 2) {
                    communicationWindowController.gridPaneFriend.add(new Label(groupOfFriends[i]), 0, k);
                    if (groupOfFriends[i + 1].equals("false")) {

                        communicationWindowController.gridPaneFriend.add(new Circle(10, Color.RED), 1, k);
                    } else {
                        communicationWindowController.gridPaneFriend.add(new Circle(10, Color.GREEN), 1, k);
                    }
                    k++;
                }

            });
        }
        else
        {
            Platform.runLater(()->{
                ObservableList<Node> childrens = communicationWindowController.gridPaneFriend.getChildren();
                    communicationWindowController.gridPaneFriend.getChildren().removeAll(childrens);

            });
        }
    }

    private static void listDeleteFriendsExecutor(String[] arrayOfFriendsAndStatus)  {

        Message message = new Message();
        String[] arrayOfFriends = new String[(arrayOfFriendsAndStatus.length - 1) / 2];
        for (int i = 0; i < arrayOfFriends.length; i++) {
            System.out.println(arrayOfFriendsAndStatus[i]);
            arrayOfFriends[(i)] = arrayOfFriendsAndStatus[i * 2 + 1];
        }
        String defaultText = "List of your friends";
        Platform.runLater(()->{ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(defaultText, arrayOfFriends);
            {
                Optional<String> result = choiceDialog.showAndWait();
                if (result.isPresent() && !result.get().equals(defaultText)) {
                    try {
                        TransmissionManager.sendMessageToServer(TransmissionManager.getClient(), message.deleteFriendMessage(result.get(), CommunicationWindowController.getMe()));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }

            }});

    }


    private static void callAcceptExecutor(String[] arrayOfReceivedMessage) throws IOException, LineUnavailableException {
        if(arrayOfReceivedMessage[1].equals("ACCEPT")) {
            String[] portsAndHostName = new String [5];
            for(int i = 4;i<arrayOfReceivedMessage.length;i++)
            {
                portsAndHostName[i-4] = arrayOfReceivedMessage[i];
            }
            startTransmission(portsAndHostName,true);
        }
        else
        {
            Platform.runLater(()->{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Communicat");
                alert.setHeaderText("Communicat");
                alert.setContentText("On nie chce z tobą gadać!");
                alert.showAndWait();
            });

        }
    }

    private static void requestFriendExecutor(String[] friends) {
        Platform.runLater(()->{if (friends[1].equals("ACCEPT")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Communicat");
            alert.setHeaderText("Communicat");
            alert.setContentText("Request was send to user or user was added ");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Communicat");
            alert.setHeaderText("Communicat");
            alert.setContentText("There is no user");
            alert.showAndWait();
        }});

        }


    private static void acceptFriendExecutor(String[] message) throws IOException {
        Platform.runLater(()->
        {
            if (message[1].equals("ACCEPT")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Communicat");
                alert.setHeaderText("Communicat");
                alert.setContentText(message[2]+ " was added!");
                alert.showAndWait();

            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Communicat");
                alert.setHeaderText("Communicat");
                alert.setContentText(message[2]+ "was not added!");
                alert.showAndWait();
            }
        });
        communicationWindowController.loadFriends();


    }

    private static void noActiveFriendsExecutor(String[] friends) throws IOException {
        Platform.runLater(()->{
        if (friends.length >1) {
            Message message = new Message();
            String defaultText = "List of friend, choose one to add him to your friend list";

            String[] AllOfFriends = new String[friends.length-1];
            for(int i =1;i<friends.length;i++)
            {
                AllOfFriends[i-1]= friends[i];
            }
         ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(defaultText,AllOfFriends);
            Optional<String> result = choiceDialog.showAndWait();
            if (result.isPresent() && !defaultText.equals(result.get())) {
                try {
                    TransmissionManager.sendMessageToServer(TransmissionManager.getClient(), message.acceptFriendMessage(result.get(), CommunicationWindowController.getMe()));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Communicat");
            alert.setHeaderText("Communicat");
            alert.setContentText("There is no request from other users");
            alert.showAndWait();
        }
        });

    }

    private static void logoutExecutor(String[] tempString) throws IOException {
        Message message = new Message();
        if(tempString[1].equals("ACCEPT"))
        {

            TransmissionManager.sendMessageToServer(TransmissionManager.getClient(), message.logOutMessage(CommunicationWindowController.getMe()));
        }
        System.exit(0);
    }

    private static void deleteFriendExecutor(String[] message) throws IOException {
        Platform.runLater(()->{
            if (message[1].equals("ACCEPT")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Communicat");
            alert.setHeaderText("Communicat");
            alert.setContentText(message[2] + " was deleted!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Communicat");
            alert.setHeaderText("Communicat");
            alert.setContentText("ERROR");
            alert.showAndWait();
        }});
        communicationWindowController.loadFriends();
        }

    }





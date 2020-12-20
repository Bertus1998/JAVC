import javafx.scene.control.Alert;

import javax.sound.sampled.LineUnavailableException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TransmissionManager {
   static InetAddress severAddress;
   static boolean waitForRespond;

    public static boolean isWaitForRespond() {
        return waitForRespond;
    }

    public static void setWaitForRespond(boolean waitForRespond) {
        System.out.println("RESPOND" + waitForRespond);
        TransmissionManager.waitForRespond = waitForRespond;
    }

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

    public static boolean login(String message) throws IOException {
        if (client==null) {
            client = new Socket(severAddress,5003);
        }
            TransmissionManager.sendMessageToServer(client,message);
            String receivedMessage = TransmissionManager.getMessageFromServer(client);
            if (receivedMessage.equals("Y")) {
                return true;
            }


        return false;
    }

    public static boolean register(String message) throws IOException {
        TransmissionManager.sendMessageToServer(client,message);
        System.out.println(message);
        String receivedMessage =  getMessageFromServer(client);
        System.out.println(receivedMessage);
        if(receivedMessage.equals("Y"))
        {
           return  true;
        }
        return false;
    }
    public static  void sendMessageToServer(Socket client, String message) throws IOException {
        TransmissionManager.setWaitForRespond(true);
        OutputStream outputStream = client.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeUTF(message);
        dataOutputStream.flush();
    }
    public static String getMessageFromServer(Socket client) throws IOException {


            if (isWaitForRespond()) {
                client.setSoTimeout(0);
            } else {
                client.setSoTimeout(10);
            }
            InputStream inputStream = client.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            String message = dataInputStream.readUTF();
            return message;

    }
    public  static void callToFriend(String friend, String me) throws IOException, InterruptedException, LineUnavailableException {
        Message message = new Message();
        TransmissionManager.sendMessageToServer(TransmissionManager.getClient(), message.callMessage(friend, me));
        TransmissionManager.setWaitForRespond(true);
        String receivedMessage = TransmissionManager.getMessageFromServer(TransmissionManager.getClient());
        TransmissionManager.setWaitForRespond(false);
        String[] arrayOfReceivedMessage = receivedMessage.split(" ");
        if(arrayOfReceivedMessage[0].equals("CALLACCEPT")) {
            String[] portsAndHostName = new String [5];
            for(int i = 3;i<arrayOfReceivedMessage.length;i++)
            {
                portsAndHostName[i-3] = arrayOfReceivedMessage[i];
            }
        startTransmission(portsAndHostName,true);

        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Communicat");
            alert.setHeaderText("Communicat");
            alert.setContentText("On nie chce z tobą gadać!");
            alert.showAndWait();
        }
    }
    public static void startTransmission(String []message,boolean caller) throws IOException, LineUnavailableException {
      // 1 videoreceive/ 2 aidoreceive /3 audiotransmit /4 video transmit // 5 socket

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
        Audio.configureAudioReceive(10000);
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
                    Video.getWebcam().open();
                    Video.receiveAndShowImageFromWebcam(socket);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        };
        Runnable runnableAudio = () -> {
                try {

                    Audio.receiveAndStreamToLouder(port);
                }
                catch (IOException | LineUnavailableException ioException) {
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
                        Video.captureAndSendFromWebcam(socket);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            };
        Runnable runnableAudio = () -> {
                try {
                    Audio.captureAndSendFromMicro();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

        };
        Thread threadSendAudio= new Thread(runnableAudio);
        Thread threadSendVideo = new Thread(runnableVideo);
        threadSendAudio.start();
        threadSendVideo.start();

    }

}

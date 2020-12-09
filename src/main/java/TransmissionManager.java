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
            severAddress = InetAddress.getByName("192.168.0.101");
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
        if(caller)
        {
            ServerSocket serwerSocketReceive = new ServerSocket(Integer.parseInt(message[0]));
            socketReceive =  serwerSocketReceive.accept();
            socketTransmit = new Socket(InetAddress.getByName(message[4]),Integer.parseInt(message[3]));

        }
        else
        {
            socketTransmit = new Socket(InetAddress.getByName(message[4]),Integer.parseInt(message[0]));
            serwerSocketVideoReceive = new ServerSocket(Integer.parseInt(message[3]));
            socketReceive = serwerSocketVideoReceive.accept();

        }
        Video.configureWebcam();
        Audio.configureAudio();
        sendData(socketTransmit);
        getData(socketReceive);
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
    public static void sendPacket(BufferedImage bufferedImage, String port, InetAddress adress)
    {
        final int sizeOfBiggestPacket = 50*1024;
        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            DatagramPacket datagramPacket;
            byte[] arrayOfBytes = DataConverter.convertImageToByte(bufferedImage,1);
            byte[] sizeOfImage = Integer.toString(arrayOfBytes.length).getBytes();
            for(int i = 0;i<10;i++)
            {
                datagramSocket.send(new DatagramPacket(sizeOfImage,4,0,adress,Integer.parseInt(port)));
            }
            for(int i = 0;i<(arrayOfBytes.length/sizeOfBiggestPacket);i++) {
                int lengthOfMessage;
                if((i+1)*sizeOfBiggestPacket<arrayOfBytes.length)
                {
                    lengthOfMessage = sizeOfBiggestPacket;
                }
                else
                {
                    lengthOfMessage = arrayOfBytes.length-i*sizeOfBiggestPacket;
                }
                datagramPacket = new DatagramPacket(arrayOfBytes, i*sizeOfBiggestPacket,lengthOfMessage);
                datagramSocket.send(datagramPacket);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    public static byte[] getPacket(DatagramSocket datagramSocket,int size, boolean status) throws IOException {

        if(status)
        {
            byte [] arrayOfBytes = new byte[size];
        DatagramPacket datagramPacket= new DatagramPacket(arrayOfBytes,size);
        datagramSocket.receive(datagramPacket);
        return datagramPacket.getData();
        }
        else
        {
            byte [] arrayOfBytes = new byte[size];
            final int sizeOfBiggestPacket = 50*1024;
            byte [] partOfArray;
            DatagramPacket datagramPacket;
            for(int i = 0;i*sizeOfBiggestPacket<size ;i++)
            {
                int length;
                if(i+1*sizeOfBiggestPacket>size)
                {
                    length = size - i*sizeOfBiggestPacket;
                }
                else
                {
                    length = sizeOfBiggestPacket;
                }
                partOfArray = new byte[length];
                datagramPacket= new DatagramPacket(partOfArray,i*sizeOfBiggestPacket,length);
                datagramSocket.receive(datagramPacket);
                partOfArray = datagramPacket.getData();
                System.arraycopy(partOfArray,0,arrayOfBytes,i*50*1024,length);
            }
            return arrayOfBytes;
        }

        }
    public static void getData(Socket socket) throws LineUnavailableException {


        Runnable runnable = () -> {
            while(true) {
                try {
                    Video.getWebcam().open();
                    Video.receiveAndShowImageFromWebcam(socket);
                    Audio.receiveAndStreamToLouder(socket);
                } catch (IOException | LineUnavailableException ioException) {
                    ioException.printStackTrace();
                }
            }
        };
        Thread t = new Thread(runnable);
        t.start();

    }
    public static void sendData(Socket socket)
    {

            Runnable runnable = () -> {
                while(true) {
                    try {
                        Video.captureAndSendFromWebcam(socket);
                        Audio.captureAndSendFromMicro(socket);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    } catch (InterruptedException | LineUnavailableException e) {
                        e.printStackTrace();
                    }
                }
            };
            Thread t = new Thread(runnable);
            t.start();

    }

}

import org.apache.commons.io.output.ChunkedOutputStream;

import javax.sound.sampled.*;
import javax.xml.crypto.Data;
import javax.xml.transform.Source;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Audio {
    public static AudioFormat audioFormatToSend;
    public static AudioFormat audioFormatToReceive;
    public static TargetDataLine targetDataLine;
    public static SourceDataLine sourceDataLine;
    public static int sizeToSend, sizeToReceive;
    public static  CommunicationWindowController communicationWindowController;
    public  static  byte[] dataToSend ;
    public static  byte[] dataToReceive ;
    public static DatagramPacket datagramPacketToSend;
    public static DatagramPacket datagramPacketToReceive;
    public static InetAddress inetAddressTemp;
    public static int portTemp;
    public static boolean transmission = false;

    public static boolean isTransmission() {
        return transmission;
    }

    public static void setTransmission(boolean transmission) {
        Audio.transmission = transmission;
    }

    public static CommunicationWindowController getCommunicationWindowController() {
        return communicationWindowController;
    }

    public static void setCommunicationWindowController(CommunicationWindowController communicationWindowController) {
        Audio.communicationWindowController = communicationWindowController;
    }

    public static void captureAndSendFromMicro() throws IOException {

        int numBytesRead;

        DatagramSocket datagramSocket = new DatagramSocket();
        int bytesRead = 0;


        while(true) {
            System.out.println(transmission);
            if(transmission) {
                numBytesRead = targetDataLine.read(dataToSend, 0, sizeToSend);
                bytesRead += numBytesRead;
                System.out.println("Send1");
                if (bytesRead > targetDataLine.getBufferSize() / 5) {
                    datagramSocket.send(datagramPacketToSend);
                    System.out.println("Send");
                }
            }
            else
            {
                break;
            }

        }

    }

    public static void receiveAndStreamToLouder(int port) throws IOException{

        DatagramSocket datagramSocket = new DatagramSocket(port);
        byte[] checkArray = new byte[6];
        byte [] intArray = new byte[4];
        while(true)
        {   if(transmission) {
            datagramSocket.receive(datagramPacketToReceive);
            dataToReceive = datagramPacketToReceive.getData();
            for (int i = 0; i < 6; i++) {
                checkArray[i] = dataToReceive[i];
            }
            if (checkArray.toString().equals("Change")) {
                for (int i = 0; i < 4; i++) {
                    intArray[i] = dataToReceive[i + 6];
                    for (int j = 0; j < 100000; j++) {
                        System.out.println("WE DID IT!");
                    }
                }
            }
            System.out.println(dataToReceive.toString());
            sourceDataLine.write(dataToReceive, 0, datagramPacketToReceive.getData().length);
        }
        else
        {
            break;
        }

        }
      }
    public static void configureAudioSend(int sampleRate,InetAddress inetAddress,int port) throws LineUnavailableException {
        inetAddressTemp =inetAddress;
        portTemp = port;
        sizeToSend = (int)sampleRate/5;
        dataToSend = new byte[(int)sampleRate / 5];
        datagramPacketToSend = new DatagramPacket(dataToSend, dataToSend.length,inetAddress, port);
        audioFormatToSend = new AudioFormat(sampleRate, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormatToSend);
        targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
        targetDataLine.open(audioFormatToSend);
        targetDataLine.start();
    }
    public static void configureAudioReceive(int sampleRate) throws LineUnavailableException {
        setTransmission(true);
        sizeToReceive = (int)sampleRate/5;
        dataToReceive = new byte[(int)sampleRate / 5];
        datagramPacketToReceive = new DatagramPacket(dataToReceive,dataToReceive.length);
        audioFormatToReceive = new AudioFormat(sampleRate, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormatToReceive);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceDataLine.open(audioFormatToReceive);
        sourceDataLine.start();

    }
    public static void reconfigureAudioSend(int sampleRate) throws LineUnavailableException, IOException, InterruptedException {
        {
            DatagramSocket datagramSocket = new DatagramSocket();
            String string = "Change" + String.valueOf(sampleRate);
            byte [] array = new byte[dataToSend.length];
            datagramPacketToSend = new DatagramPacket(dataToSend, dataToSend.length,inetAddressTemp, portTemp);
            for(int i =0;i< string.getBytes().length ; i++) {
                array[i] = string.getBytes()[i];
            }
            datagramPacketToSend.setData(array);
            for(int i =0;i<100;i++) {
                datagramSocket.send(datagramPacketToSend);
                System.out.println(datagramPacketToSend.getData().length);
            }
            sizeToSend = (int)sampleRate/5;
            dataToSend = new byte[(int)sampleRate / 5];
            audioFormatToSend = new AudioFormat(sampleRate, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormatToSend);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(audioFormatToSend);
            targetDataLine.start();
        }}

}

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
    public static int portTempToSend;
    public static int portTempToReceive;
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
            if(transmission) {
                numBytesRead = targetDataLine.read(dataToSend, 0, sizeToSend);
                bytesRead += numBytesRead;
                if (bytesRead > targetDataLine.getBufferSize() / 5) {
                    datagramSocket.send(datagramPacketToSend);
                    System.out.println("WYSłane " +datagramPacketToSend.getData().length);
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

        while(true)
        {   if(transmission) {
            datagramSocket.receive(datagramPacketToReceive);
            dataToReceive = datagramPacketToReceive.getData();
            System.out.println("ODEBRANE : "+dataToReceive.length);
            sourceDataLine.write(dataToReceive, 0, sizeToReceive);
        }
        else
        {
            break;
        }

        }
      }
    public static void configureAudioSend(int sampleRate,InetAddress inetAddress,int port) throws LineUnavailableException {
        inetAddressTemp =inetAddress;
        portTempToSend = port;
        sizeToSend = (int)sampleRate/5;
        dataToSend = new byte[(int)sampleRate / 5];
        datagramPacketToSend = new DatagramPacket(dataToSend, dataToSend.length,inetAddress, port);
        audioFormatToSend = new AudioFormat(sampleRate, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormatToSend);
        targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
        targetDataLine.open(audioFormatToSend);
        targetDataLine.start();
    }
    public static void configureAudioReceive(int sampleRate, int port) throws LineUnavailableException {
        portTempToReceive = port;
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
            datagramPacketToSend = new DatagramPacket(dataToSend, dataToSend.length,inetAddressTemp, portTempToSend);
            sizeToSend = (int)sampleRate/5;
            dataToSend = new byte[(int)sampleRate / 5];
            audioFormatToSend = new AudioFormat(sampleRate, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormatToSend);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(audioFormatToSend);
            targetDataLine.start();
        }}
    public static void reconfigureAudioReceive(int sampleRate) throws LineUnavailableException {
        sizeToReceive=(int)sampleRate/5;
       dataToReceive = new byte[(int)sampleRate / 5];
        datagramPacketToReceive = new DatagramPacket(dataToReceive,dataToReceive.length,inetAddressTemp,portTempToReceive);
        audioFormatToReceive = new AudioFormat(sampleRate, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormatToReceive);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceDataLine.open(audioFormatToReceive);
        sourceDataLine.start();

    }
        public static void resetAudio()
        {
            targetDataLine.close();
            sourceDataLine.close();
            datagramPacketToSend = null;
            datagramPacketToReceive = null;
        }


}

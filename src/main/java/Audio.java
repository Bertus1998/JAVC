import org.apache.commons.io.output.ChunkedOutputStream;

import javax.sound.sampled.*;
import javax.xml.crypto.Data;
import javax.xml.transform.Source;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
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

    public static CommunicationWindowController getCommunicationWindowController() {
        return communicationWindowController;
    }

    public static void setCommunicationWindowController(CommunicationWindowController communicationWindowController) {
        Audio.communicationWindowController = communicationWindowController;
    }

    public static void captureAndSendFromMicro(InetAddress inetAddress, int port) throws IOException {
        int numBytesRead;
        System.out.println("Send");
        DatagramSocket datagramSocket = new DatagramSocket(port,inetAddress);
        int bytesRead = 0;


        while(true) {
            numBytesRead = targetDataLine.read(dataToSend, 0, sizeToSend);
            bytesRead += numBytesRead;

            if (bytesRead > targetDataLine.getBufferSize() / 5) {
                datagramSocket.send(datagramPacketToSend);
                System.out.println("Send");
            }

        }

    }

    public static void receiveAndStreamToLouder(int port) throws  IOException {

        DatagramSocket datagramSocket = new DatagramSocket(port);

        while(true)
        {
            datagramSocket.receive(datagramPacketToReceive);
            sourceDataLine.write(datagramPacketToReceive.getData(),0,datagramPacketToReceive.getData().length);

        }
      }
    public static void configureAudioSend(float sampleRate) throws LineUnavailableException {
        System.out.println("SEND" + sampleRate);
        sizeToSend = (int)sampleRate/5;
        dataToSend = new byte[(int)sampleRate / 5];
        datagramPacketToSend = new DatagramPacket(dataToSend, dataToSend.length);
        audioFormatToSend = new AudioFormat(sampleRate, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormatToSend);
        targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
        targetDataLine.open(audioFormatToSend);
        targetDataLine.start();
    }
    public static void configureAudioReceive(float sampleRate) throws LineUnavailableException {
        System.out.println("RECEIVE" + sampleRate);
        sizeToReceive = (int)sampleRate/5;
        dataToReceive = new byte[(int)sampleRate / 5];
        datagramPacketToReceive = new DatagramPacket(dataToReceive, dataToReceive.length);
        audioFormatToReceive = new AudioFormat(sampleRate, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormatToReceive);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceDataLine.open(audioFormatToReceive);
        sourceDataLine.start();

    }
}

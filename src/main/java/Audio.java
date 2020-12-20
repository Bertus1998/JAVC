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
    private static AudioFormat audioFormatToSend;
    private static AudioFormat audioFormatToReceive;
    private static TargetDataLine targetDataLine;
    private static SourceDataLine sourceDataLine;
    private  static int sizeToSend, sizeToReceive;
    private static  CommunicationWindowController communicationWindowController;
    private  static  byte[] dataToSend ;
    private  static  byte[] dataToReceive ;
    private static DatagramPacket datagramPacketToSend;
    private static DatagramPacket datagramPacketToReceive;

    public static CommunicationWindowController getCommunicationWindowController() {
        return communicationWindowController;
    }

    public static void setCommunicationWindowController(CommunicationWindowController communicationWindowController) {
        Audio.communicationWindowController = communicationWindowController;
    }

    public static void captureAndSendFromMicro(InetAddress inetAddress, int port) throws IOException {
        int numBytesRead;
        DatagramSocket datagramSocket = new DatagramSocket(port,inetAddress);
        int bytesRead = 0;
        targetDataLine.start();

        while(true) {
            numBytesRead = targetDataLine.read(dataToSend, 0, sizeToSend);
            bytesRead += numBytesRead;
            if (bytesRead > targetDataLine.getBufferSize() / 5) {
                datagramSocket.send(datagramPacketToSend);
            }

        }

    }

    public static void receiveAndStreamToLouder(int port) throws  IOException {

        DatagramSocket datagramSocket = new DatagramSocket(port);
        sourceDataLine.start();
        while(true)
        {
            datagramSocket.receive(datagramPacketToReceive);
            sourceDataLine.write(datagramPacketToReceive.getData(),0,datagramPacketToReceive.getData().length);
        }
      }
    public static void configureAudioSend(float sampleRate) throws LineUnavailableException {
        sizeToSend = (int)sampleRate/5;
        dataToSend = new byte[(int)sampleRate / 5];
        datagramPacketToSend = new DatagramPacket(dataToSend, dataToSend.length);
        audioFormatToSend = new AudioFormat(sampleRate, 16, 1, true, true);
        targetDataLine = AudioSystem.getTargetDataLine(audioFormatToSend);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormatToSend);
        targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
        targetDataLine.open(audioFormatToSend);
    }
    public static void configureAudioReceive(float sampleRate) throws LineUnavailableException {
        sizeToReceive = (int)sampleRate/5;
        dataToReceive = new byte[(int)sampleRate / 5];
        datagramPacketToReceive = new DatagramPacket(dataToReceive, dataToReceive.length);
        audioFormatToReceive = new AudioFormat(sampleRate, 16, 1, true, true);
        sourceDataLine = AudioSystem.getSourceDataLine(audioFormatToReceive);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormatToReceive);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceDataLine.open(audioFormatToReceive);
    }
}

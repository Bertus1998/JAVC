import javax.sound.sampled.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Audio {
    private static AudioFormat audioFormat;
    private static TargetDataLine targetDataLine;
    private static SourceDataLine sourceDataLine;


    public static void captureAndSendFromMicro(InetAddress inetAddress, int port) throws LineUnavailableException, IOException {
        byte[] transmitBufferedAudioBytes = new byte[targetDataLine.getBufferSize() / 5];
        DatagramSocket datagramSocket = new DatagramSocket();
          int numBytesRead;
        DatagramPacket datagramPacket;
        int CHUNK_SIZE = 1024;
        int bytesRead = 0;
        numBytesRead = targetDataLine.read(transmitBufferedAudioBytes, 0, CHUNK_SIZE);
        bytesRead += numBytesRead;
        if (bytesRead > targetDataLine.getBufferSize() / 5) {
            datagramPacket = new DatagramPacket(transmitBufferedAudioBytes,targetDataLine.getBufferSize() / 5,inetAddress,port);
            datagramSocket.send(datagramPacket);
            System.out.println("XD");
        }

    }

    public static void receiveAndStreamToLouder(int port) throws  IOException {
        byte[] transmitBufferedAudioBytes = new byte[targetDataLine.getBufferSize() / 5];
        DatagramPacket datagramPacket = new DatagramPacket(transmitBufferedAudioBytes,targetDataLine.getBufferSize() / 5);
        DatagramSocket datagramSocket = new DatagramSocket(port);
        datagramSocket.receive(datagramPacket);
        sourceDataLine.write(transmitBufferedAudioBytes, 0,targetDataLine.getBufferSize() / 5);
    }
    public static void configureAudio() throws LineUnavailableException {
        audioFormat = new AudioFormat(10000.0f, 16, 1, true, true);
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
        targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
        targetDataLine.open((audioFormat));
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();
        targetDataLine.start();
    }

}

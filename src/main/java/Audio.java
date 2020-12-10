import javax.sound.sampled.*;
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
    private static AudioFormat audioFormat;
    private static TargetDataLine targetDataLine;
    private static SourceDataLine sourceDataLine;


    public static void captureAndSendFromMicro(InetAddress inetAddress, int port) throws LineUnavailableException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int numBytesRead;
        DatagramSocket datagramSocket = new DatagramSocket();

        int CHUNK_SIZE = 1024;
        byte[] data = new byte[targetDataLine.getBufferSize() / 5];
        int bytesRead = 0;
        targetDataLine.start();
        while(true) {

            numBytesRead = targetDataLine.read(data, 0, CHUNK_SIZE);
            System.out.println("send" + Arrays.toString(data));
            bytesRead += numBytesRead;
            if (bytesRead > targetDataLine.getBufferSize() / 5) {
                System.out.println(numBytesRead);
                datagramSocket.send(new DatagramPacket(data,0,inetAddress,port));
            }
        }

    }

    public static void receiveAndStreamToLouder(int port) throws  IOException {
        byte[] transmitBufferedAudioBytes = new byte[targetDataLine.getBufferSize() / 5];
        DatagramPacket datagramPacket = new DatagramPacket(transmitBufferedAudioBytes,targetDataLine.getBufferSize() / 5);
        DatagramSocket datagramSocket = new DatagramSocket(port);
        sourceDataLine.start();
        while(true)
        {
            System.out.println("receive " + Arrays.toString(transmitBufferedAudioBytes));
            datagramSocket.receive(datagramPacket);
            sourceDataLine.write(transmitBufferedAudioBytes,0,transmitBufferedAudioBytes.length);

        }
      }
    public static void configureAudio() throws LineUnavailableException {
        audioFormat = new AudioFormat(8000.0f, 16, 1, true, true);
        targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        sourceDataLine.open(audioFormat);
        targetDataLine.open(audioFormat);

    }

}

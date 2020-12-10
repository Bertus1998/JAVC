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
        int numBytesRead;
        DatagramSocket datagramSocket = new DatagramSocket();

        int CHUNK_SIZE = 128;
        byte[] data = new byte[targetDataLine.getBufferSize() / 5];
        int bytesRead = 0;
        targetDataLine.start();
        while(true) {

            numBytesRead = targetDataLine.read(data, 0, CHUNK_SIZE);
            bytesRead += numBytesRead;

                datagramSocket.send(new DatagramPacket(data,targetDataLine.getBufferSize()/5,inetAddress,port));
            //    System.out.println("SEND : "+Arrays.toString(data));

        }

    }

    public static void receiveAndStreamToLouder(int port) throws  IOException {
        byte[] transmitBufferedAudioBytes = new byte[targetDataLine.getBufferSize() / 5];
        DatagramPacket datagramPacket = new DatagramPacket(transmitBufferedAudioBytes,targetDataLine.getBufferSize() / 5);
        DatagramSocket datagramSocket = new DatagramSocket(port);
        sourceDataLine.start();
        while(true)
        {
            datagramSocket.receive(datagramPacket);
        //    System.out.println("RECEIED : " + Arrays.toString(transmitBufferedAudioBytes));
            sourceDataLine.write(datagramPacket.getData(),0,datagramPacket.getData().length);
        }
      }
    public static void configureAudio() throws LineUnavailableException {
        audioFormat = new AudioFormat(40000.0f, 16, 1, true, true);
        targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        sourceDataLine.open(audioFormat);
        targetDataLine.open(audioFormat);

    }

}

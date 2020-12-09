import javax.sound.sampled.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Audio {
    private static AudioFormat audioFormat;
    private static TargetDataLine targetDataLine;
    private static SourceDataLine sourceDataLine;


    public static void captureAndSendFromMicro(Socket socket) throws LineUnavailableException, IOException {
        audioFormat = new AudioFormat(10000.0f, 16, 1, true, true);
        targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
        targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
        targetDataLine.open((audioFormat));
        byte[] transmitBufferedAudioBytes = new byte[targetDataLine.getBufferSize() / 5];
        int numBytesRead;
        int CHUNK_SIZE = 1024;
        int bytesRead = 0;
        numBytesRead = targetDataLine.read(transmitBufferedAudioBytes, 0, CHUNK_SIZE);
        bytesRead += numBytesRead;
        if (bytesRead > targetDataLine.getBufferSize() / 5) {
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            dOut.writeInt(transmitBufferedAudioBytes.length);
            dOut.write(transmitBufferedAudioBytes);
        }

    }

    public static void receiveAndStreamToLouder(Socket socket) throws LineUnavailableException, IOException {
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        int length = dataInputStream.readInt();
        byte[] message = new byte[length];
        dataInputStream.readFully(message,0,message.length);
        sourceDataLine.write(message, 0,message.length);
    }
}

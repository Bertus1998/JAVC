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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Audio {
    public static AudioFormat audioFormatToSend;
    public static AudioFormat audioFormatToReceive;
    public static TargetDataLine targetDataLine;
    public static SourceDataLine sourceDataLine;
    public static int sizeToSend, sizeToReceive;
    public static  CommunicationWindowController communicationWindowController;
    public  static  byte[] dataToSend ;
    public static byte[] throwToLouderData;
    public static  byte[] dataToReceive ;
    public static byte []captureDataFromMicroData;
    public static DatagramPacket datagramPacketToSend;
    public static DatagramPacket datagramPacketToReceive;
    public static InetAddress inetAddressTemp;
    public static int portTempToSend;
    public static int portTempToReceive;
    public static boolean transmission = false;
    public static ReentrantLock reentrantLock;

    public static boolean isTransmission() {
        return transmission;
    }
    public static boolean wait;

    public static void setTransmission(boolean transmission) {
        Audio.transmission = transmission;
    }

    public static CommunicationWindowController getCommunicationWindowController() {
        return communicationWindowController;
    }

    public static void setCommunicationWindowController(CommunicationWindowController communicationWindowController) {
        Audio.communicationWindowController = communicationWindowController;
    }

    public static void captureAndSendFromMicro() throws InterruptedException {

        int numBytesRead;

        DatagramSocket datagramSocket = null ;
        int bytesRead = 0;
        while(true) {
            if(wait)
            {
                Thread.sleep(2000);
                wait =false;
            }
            try {
                if (datagramSocket == null) {
                    datagramSocket = new DatagramSocket();
                    datagramSocket.setSoTimeout(1000);
                }
                if (transmission) {
                    if (datagramPacketToSend != null) {
                        numBytesRead = targetDataLine.read(captureDataFromMicroData, 0, captureDataFromMicroData.length);
                        bytesRead += numBytesRead;
                        if (bytesRead > targetDataLine.getBufferSize() / 5) {
                            System.out.println("Przed enkrypcją ROZMIAR :" +captureDataFromMicroData.length);
                           dataToSend= EncryptionManager.encrypt(captureDataFromMicroData);
                            System.out.println("WYSYL, ROZMIAR :" +dataToSend.length);
                            datagramSocket.send(datagramPacketToSend);
                        }
                    }
                } else {
                    break;
                }

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    public static void receiveAndStreamToLouder(int port) {

        DatagramSocket datagramSocket = null;

        while(true)
        {
            try {
                if (datagramSocket == null) {
                    datagramSocket = new DatagramSocket(port);
                    datagramSocket.setSoTimeout(1000);
                }
                if (transmission) {
                    if (datagramPacketToReceive != null) {
                        datagramSocket.receive(datagramPacketToReceive);
                        byte [] temp = datagramPacketToReceive.getData();
                        System.out.println("ODBIÓR, ROZMIAR :" + temp.length);
                       byte [] decrypted =  EncryptionManager.decrypt(temp);
                        System.out.println("PO DEKRYPCJI ROZMIAR :" + decrypted.length);
                       if(decrypted!=null) {
                           sourceDataLine.write(decrypted, 0, decrypted.length);
                       }
                    }
                } else {
                    break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
      }
    public static void configureAudioSend(int sampleRate,InetAddress inetAddress,int port) throws Exception {

        inetAddressTemp =inetAddress;
        portTempToSend = port;
        captureDataFromMicroData = new byte[sampleRate/5];
        sizeToSend = EncryptionManager.sizeOfEncrypted(sampleRate/5);
        dataToSend = new byte[sizeToSend];
        datagramPacketToSend = new DatagramPacket(dataToSend, dataToSend.length,inetAddress, port);
        audioFormatToSend = new AudioFormat(sampleRate, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormatToSend);
        targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
        targetDataLine.open(audioFormatToSend);
        targetDataLine.start();
    }
    public static void configureAudioReceive(int sampleRate, int port) throws Exception {

        sizeToReceive = EncryptionManager.sizeOfEncrypted(sampleRate/5);
        portTempToReceive = port;
        setTransmission(true);

        dataToReceive = new byte[sizeToReceive];
        datagramPacketToReceive = new DatagramPacket(dataToReceive,dataToReceive.length);
        audioFormatToReceive = new AudioFormat(sampleRate, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormatToReceive);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceDataLine.open(audioFormatToReceive);
        sourceDataLine.start();

    }
    public static void reconfigureAudioSend(int sampleRate) throws LineUnavailableException, IOException, InterruptedException {
        {
            reentrantLock = new ReentrantLock();
            Lock l = reentrantLock;
            l.lock();
            try {
            wait = true;
            targetDataLine.close();
            byte [] temp =new byte[(int)sampleRate/5];
            sizeToSend = EncryptionManager.encrypt(temp).length;
            dataToSend = new byte[sizeToSend];
            datagramPacketToSend=null;
            datagramPacketToSend = new DatagramPacket(dataToSend, dataToSend.length,inetAddressTemp, portTempToSend);
            audioFormatToSend = new AudioFormat(sampleRate, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormatToSend);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(audioFormatToSend);
            targetDataLine.start();
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                l.unlock();
            }

        }}
    public static void reconfigureAudioReceive(int sampleRate) throws LineUnavailableException {
        reentrantLock = new ReentrantLock();
        Lock lock = reentrantLock;
        lock.lock();
        try {
            sourceDataLine.close();
            byte [] temp =new byte[(int)sampleRate/5];
            sizeToReceive = EncryptionManager.encrypt(temp).length;
            dataToReceive = new byte[sizeToReceive];
            datagramPacketToReceive = null;
            datagramPacketToReceive = new DatagramPacket(dataToReceive, dataToReceive.length);
            audioFormatToReceive = new AudioFormat(sampleRate, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormatToReceive);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceDataLine.open(audioFormatToReceive);
            sourceDataLine.start();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
        public static void resetAudio()
        {
            targetDataLine.close();
            sourceDataLine.close();
            datagramPacketToSend = null;
            datagramPacketToReceive = null;
        }


}

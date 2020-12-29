import javax.sound.sampled.*;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Audio {
    private static AudioFormat audioFormatToSend;
    private static AudioFormat audioFormatToReceive;
    private static TargetDataLine targetDataLine;
    private static SourceDataLine sourceDataLine;
    public static int sizeToSend, sizeToReceive;
    public static  CommunicationWindowController communicationWindowController;
    public  static  byte[] dataToSend,dataBeforeEncryption ;
    public static  byte[] dataToReceive ;
    public static DatagramPacket datagramPacketToSend;
    public static DatagramPacket datagramPacketToReceive;
    private static InetAddress inetAddressTemp;
    public static int portTempToSend;
    public static int portTempToReceive;
    public static boolean transmission = false;
    public static ReentrantLock reentrantLock;

    public static boolean isTransmission() {
        return transmission;
    }
    private static boolean wait;

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
            if(isWait())
            {
                Thread.sleep(2000);
                setWait(false);
            }
            try {
                if (datagramSocket == null) {
                    datagramSocket = new DatagramSocket();
                    datagramSocket.setSoTimeout(1000);
                }
                if (transmission) {
                    if (datagramPacketToSend != null) {
                        numBytesRead = getTargetDataLine().read(dataBeforeEncryption, 0, dataBeforeEncryption.length);

                        bytesRead += numBytesRead;
                        if (bytesRead > getTargetDataLine().getBufferSize() / 5) {
                            datagramPacketToSend.setData(dataToSend,0,dataToSend.length);
                            System.out.println("Wysłano: " + dataToSend.length);
                            datagramSocket.send(datagramPacketToSend);
                            Thread.sleep(1000);
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
                        byte[] packet =datagramPacketToReceive.getData();
                        System.out.println("ODEBRANO: " + packet.length);
                        if(packet !=null){
                            try {
                                byte[] decrypted = EncryptionManager.decrypt(packet);
                                if(decrypted!=null) {
                                    getSourceDataLine().write(decrypted, 0, decrypted.length);
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                    }}
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
        dataBeforeEncryption =new byte[(int)sampleRate/5];
        setInetAddressTemp(inetAddress);
        portTempToSend = port;
        sizeToSend = EncryptionManager.encrypt(dataBeforeEncryption).length;
        dataToSend = new byte[sizeToSend];
        datagramPacketToSend = new DatagramPacket(dataToSend, dataToSend.length,inetAddress, port);
        setAudioFormatToSend(new AudioFormat(sampleRate, 16, 1, true, true));
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, getAudioFormatToSend());
        setTargetDataLine((TargetDataLine) AudioSystem.getLine(info));
        getTargetDataLine().open(getAudioFormatToSend());
        getTargetDataLine().start();
    }
    public static void configureAudioReceive(int sampleRate, int port) throws Exception {
        byte [] dataAfterDecryption = new byte[(int)sampleRate/5];
        sizeToReceive = EncryptionManager.encrypt(dataAfterDecryption).length;
        portTempToReceive = port;
        setTransmission(true);
        dataToReceive = new byte[sizeToReceive];
        datagramPacketToReceive = new DatagramPacket(dataToReceive,dataToReceive.length);
        setAudioFormatToReceive(new AudioFormat(sampleRate, 16, 1, true, true));
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, getAudioFormatToReceive());
        setSourceDataLine((SourceDataLine) AudioSystem.getLine(info));
        getSourceDataLine().open(getAudioFormatToReceive());
        getSourceDataLine().start();

    }
    public static void reconfigureAudioSend(int sampleRate) throws LineUnavailableException, IOException, InterruptedException {
        {
            reentrantLock = new ReentrantLock();
            Lock l = reentrantLock;
            l.lock();
            try {
            setWait(true);
            getTargetDataLine().close();
            byte [] temp =new byte[(int)sampleRate/5];
            sizeToSend = EncryptionManager.encrypt(temp).length;
            dataToSend = new byte[sizeToSend];
            datagramPacketToSend=null;
            datagramPacketToSend = new DatagramPacket(dataToSend, dataToSend.length, getInetAddressTemp(), portTempToSend);
            setAudioFormatToSend(new AudioFormat(sampleRate, 16, 1, true, true));
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, getAudioFormatToSend());
            setTargetDataLine((TargetDataLine) AudioSystem.getLine(info));
            getTargetDataLine().open(getAudioFormatToSend());
            getTargetDataLine().start();
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
            getSourceDataLine().close();
            byte [] temp =new byte[(int)sampleRate/5];
            sizeToReceive = EncryptionManager.encrypt(temp).length;
            dataToReceive = new byte[sizeToReceive];
            datagramPacketToReceive = null;
            datagramPacketToReceive = new DatagramPacket(dataToReceive, dataToReceive.length);
            setAudioFormatToReceive(new AudioFormat(sampleRate, 16, 1, true, true));
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, getAudioFormatToReceive());
            setSourceDataLine((SourceDataLine) AudioSystem.getLine(info));
            getSourceDataLine().open(getAudioFormatToReceive());
            getSourceDataLine().start();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
        public static void resetAudio()
        {
            getTargetDataLine().close();
            getSourceDataLine().close();
            datagramPacketToSend = null;
            datagramPacketToReceive = null;
        }


    private static AudioFormat getAudioFormatToSend() {
        return audioFormatToSend;
    }

    private static void setAudioFormatToSend(AudioFormat audioFormatToSend) {
        Audio.audioFormatToSend = audioFormatToSend;
    }

    private static AudioFormat getAudioFormatToReceive() {
        return audioFormatToReceive;
    }

    private static void setAudioFormatToReceive(AudioFormat audioFormatToReceive) {
        Audio.audioFormatToReceive = audioFormatToReceive;
    }

    private static TargetDataLine getTargetDataLine() {
        return targetDataLine;
    }

    private static void setTargetDataLine(TargetDataLine targetDataLine) {
        Audio.targetDataLine = targetDataLine;
    }

    private static SourceDataLine getSourceDataLine() {
        return sourceDataLine;
    }

    private static void setSourceDataLine(SourceDataLine sourceDataLine) {
        Audio.sourceDataLine = sourceDataLine;
    }

    private static InetAddress getInetAddressTemp() {
        return inetAddressTemp;
    }

    private static void setInetAddressTemp(InetAddress inetAddressTemp) {
        Audio.inetAddressTemp = inetAddressTemp;
    }

    private static boolean isWait() {
        return wait;
    }

    private static void setWait(boolean wait) {
        Audio.wait = wait;
    }
}

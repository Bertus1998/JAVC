import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Video {
    private static Webcam webcam;
    private static CommunicationWindowController communicationWindowController;
    private static boolean transmitingVideo = true;
    private static boolean receivingVideo = true;

    public static Webcam getWebcam() {
        return webcam;
    }

    public static void setWebcam(Webcam webcam) {
        Video.webcam = webcam;
    }

    public static CommunicationWindowController getCommunicationWindowController() {
        return communicationWindowController;
    }

    public static void setCommunicationWindowController(CommunicationWindowController communicationWindowController) {
        Video.communicationWindowController = communicationWindowController;
    }

    public static void captureAndSendFromWebcam(DatagramSocket datagramSocket,InetAddress address) {
        if (getCommunicationWindowController() != null) {
            getWebcam().open();

            Runnable runnableTranssmitingVideo = () -> {
                while (transmitingVideo) {

                     BufferedImage bufferedImage = getWebcam().getImage();
                    Image image= SwingFXUtils.toFXImage(bufferedImage, null);
                    TransmissionManager.sendPacket(bufferedImage,datagramSocket,address);
                    Platform.runLater(() -> {
                        communicationWindowController.timg.setImage(image);
                    });
                }
            };
            Thread thread = new Thread(runnableTranssmitingVideo);
            thread.start();

        }
    }
    public static void receiveAndShowImageFromWebcam(DatagramSocket datagramSocket)
    {
        if(getCommunicationWindowController()!=null)
        {

            Runnable runnableReceivingVideo = ()->
            {

                byte[] message ;
                byte [] arrayOfsize = new byte[4];
                int size = 8;
                while(receivingVideo)
                {
                    try {

                        message = TransmissionManager.getPacket(datagramSocket,size,true);
                        for(int i =0 ;i<4; i ++)
                        {
                            arrayOfsize[i]  = message[i];
                        }
                        if(arrayOfsize.toString().equals("size"))
                        {
                            for(int i =4 ;i< message.length; i++)
                            {
                                arrayOfsize[i-4] = message[i];
                            }
                            size = ByteBuffer.wrap(arrayOfsize).getInt();
                        }
                        else
                        {

                         byte [] image = TransmissionManager.getPacket(datagramSocket,size,false);
                            ByteArrayInputStream bis = new ByteArrayInputStream(image);
                            getCommunicationWindowController().getRimg().setImage(SwingFXUtils.toFXImage(ImageIO.read(bis),null));
                            size =8;
                        }

                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            };
            Thread thread = new Thread(runnableReceivingVideo);
            thread.start();
        }

    }
    public static boolean configureWebcam() {
        setWebcam(Webcam.getDefault());
        if (getWebcam() != null) {
            System.out.println("Webcam: " + getWebcam().getName());
            return true;
        } else {
            System.out.println("No webcam detected");
            return false;
        }
    }

    public static void getVideo(DatagramSocket datagramSocket)
    {
        receiveAndShowImageFromWebcam(datagramSocket);
    }
    public static void sendVideo(DatagramSocket datagramSocket, InetAddress address)
    {
        captureAndSendFromWebcam(datagramSocket,address);
    }

}

import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
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

    public static void captureAndSendFromWebcam(Socket socket) {
        if (getCommunicationWindowController() != null) {
            getWebcam().open();

            Runnable runnableTranssmitingVideo = () -> {
                while (transmitingVideo) {
                    try {
                        BufferedImage bufferedImage =webcam.getImage();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "jpg", bos );
                        byte [] message = bos.toByteArray();

                        DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                        dOut.writeInt(message.length);
                        dOut.write(message);

                        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                        Platform.runLater(() -> {
                            communicationWindowController.timg.setImage(image);
                        });
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            };
            Thread thread = new Thread(runnableTranssmitingVideo);
            thread.start();

        }
    }
    public static void receiveAndShowImageFromWebcam(Socket socket)
    {
        if(getCommunicationWindowController()!=null)
        {
            System.out.println("tada");
            Runnable runnableReceivingVideo = ()->
            {
                while(receivingVideo)
                {
                    DataInputStream dIn = null;
                    try {
                        dIn = new DataInputStream(socket.getInputStream());
                        int length = dIn.readInt();
                        if(length>0) {
                            byte[] message = new byte[length];
                            dIn.readFully(message, 0, message.length);
                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(message);
                            BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
                            Image image= SwingFXUtils.toFXImage(bufferedImage, null);
                            Platform.runLater(() -> {
                                communicationWindowController.rimg.setImage(image);
                            });
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

    public static void getVideo(Socket socket)
    {
        receiveAndShowImageFromWebcam(socket);
    }
    public static void sendVideo(Socket socket)
    {
        captureAndSendFromWebcam(socket);
    }

}

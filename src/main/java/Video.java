import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Video {
    private static Webcam webcam;
    private static CommunicationWindowController communicationWindowController;
    public static Webcam getWebcam() {
        return webcam;
    }
    private static boolean transmission = false;
    public static void setWebcam(Webcam webcam) {
        Video.webcam = webcam;
    }

    public static boolean isTransmission() {
        return transmission;
    }

    public static void setTransmission(boolean transmission) {
        Video.transmission = transmission;
    }

    public static CommunicationWindowController getCommunicationWindowController() {
        return communicationWindowController;
    }

    public static void setCommunicationWindowController(CommunicationWindowController communicationWindowController) {
        Video.communicationWindowController = communicationWindowController;
    }

    public static void captureAndSendFromWebcam(Socket socket) throws IOException {

                        BufferedImage bufferedImage =webcam.getImage();
                        bufferedImage = DataConverter.qualityOfImage(communicationWindowController.sliderUploadSpeedValue,bufferedImage);
                  if(bufferedImage!=null) {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            ImageIO.write(bufferedImage, "jpg", bos);
                            byte[] message = bos.toByteArray();

                            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                            dOut.writeInt(message.length);
                            dOut.write(message);

                            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                            Platform.runLater(() -> {
                                communicationWindowController.timg.setImage(image);
                            });
                        }

            };




    public static void receiveAndShowImageFromWebcam(Socket socket) throws IOException {
                    DataInputStream dIn = null;
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
    }
    public static boolean configureWebcam() {
        Video.setTransmission(true);
        setWebcam(Webcam.getDefault());
        if (getWebcam() != null) {
            System.out.println("Webcam: " + getWebcam().getName());
            return true;
        } else {
            System.out.println("No webcam detected");
            return false;
        }
    }


}

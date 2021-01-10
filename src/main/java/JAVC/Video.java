package JAVC;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.raspberrypi.RaspistillDriver;
import com.github.sarxos.webcam.ds.raspberrypi.RaspividDriver;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;
import com.sun.mail.iap.ByteArray;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import javax.xml.crypto.Data;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//--module-path "MIEJSCELIBU" -add-modules=javafx.controls,javafx.base,javafx.graphics,javafx.fxml
public class Video {
    /*  static {                                     //
    //    Webcam.setDriver(new RaspistillDriver());    //
    //    Webcam.setDriver(new RaspiYUVDriver());      //
      Webcam.setDriver(new RaspividDriver());      //
    //    Webcam.setDriver(new RaspividYUVDriver());   //
          }      */                                     //
    private static Webcam webcam;
    private static int counter = 0;
    private static CommunicationWindowController communicationWindowController;
    public static Webcam getWebcam() {
        return webcam;
    }
    private static String check = null;
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

    public static void captureAndSendFromWebcam(DatagramSocket socket, InetAddress address,  int port) throws Exception {

                        BufferedImage bufferedImage =webcam.getImage();
                        bufferedImage = DataConverter.qualityOfImage(communicationWindowController.sliderUploadSpeedValue,bufferedImage);
                         if(bufferedImage!=null) {

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            ImageIO.write(bufferedImage, "jpg", bos);
                            byte[] message = EncryptionManager.encrypt(bos.toByteArray());
                            int amountOfMessage = message.length/64000;
                            byte[] sendIt = new byte[64005];

                            String key = "ANNA" + counter;
                            ByteBuffer buff = ByteBuffer.wrap(sendIt);
                            if(amountOfMessage==0) {
                                buff.put(key.getBytes());
                                buff.put(message);
                                //wysyłanie
                                System.out.println(buff.array());
                                DatagramPacket datagramPacket = new DatagramPacket(buff.array(), buff.array().length , sendIt.length, address,port);
                                socket.send(datagramPacket);
                                buff.clear();

                            }
                            else{

                            for(int i =-1; i<amountOfMessage;i++)
                            {

                                buff.put(key.getBytes());
                                buff.put(Arrays.copyOfRange(message,(i+1)*64000,(i+2)*64000));
                                System.out.println(buff.array());
                                DatagramPacket datagramPacket = new DatagramPacket(buff.array(), buff.array().length , sendIt.length, address,port);
                                socket.send(datagramPacket);
                                buff.clear();
                            }
                            }
                            if(counter<9)
                            {
                                counter++;
                            }
                            else
                            {
                                counter = 0;
                            }


                            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                            Platform.runLater(() -> {
                                communicationWindowController.timg.setImage(image);
                            });
                        }

            };




    public static String receiveAndShowImageFromWebcam(DatagramSocket socket, String status) throws Exception {
        byte[] partOfmessage = new byte[64005];
        byte[] imageArray = new byte[64000 * 20];
        String state = status; //ANNAWCZESNIEJSZA


        ByteBuffer buffer = ByteBuffer.wrap(imageArray);
        DatagramPacket datagramPacket = new DatagramPacket(partOfmessage, 64005);
        socket.receive(datagramPacket);
        partOfmessage = datagramPacket.getData();
        while (true)
        {
            System.out.println(Arrays.copyOfRange(partOfmessage, 0, 4).toString());
            if (Arrays.copyOfRange(partOfmessage, 0, 4).toString().equals(state)) {
                buffer.put(Arrays.copyOfRange(partOfmessage,5,partOfmessage.length));

            } else {
                state = Arrays.copyOfRange(partOfmessage, 0, 4).toString();
                break;
            }
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.array());
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);


        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                                Platform.runLater(() -> {
                                    communicationWindowController.rimg.setImage(image);
                                });
                                buffer.clear();
                                return state;
    }

    public static boolean configureWebcam() {

        setWebcam(Webcam.getDefault());
        if (getWebcam() != null) {
            System.out.println("Webcam: " + getWebcam().getName());
            getWebcam().open();
            setTransmission(true);
            return true;
        } else {
            System.out.println("No webcam detected");
            return false;
        }
    }


}

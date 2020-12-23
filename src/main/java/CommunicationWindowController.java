import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

public class CommunicationWindowController {
    private static String me;
    public Button button;
    public ImageView rimg, avatar, timg;
    public Text textMail, textLogin, choosenFriend;
    public TextField ipAdressText, portText;
    public GridPane gridPaneFriend;
    public float sliderUploadSpeedValue;
    public float sliderDownloadSpeedValue;
    @FXML
    public Slider uploadSpeed, downloadSpeed;
    @FXML
    private void initialize() {
        uploadSpeed.valueChangingProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                sliderUploadSpeedValue =(float)uploadSpeed.getValue();
                try {

                   int sample=(8000+320*(int)sliderUploadSpeedValue)-(8000+320*(int)sliderUploadSpeedValue)%2000;
                    Audio.targetDataLine.close();
                    Audio.reconfigureAudioSend(sample);
                } catch (LineUnavailableException | IOException | InterruptedException e) {
                    e.printStackTrace();
                }



            }
        });
        downloadSpeed.valueChangingProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                sliderDownloadSpeedValue =(float)downloadSpeed.getValue();
            }
        });
    }

    public GridPane getGridPaneFriend() {
        return gridPaneFriend;
    }
    public static float qualityOfImage;
    public void setGridPaneFriend(GridPane gridPaneFriend) {
        this.gridPaneFriend = gridPaneFriend;
    }

    @FXML
    private ScrollPane friendPanel;
    private Menu addF, deleteF;

    public ImageView getTimg() {
        return timg;
    }

    public void setTimg(ImageView timg) {
        this.timg = timg;
    }

    public ImageView getRimg() {
        return rimg;
    }

    public void setRimg(ImageView rimg) {
        this.rimg = rimg;
    }

    public static String getMe() {
        return me;
    }

    public static void setMe(String me) {
        CommunicationWindowController.me = me;
    }

    public void pushCallButton(ActionEvent event) throws IOException,LineUnavailableException {
        TransmissionManager.callToFriend(choosenFriend.getText(),getMe());
    }

    public void pushDisconnectButton(ActionEvent event) {
        Video.setTransmission(false);
        Audio.setTransmission(false);
        Video.getWebcam().close();
        Audio.resetAudio();
        Platform.runLater(()->{ TransmissionManager.communicationWindowController.getRimg().setImage(SwingFXUtils.toFXImage(new BufferedImage(
                getRimg().fitWidthProperty().intValue(),
                getRimg().fitWidthProperty().intValue(),
                BufferedImage.TYPE_INT_RGB),null));
                TransmissionManager.communicationWindowController.getTimg().setImage(SwingFXUtils.toFXImage(new BufferedImage(
                   getTimg().fitWidthProperty().intValue(),
                    getTimg().fitWidthProperty().intValue(),
                    BufferedImage.TYPE_INT_RGB),null));});


    }

    public void addFriend(ActionEvent event) throws IOException {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Text Input Dialog");
        dialog.setHeaderText("Look, a Text Input Dialog");
        dialog.setContentText("Please enter friends name:");
        Optional<String> friendToAdd = dialog.showAndWait();
        if (friendToAdd.isPresent()) {
            Message message = new Message();
            TransmissionManager.sendMessageToServer(TransmissionManager.getClient(), message.requestFriendMessage(dialog.getResult(), getMe()));
        }

    }

    public void deleteFriend(ActionEvent event) throws IOException {
        Message message = new Message();
        TransmissionManager.sendMessageToServer(TransmissionManager.getClient(),message.listDeleteFriends(me));
    }


    public void respondForRequest(ActionEvent event) throws IOException {
        Message message = new Message();
        TransmissionManager.sendMessageToServer(TransmissionManager.getClient(), message.listRequestMessage(me));

    }

    public void configurationCommunicationWindowController(String name) throws IOException {
        textLogin.setText(name);


    }

    public void loadFriends() throws IOException {
        Message message = new Message();
        TransmissionManager.sendMessageToServer(TransmissionManager.getClient(), message.listFriendMessage(getMe()));
    }
    public void serverListiner() {

        Runnable runnable = () -> {
            String message;
            while (true) {
                try {
                    message = TransmissionManager.getMessageFromServer(TransmissionManager.getClient());
                    TransmissionManager.messageExecutor(message);
                } catch (IOException | LineUnavailableException ioException) {
                    ioException.printStackTrace();
                }

            }

        };
        Thread t = new Thread(runnable);
        t.start();

    }

    public void getFriendFromPanelOfFriend(MouseEvent mouseEvent) {
        Node node = mouseEvent.getPickResult().getIntersectedNode();
        Integer index = GridPane.getRowIndex(node);
        Circle circle = new Circle(10, Color.RED);
        Node result;
        Label label;
        if (index != null) {
            for (Node object : getGridPaneFriend().getChildren()) {
                if (GridPane.getRowIndex(object) == index && GridPane.getColumnIndex(object) == 0) {
                    result = object;
                    if (object instanceof Label) {
                        label = (Label) object;
                        choosenFriend.setText(label.getText());
                    }

                }
            };
        }
    }
    public void logOut(ActionEvent event) throws IOException {
        Message message = new Message();
        TransmissionManager.sendMessageToServer(TransmissionManager.getClient(),message.logOutMessage(me));
    }
}

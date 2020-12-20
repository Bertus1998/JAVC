import javafx.application.Platform;
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
                    Audio.reconfigureAudioSend((int)(8000+320*sliderUploadSpeedValue));
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

    public void pushCallButton(ActionEvent event) throws IOException, InterruptedException, LineUnavailableException {
        TransmissionManager.callToFriend(choosenFriend.getText(),getMe());
    }

    public void pushDisconnectButton(ActionEvent event) {
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
            if (TransmissionManager.getMessageFromServer(TransmissionManager.getClient()).equals("Y")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Communicat");
                alert.setHeaderText("Communicat");
                alert.setContentText("Request was send to user or user was added " + dialog.getResult());
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Communicat");
                alert.setHeaderText("Communicat");
                alert.setContentText("There is no user : " + dialog.getResult());
                alert.showAndWait();
            }

        }
        TransmissionManager.setWaitForRespond(false);
    }

    public void deleteFriend(ActionEvent event) {

    }

    public void respondForRequest(ActionEvent event) throws IOException {
        Message message = new Message();
        TransmissionManager.sendMessageToServer(TransmissionManager.getClient(), message.listRequestMessage(me));
        String[] friends = TransmissionManager.getMessageFromServer(TransmissionManager.getClient()).split(" ");
        if (friends != null) {
            String defaultText = "List of friend, choose one to add him to your friend list";
            ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(defaultText, friends);
            Optional<String> result = choiceDialog.showAndWait();
            if (result.isPresent() && !defaultText.equals(result.get())) {
                TransmissionManager.sendMessageToServer(TransmissionManager.getClient(), message.acceptFriendMessage(result.get(), me));

                if (TransmissionManager.getMessageFromServer(TransmissionManager.getClient()).equals("Y")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Communicat");
                    alert.setHeaderText("Communicat");
                    alert.setContentText(result.get() + " was added!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Communicat");
                    alert.setHeaderText("Communicat");
                    alert.setContentText("ERROR");
                    alert.showAndWait();
                }

            }

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Communicat");
            alert.setHeaderText("Communicat");
            alert.setContentText("There is no request from other users");
            alert.showAndWait();
        }
        TransmissionManager.setWaitForRespond(false);
    }

    public void configurationCommunicationWindowController(String name) throws IOException {
        textLogin.setText(name);


    }

    public void loadFriends() throws IOException {
        Message message = new Message();
        TransmissionManager.sendMessageToServer(TransmissionManager.getClient(), message.listFriendMessage(getMe()));
        String groupOfFriends = TransmissionManager.getMessageFromServer(TransmissionManager.getClient());
        System.out.println(groupOfFriends);
        groupOfFriends = groupOfFriends.substring(1);
        String[] friends = groupOfFriends.split(" ");

        System.out.println(friends.length);
        Platform.runLater(() -> {
            int k = 0;
            for (int i = 0; i < friends.length; i = i + 2) {
                gridPaneFriend.add(new Label(friends[i]), 0, k);
                if (friends[i + 1].equals("false")) {
                    gridPaneFriend.add(new Circle(10, Color.RED), 1, k);
                } else {
                    gridPaneFriend.add(new Circle(10, Color.GREEN), 1, k);
                }
                k++;
            }

        });
    }
    public void serverListiner() {

        Runnable runnable = () -> {
            String message;
            String[] arrayOfmessage;
            while (true) {
               // System.out.println(TransmissionManager.isWaitForRespond());
                if (!TransmissionManager.isWaitForRespond()) {
                    try {
                        System.out.println(TransmissionManager.isWaitForRespond());
                        message = TransmissionManager.getMessageFromServer(TransmissionManager.getClient());
                        System.out.println(message);
                        arrayOfmessage = message.split(" ");
                        if (arrayOfmessage[0].equals("RESPONDCALL")) {
                            TransmissionManager.setWaitForRespond(true);
                            String[] finalArrayOfmessage = arrayOfmessage;

                            Platform.runLater(()->{ Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Call dialog");
                            alert.setHeaderText(finalArrayOfmessage[1] + " call!");
                            alert.setContentText("Do you want answer?");
                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.get() == ButtonType.OK) {
                                Message messageAccept = new Message();
                                int []ports = new int[4];
                                try {
                                    int counter =0;
                                    for(int i =5;i<finalArrayOfmessage.length;i++)
                                    {
                                        System.out.println(finalArrayOfmessage[i]);
                                        if(TransmissionManager.isPortAvailable(Integer.parseInt(finalArrayOfmessage[i])))
                                        {
                                            ports[counter] =Integer.parseInt(finalArrayOfmessage[i]);
                                            counter++;
                                            if(counter == 4)
                                            {break;}
                                        }
                                    }
                                    System.out.println(finalArrayOfmessage.toString());
                                    String [] messegaeToStartTransmission= new String[5];
                                    for(int i =0;i<4;i++)
                                    {
                                        messegaeToStartTransmission[i] = String.valueOf(ports[i]);
                                        System.out.println(ports[i]);
                                    }
                                    messegaeToStartTransmission[4] = finalArrayOfmessage[finalArrayOfmessage.length-1];
                                    TransmissionManager.sendMessageToServer(TransmissionManager.getClient(),messageAccept.callAcceptMessage(finalArrayOfmessage[2],getMe(),ports));
                                    TransmissionManager.startTransmission(messegaeToStartTransmission,false);
                                    TransmissionManager.setWaitForRespond(false);
                                } catch (IOException | LineUnavailableException ioException) {
                                    ioException.printStackTrace();
                                }

                            } else {
                                try {
                                    TransmissionManager.sendMessageToServer(TransmissionManager.getClient(), "N");
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }
                            }
                        });
                        } else if (arrayOfmessage[0].equals("CHANGESTATUS")) {
                            loadFriends();
                            TransmissionManager.setWaitForRespond(false);
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();

                    }

                }

            }
        };
        Thread t = new Thread(runnable);
        t.start();


    }

    public void getFriendFromPanelOfFriend(MouseEvent mouseEvent) {
        Node node = mouseEvent.getPickResult().getIntersectedNode();
        Integer index = GridPane.getRowIndex(node);
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
}

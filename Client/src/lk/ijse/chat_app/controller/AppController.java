package lk.ijse.chat_app.controller;

import animatefx.animation.ZoomIn;
import animatefx.animation.ZoomOut;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import lk.ijse.chat_app.service.Client;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class AppController {
    public AnchorPane context;
    public AnchorPane ap_bkImage;
    public ScrollPane sp_main;
    public VBox vbox_message;
    public TextField txt_message;
    public JFXButton btn_sent;
    public JFXButton btn_sent_V;
    public Pane pane_imageSent;
    public JFXButton btn_Close;
    public JFXButton btn_sentImage;
    public JFXButton btn_fileChooser;
    public JFXButton btn_Cancel;
    public ImageView imgViewer;
    public Label lblUsername;
    public JFXButton btn_wClose;
    public JFXButton btn_Minimized;
    public Pane header_pane;
    private Client client;
    private String clientUserName;
    private final FileChooser fileChooser=new FileChooser();
    private Image imageToSent;

    private File file;

    public void initialize(){
        pane_imageSent.setVisible(false);
        this.clientUserName=MainFormController.nameList.get(MainFormController.nameList.size()-1);

        try {
            System.out.println("name"+MainFormController.nameList.get(MainFormController.nameList.size()-1));

            this.client=new Client(new Socket("localhost",4000),clientUserName);
            System.out.println("Client connected to group!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        vbox_message.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                sp_main.setVvalue((Double)newValue);
            }
        });
        lblUsername.setText("Group Chat - "+clientUserName);

        client.receiveMessage(vbox_message);

    }

    public void setClientUserName(String clientUserName){
        this.clientUserName=clientUserName;
    }
    public void txt_messageOnAction(ActionEvent actionEvent) {
        btn_sentOnAction(actionEvent);
    }

    public void btn_sentOnAction(ActionEvent actionEvent) {
        String messageToSent=txt_message.getText();

        /** ensure to text field not empty*/
        if(!messageToSent.isEmpty()){

            HBox hBox=new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(5,5,5,10));

            Text text=new Text(messageToSent);
            TextFlow textFlow=new TextFlow(text);

            textFlow.setStyle("-fx-color: rgb(239,242,255);"+
                    "-fx-background-color: rgb(15,125,242);" +
                    "-fx-background-radius: 20px;");

            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.color(0.934,0.945,0.996));

            hBox.getChildren().add(textFlow);
            vbox_message.getChildren().add(hBox);

            client.sentMessage(clientUserName+": "+messageToSent);
            txt_message.clear();
        }
    }

    public static void receiveMessage(String receivedMessage,VBox vbox){
        HBox hBox=new HBox();
        hBox.setPadding(new Insets(5,5,5,10));
        Text text=new Text(receivedMessage);
        TextFlow textFlow=new TextFlow(text);

        if(receivedMessage.startsWith("*")){
            hBox.setAlignment(Pos.CENTER);
            textFlow.setStyle("-fx-background-color: rgb(243,172,157);" +
                    "-fx-background-radius: 20px;");
        }else {
            hBox.setAlignment(Pos.CENTER_LEFT);
            textFlow.setStyle("-fx-background-color: rgb(233,233,235);" +
                    "-fx-background-radius: 20px;");

        }

        textFlow.setPadding(new Insets(5,10,5,10));
        //text.setFill(Color.color(0.934,0.945,0.996));

        hBox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbox.getChildren().add(hBox);
            }
        });
    }

    public static void receiveImage(Image imageReceived, VBox vbox_messages,String senderUsername) {

        HBox hBox=new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,5,5,10));

        ImageView imageView=new ImageView(imageReceived);

        if(imageReceived.getHeight()<imageReceived.getWidth()){
            System.out.println("1");
            imageView.setFitHeight(150);
            imageView.setFitWidth(250);
        }else if(imageReceived.getHeight()>imageReceived.getWidth()) {
            System.out.println("2");
            imageView.setFitHeight(250);
            imageView.setFitWidth(200);
        }else {
            System.out.println("3");
            imageView.setFitHeight(200);
            imageView.setFitWidth(200);
        }

        HBox imgBox=new HBox();
        imgBox.setAlignment(Pos.CENTER);
        receiveMessage(senderUsername,vbox_messages);
        imgBox.getChildren().add(imageView);
        hBox.getChildren().add(imgBox);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbox_messages.getChildren().add(hBox);
            }
        });

    }

    public void btn_sent_VOnAction(ActionEvent actionEvent) {
        imgViewer.setImage(null);
        pane_imageSent.setVisible(true);
        new ZoomIn(pane_imageSent).play();
        btn_Cancel.setVisible(false);
        btn_sentImage.setDisable(true);
    }

    public void btn_CloseONAction(ActionEvent actionEvent) {
        new ZoomOut(pane_imageSent).play();
//        pane_imageSent.setVisible(false);
    }

    public void btn_sentImageOnAction(ActionEvent actionEvent) {
        if(imageToSent != null){
            HBox hBox=new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(5,5,5,10));

            ImageView imageView=new ImageView(imageToSent);

            if(imageToSent.getHeight()<imageToSent.getWidth()){
                System.out.println("1");
                imageView.setFitHeight(150);
                imageView.setFitWidth(250);
            }else if(imageToSent.getHeight()>imageToSent.getWidth()) {
                System.out.println("2");
                imageView.setFitHeight(250);
                imageView.setFitWidth(200);
            }else {
                System.out.println("3");
                imageView.setFitHeight(200);
                imageView.setFitWidth(200);
            }

            HBox imgBox=new HBox();
            imgBox.setAlignment(Pos.CENTER);
//            imgBox.setStyle("-fx-background-color: rgb(15,125,242);" +
//                    "-fx-background-radius: 20px;");
//            imgBox.setPadding(new Insets(10,10,10,10));
            imgBox.getChildren().add(imageView);

            hBox.getChildren().add(imgBox);
            vbox_message.getChildren().add(hBox);
            new ZoomOut(pane_imageSent).play();
            pane_imageSent.setVisible(false);

            client.sentImage(this.file,clientUserName+" : ");
//            client.sentImage(imageToSent);
        }
    }

    public void btn_fileChooserOnAction(ActionEvent actionEvent) {
        fileChooser.setTitle("Group Chat File Chooser");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files","*.jpg"));
        Stage window = (Stage) btn_sentImage.getScene().getWindow();
        file=fileChooser.showOpenDialog(window);

        if(file!=null){
            imageToSent=new Image(file.toURI().toString());
            imgViewer.setImage(imageToSent);

            btn_sentImage.setDisable(false);
            btn_Cancel.setVisible(true);
        }else {
            new Alert(Alert.AlertType.INFORMATION,"Please select image").show();
        }
    }

    public void btn_CancelONAction(ActionEvent actionEvent) {
        imageToSent=null;
        imgViewer.setImage(null);
        btn_sentImage.setDisable(true);
        btn_Cancel.setVisible(false);
    }

    public void btn_wCloseOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) btn_sent.getScene().getWindow();
        stage.close();
    }

    public void btn_MinimizedOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) btn_sent.getScene().getWindow();
        stage.setIconified(true);
    }
}

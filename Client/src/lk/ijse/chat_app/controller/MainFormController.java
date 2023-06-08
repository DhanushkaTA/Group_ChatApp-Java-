package lk.ijse.chat_app.controller;

import animatefx.animation.Shake;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;

public class MainFormController {
    public TextField txt_userName;
    public JFXButton btn_start;

    public static ArrayList<String>nameList=new ArrayList<>();
    public Label lbl_warning;

    private double x,y;

    public void initialize(){
        lbl_warning.setVisible(false);
    }

    public void txt_userNameOnAction(ActionEvent actionEvent) {
        btn_startOnAction(actionEvent);
    }

    public void btn_startOnAction(ActionEvent actionEvent) {
        lbl_warning.setVisible(false);

        if(!txt_userName.getText().isEmpty()){
            if(!checkUsernameAlreadyUsed(txt_userName.getText())){

                nameList.add(txt_userName.getText());

                FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("../view/App.fxml"));
                Parent load = null;
                try {
                    load = fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Stage stage = new Stage();
//                Image icon=new Image(getClass().getResourceAsStream("lk/ijse/chat_app/assets/icon/icon_2.png"));
//                stage.getIcons().add(icon);
                stage.setResizable(false);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(new Scene(load));
                load.setOnMousePressed(event -> {
                    x=event.getSceneX();
                    y=event.getSceneY();
                });

                load.setOnMouseDragged(event -> {
                    stage.setX(event.getScreenX() - x);
                    stage.setY(event.getScreenY() - y);
                });
                stage.centerOnScreen();
                //stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
                txt_userName.clear();

            }else{
                lbl_warning.setText("This Username already used!");
                lbl_warning.setVisible(true);
                new Shake(txt_userName).play();
            }

        }else {
            lbl_warning.setText("Username can't be Empty!");
            lbl_warning.setVisible(true);
            new Shake(txt_userName).play();
        }


    }

    private boolean checkUsernameAlreadyUsed(String username){
        for (String clientUsername:nameList){
            if (clientUsername.equals(username)){
                return true;
            }
        }
        return false;
    }
}

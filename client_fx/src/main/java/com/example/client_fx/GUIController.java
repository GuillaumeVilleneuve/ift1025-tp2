package com.example.client_fx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.text.ChoiceFormat;
import java.util.ResourceBundle;
import javafx.scene.control.*;

public class GUIController implements Initializable {
    private final String[] sessions = {"automne", "hiver", "été"};

    @FXML
    private TextField textFieldFirstName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        textFieldFirstName.setText("Bonjour");
        System.out.println(textFieldFirstName.getText());
    }








}

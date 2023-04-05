package com.example.client_fx;

import com.example.server.models.Course;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.*;


// previously implemented initializable
public class GUIController implements Initializable {

    // attributes
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldMatricule;
    @FXML
    private Button buttonInscription;
    @FXML
    private Button buttonCharger;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML ListView<String> listView1;
    @FXML ListView<String> listView2;


    private String[] semesters = {"Automne", "Hiver", "Été"};

    private Model model;


    public void buttonChargerClicked(ActionEvent e) {
        model.loadCourses();
        }

    public void buttonInscriptionClicked(ActionEvent e) {
        model.registerForCourses();
    }


    // returns the semester choice selected by the user from the choice box
    public String getSemesterChoice() {
        return this.choiceBox.getValue();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        choiceBox.getItems().addAll(semesters);

        for (Course course: Model.courses) {
            listView1.getItems().add(course.getCode());
            listView2.getItems().add(course.getName());
        }
    }

    /*@Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        textFieldFirstName.setText("Bonjour");
        System.out.println(textFieldFirstName.getText());
    }*/








}

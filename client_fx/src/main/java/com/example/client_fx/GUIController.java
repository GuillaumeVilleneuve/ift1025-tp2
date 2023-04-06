package com.example.client_fx;

import com.example.server.models.Course;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
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
    @FXML ListView<String> listView;
    private String[] semesters = {"Automne", "Hiver", "Ete"};
    private Model model;

    public GUIController() {
        try {
            this.model = new Model();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buttonChargerClicked(ActionEvent e) {

        listView.getItems().clear();

        model.loadCourses(getSemesterChoice());
        model.getAvailableCourses();

        // add course info to list view
        for (Course course: Model.courses) {
            listView.getItems().add(course.getCode() + "\t\t\t" + course.getName());
        }

        model.connect();

    }

    public void buttonInscriptionClicked(ActionEvent e) {
        model.registerForCourses();
    }

    // returns the semester choice selected by the user from the choice box
    public String getSemesterChoice() {
        return this.choiceBox.getValue();
    }

    /*public String getSelectedCourse() {

    }*/

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        choiceBox.getItems().addAll(semesters);


        // add course info to list view
        /*for (Course course: Model.courses) {
            listView1.getItems().add(course.getCode());
            listView2.getItems().add(course.getName());

        }*/
    }

    /*@Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        textFieldFirstName.setText("Bonjour");
        System.out.println(textFieldFirstName.getText());
    }*/

    // NOTE : to remove objects from list view,
    // listView.getItems.removeAll();






}

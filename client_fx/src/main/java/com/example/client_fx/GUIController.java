package com.example.client_fx;

import com.example.server.models.Course;
import com.example.server.models.RegistrationForm;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private RegistrationForm registrationForm;    // current selection of the course in the list view

    public GUIController() {
        try {
            this.model = new Model();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        choiceBox.getItems().addAll(semesters);
    }

    public void buttonChargerClicked(ActionEvent e) {

        listView.getItems().clear();

        model.loadCourses(getSemesterChoice());
        model.getAvailableCourses();

        // add course info to list view
        for (Course course: Model.courses) {
            listView.getItems().add(course.getCode() + "\t\t\t" + course.getName());
        }

        // reconnects to server after being disconnected
        model.connect();

    }

    public void buttonInscriptionClicked(ActionEvent e) {

        // get info entered by user
        String firstName = this.textFieldFirstName.getText();
        String lastName = this.textFieldLastName.getText();
        String email = this.textFieldEmail.getText();
        String matricule = this.textFieldMatricule.getText();

        MultipleSelectionModel<String> selectionModel = listView.getSelectionModel();
        String[] courseCodeAndName = selectionModel.getSelectedItem().split("\t");
        String courseCode = courseCodeAndName[0];

        // finds the course selected in the list view in the arrayList of courses available for the semester
        Course courseFound = null;
        for (Course course: Model.courses) {
            if (course.getCode().equals(courseCode)) {
                courseFound = course;
                break;
            }
        }

        // create and send registration form to model
        registrationForm = new RegistrationForm(firstName, lastName, email, matricule, courseFound); //
        model.registerForCourses(registrationForm);

        // if email entered is invalid
        if (!model.getIsValidEmail()) {
            alertEmailErrorMessage();
        };

        // if ID entered is invalid
        if (!model.getIsValidID()) {
            alertIDErrorMessage();
        }

        // if email and ID are valid, show confirmation message alert
        if (model.getIsValidEmail() && model.getIsValidID()) {
            printConfirmationMessage();
        }

        // reconnects to server after being disconnected
        model.connect();
    }

    // returns the semester choice selected by the user from the choice box
    public String getSemesterChoice() {
        return this.choiceBox.getValue();
    }


    public void alertEmailErrorMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur email");
        alert.setHeaderText("Il semble avoir une erreur dans votre email");
        alert.setContentText("SVP veuillez entrer un email valide!");
        alert.showAndWait();
    }

    public void alertIDErrorMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur matricule");
        alert.setHeaderText("Il semble avoir une erreur dans votre matricule");
        alert.setContentText("SVP veuillez entrer un email matricule valide!");
        alert.showAndWait();
    }

    public void printConfirmationMessage() {
        model.setConfirmationMessage();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation d'inscription");
        alert.setHeaderText("Information sur votre confirmation d'inscription");
        alert.setContentText(model.getConfirmationMessage());
        alert.showAndWait();
    }




}

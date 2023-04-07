package com.example.client_fx;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import com.example.server.Server;
import com.example.server.models.Course;
import com.example.server.models.RegistrationForm;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Responsible for managing the application's data and business logic. It is a software component that encapsulates
 * the data and the behavior of the application. Works in conjunction with the view and controller classes.
 */
public class Model {

    // attributes
    private Socket clientSocket;
    private ObjectOutputStream objectOutputStream;

    private ObjectInputStream objectInputStream;
    static ArrayList<Course> courses = null;
    private String cmd;
    private String args;
    private RegistrationForm registrationForm;
    private String confirmationMessage = null; // confirmation message sent to user after trying to register for courses
    private boolean validEmail;
    private boolean validID;

    /**
     * constructor for the model class. Calls the connect method in order to connect to the server
     *
     * @throws IOException thrown when connecting to server fails
     */
    public Model () throws IOException {
        connect();  // connects to server
    }

    /**
     * sends load command ("INSCRIRE") to server
     * @param semester the semester selected by the user
     */
    public void loadCourses(String semester) {

        cmd = Server.LOAD_COMMAND;
        args = semester;

        try {
            objectOutputStream.writeObject(cmd + " " + args);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * reads the list of available courses returned by the server
     */
    // receive available courses from the server
    public void getAvailableCourses() {

        try {
            this.courses = (ArrayList<Course>) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * takes the user input and registers the user for courses. Validates email and ID.
     * @param registrationForm where the register input info is stored
     */
    public void registerForCourses(RegistrationForm registrationForm) {

        // send register command to server
        this.cmd = Server.REGISTER_COMMAND;
        this.args = registrationForm.getCourse().getSession();

        // validate infos (
        validateEmail(registrationForm.getEmail());
        validateID(registrationForm.getMatricule());    // ID is the matricule of the student

        // if the email or the id entered is invalid, will not write registrationForm object in socket
        if (!validEmail || !validID) {
            disconnect();
            return;
        }

        // the email and id are valid -> writes registrationForm in socket
        try {
            objectOutputStream.writeObject(cmd + " " + args);
            objectOutputStream.flush();
            objectOutputStream.writeObject(registrationForm);
            objectOutputStream.flush();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * setter for the confirmation message object returned by the server if the registration was successful
     */
    public void setConfirmationMessage() {
        try {
            this.confirmationMessage = (String) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * setter for the confirmation message object returned by the server if the registration was successful
     * @return confirmation message
     */
    public String getConfirmationMessage() {
        return confirmationMessage;
    }

    /**
     * validates email by assuring that the user input respects the conventional email format
     * @param input email entered by the user
     */
    public void validateEmail(String input) {
        String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern emailPattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(input);
        this.validEmail = matcher.find();
    }

    /**
     * validates ID by assuring that the user input respects the conventional ID format (8 digits)
     * @param input ID entered by user
     */
    public void validateID(String input) {
        String IDRegex = "^[0-9]{8}$";
        Pattern IDPattern = Pattern.compile(IDRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = IDPattern.matcher(input);
        this.validID = matcher.find();
    }

    /**
     * getter for the validEMail attribute
     * @return
     */
    public boolean getIsValidEmail() {
        return validEmail;
    }

    /**
     * getter for the validId attribute
     * @return
     */
    public boolean getIsValidID() {
        return validID;
    }

    /**
     * connects the client to the server
     */
    public void connect() { // connects to server
        try {
            clientSocket = new Socket("127.0.0.1", 1337);
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * disconnect client from server
     */
    public void disconnect() {
        try {
            objectOutputStream.close();
            objectInputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

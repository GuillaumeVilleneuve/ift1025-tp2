package com.example.client_fx;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import com.example.server.Server;
import com.example.server.models.Course;
import com.example.server.models.RegistrationForm;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Model {

    // attributes
    private Socket clientSocket;
    private GUIController guiController;
    private ObjectOutputStream objectOutputStream;

    private ObjectInputStream objectInputStream;
    static ArrayList<Course> courses = null;
    private String cmd;
    private String args;
    private RegistrationForm registrationForm;
    private String confirmationMessage = null; // confirmation message sent to user after trying to register for courses


    public Model () throws IOException {
        connect();  // connects to server
    }

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

    public void registerForCourses(RegistrationForm registrationForm) {

        // send register command to server
        this.cmd = Server.REGISTER_COMMAND;
        this.args = registrationForm.getCourse().getSession();

        try {
            objectOutputStream.writeObject(cmd + " " + args);
            objectOutputStream.flush();
            objectOutputStream.writeObject(registrationForm);
            objectOutputStream.flush();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void setConfirmationMessage() {
        try {
            this.confirmationMessage = (String) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getConfirmationMessage() {
        return confirmationMessage;
    }

    // TODO validate email
    public static boolean validateEmail(String input) {
        String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern emailPattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(input);
        return matcher.find();
    }

    public static boolean validateID(String input) {
        String IDRegex = "^[0-9]{8}$";
        Pattern IDPattern = Pattern.compile(IDRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = IDPattern.matcher(input);
        return matcher.find();
    }


    public void connect() { // connects to server
        try {
            clientSocket = new Socket("127.0.0.1", 1337);
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
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

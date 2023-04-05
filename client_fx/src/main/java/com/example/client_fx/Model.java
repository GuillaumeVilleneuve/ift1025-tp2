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


    public Model (int port) throws IOException {
        clientSocket = new Socket("127.0.0.1", port);
    }

    // TODO : should we put the content of the run command in the model constructor?
    public void run() {

        try {
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void loadCourses() {

        cmd = Server.LOAD_COMMAND;
        args = guiController.getSemesterChoice();

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

    public void registerForCourses() {
        // TODO
    }

    // checks if the course code is in the list of available courses for the semester
    // if found, returns the corresponding Course object
    public Course validateCourse(String courseCodeEntered) {

        for (Course course: this.courses) {
            if (course.getCode().equals(courseCodeEntered)) {
                return course;    // valid code for the semester
            }
        }

        return null;    // invalid course for the semester

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

}

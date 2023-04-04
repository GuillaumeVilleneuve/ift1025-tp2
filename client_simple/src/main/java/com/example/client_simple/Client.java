package com.example.client_simple;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import com.example.server.Server;
import com.example.server.models.Course;
import com.example.server.models.RegistrationForm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Client {
    // attributes
    private Socket clientSocket;
    private ObjectOutputStream objectOutputStream;

    private ObjectInputStream objectInputStream;
//    private CmdAndArgs cmdAndArgs = new CmdAndArgs();    // command and arguments object sent to server
    static ArrayList<Course> courses = null;
    String cmd;
    String args;



    public Client(int port) throws IOException {
        clientSocket = new Socket("127.0.0.1", port);
    }

    public void run() {

        try {
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        menu();
    }

    public void menu() {
        // First, asks to choose a semester to display courses. Will only display the following message if it is the
        // first request to the server
        if (ClientLauncher.firstRequest) {
            System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM");
            displayCoursesMenu();
        }

        else {
            // 2nd, asks user to choose between displaying the courses of another semester or register for a course
            System.out.println("> Choix:");
            System.out.println("1. Consulter les cours offerts pour une autre session");
            System.out.println("2. Inscription à un cours");

            Scanner scanner = new Scanner(System.in);
            String userInput = scanner.nextLine();
            System.out.println("> Choix: " + cmd);

            switch (userInput) {
                case "1": {
                    displayCoursesMenu();
                    break;
                }
                case "2": {
                    registerForCourses();
                    break;
                }
                default: {
                    System.out.println("Choix invalide. Veuillez choisir parmi les choix disponibles");
                    disconnect();
                }
            }
        }

    }

    // menu that displays the courses
    public void displayCoursesMenu() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours");
        System.out.println("1. Automne \n2. Hiver \n3. Ete");

        String userInput = scanner.nextLine();
        cmd = Server.LOAD_COMMAND;

        // the user can enter either enter the semesters name or the corresponding number (1, 2 or 3)
        if (cmd.equalsIgnoreCase("Automne")) {
            userInput = "1";
        }   else if (cmd.equalsIgnoreCase("Hiver")) {
            userInput = "2";
        }   else if (cmd.equalsIgnoreCase("Ete")) {
            userInput = "3";
        }   else if (cmd.equalsIgnoreCase("Été")) {
            userInput = "3";
        }

        System.out.println("> Choix: " + userInput);

        switch (userInput) {

            case "1":
                args = " Automne";

                try {
                    objectOutputStream.writeObject(cmd + " " + args);
                    objectOutputStream.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "2":
                args = " Hiver";

                try {
                    objectOutputStream.writeObject(cmd + " " + args);
                    objectOutputStream.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "3":
                args = " Ete";

                try {
                    objectOutputStream.writeObject(cmd + " " + args);
                    objectOutputStream.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                System.out.println("invalid choice of semester");
        }

        getAvailableCourses();
        displayCourses();
    }

    public void getAvailableCourses() {

        // send command to server
        try {
            objectOutputStream.writeObject(Server.LOAD_COMMAND);
            objectOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // get Course object

        try {
            this.courses = (ArrayList<Course>) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayCourses() {
        // display courses
        System.out.println("Les cours offerts pendant la session d'" + args + " sont:");

        for (Course course: this.courses) {
            System.out.println(course.getCode() + "\t" +
                    course.getName() + "\t" +
                    course.getSession() + "\t");
        }
    }

    public void registerForCourses() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Veuillez saisir votre prénom:");
        String prenom = scanner.nextLine();

        System.out.println("Veuillez saisir votre nom:");
        String nom = scanner.nextLine();


        // email needs to be validated
        boolean emailIsValid;
        String email;
        do {
            System.out.println("Veuillez saisir votre email:");
            email = scanner.nextLine();
            emailIsValid = validateEmail(email);

            if (!emailIsValid) { // invalid email entered
                System.out.println("Veuillez entrer un email valide");
            }
        } while (!emailIsValid);


        // ID needs to be validated
        boolean IDIsValid;
        String matricule;
        String ID;
        do {
            System.out.println("Veuillez saisir votre matricule");
            matricule = scanner.nextLine();
            IDIsValid = validateID(matricule);

            if (!IDIsValid) {
                System.out.println("Veuillez entrer un matricule étudiant valide (exactement 8 chiffres");
            }
        } while (!IDIsValid);


        // ensures that the entered course is offered for the specified semester
        System.out.println("Veuillez saisir le code du cours");
        String courseCodeEntered = scanner.nextLine();
        Course course = validateCourse(courseCodeEntered);

        if (course == null) {   // invalid code for the semester
            System.out.println("Ce cours n'est pas disponible pour ce trimestre");
            System.out.println("Échec de l'inscription");
            disconnect();

        } else {    // valid course for the semester
            RegistrationForm registrationForm = new RegistrationForm(prenom, nom, email, matricule, course);

            try {
                cmd = Server.REGISTER_COMMAND;
                objectOutputStream.writeObject(cmd + " " + args);
                objectOutputStream.flush();
                objectOutputStream.writeObject(registrationForm);
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // receive confirmation message from client
            try {
                String confirmationMessage = (String) objectInputStream.readObject();
                System.out.println(confirmationMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
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

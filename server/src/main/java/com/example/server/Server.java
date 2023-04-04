package com.example.server;

import javafx.util.Pair;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import com.example.server.models.Course;
import com.example.server.models.RegistrationForm;


/**
 * com.example.server.Server class that handles the requests of students
 */
public class Server {

    /**
     * Register command that allows students to enrole to courses
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     * Load command to load courses that a student can enrole for
     */
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * com.example.server.Server constructor
     * Initializes the server socket, the handlers list and adds event handlers to the list.
     *
     * @param port the port of the server
     * @throws IOException if an I/O error occurs when opening the socket
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * appends events to be handled by the server
     *
     * @param h the new event added
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }


    /**
     * TO DO
     *
     * @param cmd the command passed by the user
     * @param arg the arguments of the command
     */
    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * runs the server
     */
    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                objectInputStream = new ObjectInputStream(client.getInputStream());
                listen();
                disconnect();
                System.out.println("com.example.client_simple.Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Listens to client's requests
     * @throws IOException Class of a serialized object cannot be found.
     * @throws ClassNotFoundException Something is wrong with a class used by deserialization.
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;

        if ((line = this.objectInputStream.readObject().toString()) != null) {
            System.out.println(line);
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * Processes the strings entered by the client. Gets command and arguments
     *
     * @param line Line entered by the user
     * @return Returns the command and its arguments
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * Disconnects from server
     *
     * @throws IOException If an I/O error has occurred while closing objectOutPutStream or objectInputStream
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Handles the events by calling handleRegistration and handleLoadCourses methods
     *
     * @param cmd the command of the user
     * @param arg the arguments of the user's command
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     Lire un fichier texte contenant des informations sur les cours et les transformer en liste d'objets 'Course'.
     La méthode filtre les cours par la session spécifiée en argument.
     Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     @param semester la session pour laquelle on veut récupérer la liste des cours
     */

    /**
     * Returns information to the client about the available coures for a semester
     *
     * @param arg School semester
     */
    public void handleLoadCourses(String arg) {

        // filter courses based on the semester
        try {
            Scanner scan = new Scanner(new File("com/example/server/data/cours.txt"));
            ArrayList<Course> courses = new ArrayList<>();

            while (scan.hasNext()) {
                String line = scan.nextLine();
                String[] elements = line.split("\t");   // index0 : course code; index1 : course name; index2: semester

                // checks if the semester is corresponding
                // if so, creates a course object and adds it to the arrayList of courses
                if (elements[2].equalsIgnoreCase(arg)) {
                    Course course = new Course(elements[1], elements[0], elements[2]);
                    courses.add(course);
                }
            }
            scan.close();

            // returns the list of courses to the client via the objectOutputStream
            objectOutputStream.writeObject(courses);
            objectOutputStream.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error while opening file");
        } catch (IOException e) {
            System.out.println("Error while writing to objectOutputStream");
        }

    }

    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */

    /**
     * Handles the client's registration. Saves it in the file inscription.txt
     * returns a confirmation message
     */
    public void handleRegistration() {
        try {

            // retrieves RegistrationForm object
            RegistrationForm registrationForm = (RegistrationForm) objectInputStream.readObject();


            // formats the text to write to inscription.txt
            String inscription = registrationForm.getCourse().getSession() + "\t" +
                    registrationForm.getCourse().getCode() + "\t" +
                    registrationForm.getMatricule() + "\t" +
                    registrationForm.getNom() + "\t" +
                    registrationForm.getPrenom() + "\t" +
                    registrationForm.getEmail();

            // write content to inscription.txt
            FileWriter fw = new FileWriter("com/example/server/data/inscription.txt", true);
            BufferedWriter writer = new BufferedWriter(fw);
            writer.write(inscription);
            writer.write("\n");
            writer.close();

            // confirmation message to client
            objectOutputStream.writeObject("Félicitations! Inscription réussie de " + registrationForm.getPrenom() +
                    " au cours " + registrationForm.getCourse().getCode());
            objectOutputStream.close();

        } catch (ClassNotFoundException e) {
            System.out.println("Registration form object not found in the program"); // TODO
        }
        catch (IOException e){
            System.out.println("Error while reading or writing to file"); // TODO
        }
    }
}

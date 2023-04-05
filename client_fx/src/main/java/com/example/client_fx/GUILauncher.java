package com.example.client_fx;

import java.io.IOException;

public class GUILauncher {
    public final static int PORT = 1337;
    public static void main(String[] args) {
        try {
            Model model = new Model(PORT);
            model.run();
            GUIView.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package org.example;

import javax.swing.*;

public class Main {
    public static String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static String SNIPER_STATUS_NAME = "sniper status";

    private MainWindow ui;

    public Main() throws Exception {
       startUserInterface();
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
    }
    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow();
            }
        });
    }
}
package main;

import view.ServerView;
import controller.ServerController;

public class ServerRun {
    // chạy server từ đây
    public static void main(String[] args) {
        ServerView serverView = new ServerView();
        serverView.setVisible(true);
        serverView.setLocationRelativeTo(null);
        new ServerController();
    }
}

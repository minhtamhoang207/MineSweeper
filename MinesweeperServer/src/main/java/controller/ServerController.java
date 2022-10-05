package controller;

import java.net.ServerSocket;
import java.net.Socket;
import view.ServerView;
import util.DBConnection;
import main.ServerThread;

public class ServerController implements Runnable{ // implement runnable để tạo thread

    ServerThread serverThread;
    
    public ServerController(){
        // kết nối db
        DBConnection.getInstance().getConnection();
        //       startServer();
        new Thread(this).start();
    }
    
    private void startServer(){
        try {
            // server socket
            // cổng của server
            int serverPort = 5555;
            try (ServerSocket myServer = new ServerSocket(serverPort)) {
                ServerView.info.append("Server đang chạy...\n");
                while (true) {
                    Socket clientSocket = myServer.accept();
                    serverThread = new ServerThread(clientSocket);
                    serverThread.serverNoti = ServerView.info;
                    serverThread.start();
                }
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
    }
    @Override
    public void run() {
        this.startServer();
    }
}

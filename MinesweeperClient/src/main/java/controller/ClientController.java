package controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.AbstractMap.SimpleEntry;

import model.Player;
import util.Notification;
import view.*;

public class ClientController implements Runnable, Notification {
    private Socket myClient;
    Thread clientThread;
    String clientName;
    LoginView loginView;
    boolean isRunning;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    
    private static ClientController instance;

    public ClientController() {
        instance = this;
        loginView = new LoginView();

        loginView.setVisible(true);
        loginView.setLocationRelativeTo(null);
        
        isRunning = true;
        addEventsLoginView();
    }
    
    @Override
    public void run() {}
    
    //add events
    void addEventsLoginView() {
        loginView.getLoginBtn().addActionListener(ae -> {
            Player player = new Player(loginView.getUsernameText().getText(), String.valueOf(loginView.getPasswordText().getPassword()));
            openConnection();
            sendData("CHECK_PLAYER", player);
            String result = (String) receiveData();
            System.out.println("RECEIVED FROM SERVER: " + result);
            if(result.equals(LOGIN_SUCCESS)) {
                loginView.showMessage(result);
                loginView.setVisible(true);
                clientName = player.getUserName();
                clientThread = new Thread(instance);
                clientThread.start();
            } else if(result.equals(ALREADY_LOGIN)){
                loginView.showMessage(result);
                loginView.setVisible(true);
                clientName = player.getUserName();
                clientThread = new Thread(instance);
                clientThread.start();
            }
            else {
                loginView.showMessage(LOGIN_FAIL);
            }
        });

        loginView.getSignupBtn().addActionListener(ae -> {
            Player player = new Player(loginView.getUsernameText().getText(), String.valueOf(loginView.getPasswordText().getPassword()));
            openConnection();
            sendData("SIGNUP_PLAYER", player);
            String result = (String)receiveData();
            System.out.println("RECEIVED FROM SERVER: " + result);
            if(result.equals(SIGNUP_SUCCESS)) {
                loginView.showMessage(result);
                loginView.setVisible(true);

                clientName = player.getUserName();
                clientThread = new Thread(instance);
                clientThread.start();
            } else {
                loginView.showMessage(SIGNUP_FAIL);
            }
        });
                
        
    }

    // khi client khởi tạo thì mở kết nối
    public void openConnection(){
        try {
            int serverPort = 5555;
            String serverHost = "localhost";
            myClient = new Socket(serverHost, serverPort);
            oos = new ObjectOutputStream(myClient.getOutputStream());
            ois = new ObjectInputStream(myClient.getInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void closeConnection(){
        try {
            myClient.close();
            isRunning = false;
            ois.close();
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void sendData(String cmd, Object data){
        try {
            if(data instanceof Player) {
                Player player = (Player)data;
                SimpleEntry<String, Player> req = new SimpleEntry<>(cmd, player);
                oos.writeObject(req);
            }
            if(data == null) {
                SimpleEntry<String, Object> req = new SimpleEntry<>(cmd, null);
                oos.writeObject(req);
            }
            if(data instanceof String) {
                String s = (String)data;
                SimpleEntry<String, String> req = new SimpleEntry<>(cmd, s);
                oos.writeObject(req);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public Object receiveData(){
        try {
            Object o = ois.readObject();
            if(o instanceof String){
                return o;
            }
            return o;
        } catch (Exception ex) {
            ex.printStackTrace();     
            return null;
        }
    }
}

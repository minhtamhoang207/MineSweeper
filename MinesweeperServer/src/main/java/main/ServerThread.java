package main;

import controller.PlayerController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import model.Player;

public class ServerThread extends Thread {
    Socket socket; // socket từ client
    String clientName; // client name

    // hashtable lưu danh sách các player(socket) đang kết nối với server
    public static Hashtable<Player, ServerThread> listClient = new Hashtable<>();
    ObjectInputStream ois;
    ObjectOutputStream oos;
    
    PlayerController playerController;
    public JTextArea serverNoti;
    
    boolean isRunning;
    boolean isPlaying;
    //NOTIFICATIONS
    public static final String LOGIN_SUCCESS = "Đăng nhập thành công!";
    public static final String SIGNUP_SUCCESS = "Đăng ký thành công";
    public static final String SIGNUP_FAIL = "Tài khoản đã được sử dụng";
    public static final String LOGIN_FAIL = "Tài khoản hoặc mật khẩu không đúng!";
    public static final String ALREADY_LOGIN = "Tài khoản đã đăng nhập!";

    public ServerThread(Socket socket) {
        this.socket = socket;
        clientName = "";
        playerController = PlayerController.getInstance();
        isRunning = true;
        isPlaying = false;
    }

    @Override
    public void run() {
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            
            SimpleEntry request;
            String cmd;
            Object data;
            while(isRunning)
            {
                Object o = ois.readObject();
                request = (SimpleEntry) o;
                cmd = (String) request.getKey();
                data = request.getValue();
                switch (cmd) {
                    case "CHECK_PLAYER" -> {
                        Player player = (Player) data;
                        Player _player = playerController.checkPlayer(player, false);
                        if (_player != null) {
                            boolean isLoggedIn = false;
                            Enumeration<Player> keys = listClient.keys();
                            while (keys.hasMoreElements()) {
                                if (keys.nextElement().getUserName().equals(player.getUserName())) {
                                    isLoggedIn = true;
                                }
                            }
                            if (!isLoggedIn) {
                                clientName = player.getUserName();
                                serverNoti.append("Người chơi " + clientName + " đã kết nối\n");
                                playerController.UpdateStatus(clientName, "rảnh rỗi");
                                Player updatedPlayer = playerController.checkPlayer(player, false);
                                listClient.put(updatedPlayer, this);
                                oos.writeObject(LOGIN_SUCCESS);
                            } else {
                                oos.writeObject(ALREADY_LOGIN);
                            }
                        } else oos.writeObject(LOGIN_FAIL);
                    }
                    case "SIGNUP_PLAYER" -> {
                        Player newPlayer = (Player) data;
                        Player _player = playerController.checkPlayer(newPlayer, true);
                        if(_player == null ){
                            String result = playerController.insert(newPlayer);
                            if (Objects.equals(result, "OK")) {
                                clientName = newPlayer.getUserName();
                                serverNoti.append("Người chơi " + clientName + " đã kết nối\n");
                                listClient.put(newPlayer, this);
                                oos.writeObject(SIGNUP_SUCCESS);
                                System.out.println("New player added!");
                            } else oos.writeObject(SIGNUP_FAIL);
                        } else oos.writeObject(SIGNUP_FAIL);
                    }
                    case "GET_ONLINE_PLAYERS" ->{
                        ArrayList<Player> onlinePlayers = getListOnlinePlayer();
    //                    oos.writeObject(onlinePlayers);
                        sendToAllClients("GET_ONLINE_PLAYERS", onlinePlayers);
                        break;
                    }
                    case "GET_RANK_PLAYERS" ->{
                        ArrayList<Player> players = new ArrayList<>();
                        players = playerController.getRankPlayersByPoint();
                        sendToClient("GET_RANK_PLAYERS", players);
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendToClient(String cmd, Object data)
    {
        try {
            SimpleEntry<String, Object> res = new SimpleEntry<>(cmd, data);
            oos.writeObject(res);
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void updateStatusOnline(String name, String status)
    {
//       playerController.UpdateStatus(name, status);
       Enumeration<Player> keys = listClient.keys();
        while(keys.hasMoreElements())
        {
            Player p = keys.nextElement();
            if(p.getUserName().equals(name))
            {
                p.setStatus(status);
                serverNoti.append("tran thai cua" +name + " sau update la" + p.getStatus());
                break;
            }
                        
        }
//        Player updatedPlayer = playerController.checkPlayer(name);
//        listClient.put(updatedPlayer, this);
    }

    
    public ArrayList<Player> getListOnlinePlayer()
    {
        Enumeration<Player> keys = listClient.keys();   
        ArrayList<Player> onlinePlayers = new ArrayList<>();
        while(keys.hasMoreElements())
        {
            onlinePlayers.add(keys.nextElement());
        }
        return onlinePlayers;
    }
    
     public void sendToAllClients(String cmd, Object data)
    {
        Enumeration<ServerThread> clients = listClient.elements();
        ServerThread client;
        while(clients.hasMoreElements())
        {
            client = clients.nextElement();
            try {
                SimpleEntry<String, Object> res = new SimpleEntry<>(cmd, data);
                client.oos.writeObject(res);
            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    
    public void sendToSpecificClient(String _clientName, String cmd, Object data)
    {
        Enumeration<Player> keys = listClient.keys();
        ServerThread client = null;
        while(keys.hasMoreElements())
        {
            Player p = keys.nextElement();
            if(p.getUserName().equals(_clientName))
            {
                client = listClient.get(p);
            }
        }
        try {
            SimpleEntry<String, Object> res = new SimpleEntry<>(cmd, data);
            client.oos.writeObject(res);
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeServerThread() {
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  
}

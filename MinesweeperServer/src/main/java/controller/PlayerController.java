package controller;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import util.*;
import model.Player;

public class PlayerController { // controller xử lý dữ liệu của player
    private final Connection connection;
    private final String SELECT_PLAYERS_BY_POINT = "SELECT * FROM player ORDER BY point DESC;";

    private static PlayerController instance;
    
    public static PlayerController getInstance() {
        if (instance == null) {
            instance = new PlayerController();
        }
        return instance;
    }
    
    public PlayerController() {
        this.connection = DBConnection.getInstance().getConnection();
    }
    public Player checkPlayer(Player player, boolean signup) {
        String CHECK_PLAYER;
        if(signup){
            CHECK_PLAYER = "SELECT * FROM player WHERE username ='" + player.getUserName() + "'";
        } else {
            CHECK_PLAYER = "SELECT * FROM player WHERE username ='" + player.getUserName() + "' AND password ='" + player.getPassword() + "'";
        }
        try {
         Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(CHECK_PLAYER);
         if (rs.next()) {
             String username = rs.getString("username");
             float point = rs.getFloat("point");
             float avgPointOpp = rs.getFloat("avgPointOpp");
             String avgTime = rs.getTime("avgTime").toString();
             String status = rs.getString("status");
             return new Player(username, point, avgPointOpp, avgTime, status);
         }
        }catch(Exception e) {
         e.printStackTrace();
        } 
        return null;
    }

    public void UpdateStatus(String player, String status)
    {
        String query = "UPDATE player SET status = '" + status + "' WHERE username='" + player + "';";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.executeUpdate(query);
        }catch(Exception e) {
         e.printStackTrace();
        } 
    }
    public String insert(Player player) {
        try {
            String INSERT_PLAYER = "INSERT INTO player (username, password, point, avgPointOpp, avgTime, status) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement p = connection.prepareStatement(INSERT_PLAYER);
            p.setString(1, player.getUserName());
            p.setString(2, player.getPassword());
            p.setFloat(3, player.getPoint());
            p.setFloat(4, player.getAvgPointOpp());
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            Time avgTime = new Time(0);
            p.setTime(5, avgTime);
            p.setString(6, player.getStatus());
            p.executeUpdate();
            p.close();
            return "OK";
        } catch (SQLException e) {
            e.printStackTrace();
            return "FAILED";
        }
    }
    
    public ArrayList<Player> getRankPlayersByPoint()
    {
        ArrayList<Player> players = new ArrayList<>();
        try {
         Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(SELECT_PLAYERS_BY_POINT);
         while(rs.next())
         {
             String username = rs.getString("username");
             float point = rs.getFloat("point");
             float avgPointOpp = rs.getFloat("avgPointOpp");
             String avgTime = rs.getTime("avgTime").toString();
             String status = rs.getString("status");
             Player player = new Player(username, point, avgPointOpp, avgTime, status);
             players.add(player);
         }
        }catch(Exception e) {
         e.printStackTrace();
        } 
//        return null;
        return players;
    }
    
}

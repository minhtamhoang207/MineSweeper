package model;

import java.io.Serializable;
public class Player implements Serializable{
    private String userName;
    private String password;
    private float point;
    private float avgPointOpp;
    private String avgTime;
    private String status;

    public Player(){}

    public Player(String username, String password){
        this.userName = username;
        this.password = password;
    }

    public Player(String userName, float point, float avgPointOpp, String avgTime, String status) {
        this.userName = userName;
        this.point = point;
        this.avgPointOpp = avgPointOpp;
        this.avgTime = avgTime;
        this.status = status;
    }
    
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public float getPoint() {
        return point;
    }

    public void setPoint(float point) {
        this.point = point;
    }

    public float getAvgPointOpp() {
        return avgPointOpp;
    }

    public void setAvgPointOpp(float avgPointOpp) {
        this.avgPointOpp = avgPointOpp;
    }

    public String getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(String avgTime) {
        this.avgTime = avgTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}

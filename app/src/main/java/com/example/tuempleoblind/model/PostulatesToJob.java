package com.example.tuempleoblind.model;

public class PostulatesToJob {
    String userBlindName, userBlindPhone,userEmail;
    public PostulatesToJob(){}

    public PostulatesToJob(String userBlindName, String userBlindPhone, String userEmail) {
        this.userBlindName = userBlindName;
        this.userBlindPhone = userBlindPhone;
        this.userEmail = userEmail;
    }

    public String getUserBlindName() {
        return userBlindName;
    }

    public void setUserBlindName(String userBlindName) {
        this.userBlindName = userBlindName;
    }

    public String getUserBlindPhone() {
        return userBlindPhone;
    }

    public void setUserBlindPhone(String userBlindPhone) {
        this.userBlindPhone = userBlindPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}

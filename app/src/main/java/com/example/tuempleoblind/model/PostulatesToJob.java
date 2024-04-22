package com.example.tuempleoblind.model;

public class PostulatesToJob {
    String userBlindName, userBlindPhone,userEmail,Profetion,abilities;
    public PostulatesToJob(){}

    public PostulatesToJob(String userBlindName, String userBlindPhone, String userEmail,String userProfetion, String userAbilitires) {
        this.userBlindName = userBlindName;
        this.userBlindPhone = userBlindPhone;
        this.userEmail = userEmail;
        this.Profetion = userProfetion;
        this.abilities = userAbilitires;
    }

    public String getProfetion() {
        return Profetion;
    }

    public void setProfetion(String profetion) {
        Profetion = profetion;
    }

    public String getAbilities() {
        return abilities;
    }

    public void setAbilities(String abilities) {
        this.abilities = abilities;
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

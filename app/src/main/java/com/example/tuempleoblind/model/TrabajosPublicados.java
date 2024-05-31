package com.example.tuempleoblind.model;

import android.text.BoringLayout;

public class TrabajosPublicados {
    String title;
    String category;
    String salary;
    String companyPublishId;
    Boolean checkRamp;
    Boolean checkElevator;
    public TrabajosPublicados(){}

    public TrabajosPublicados(String title, String category, String salary,String companyPublishId ,Boolean checkRamp, Boolean checkElevator) {
        this.title = title;
        this.category = category;
        this.salary = salary;
        this.checkRamp = checkRamp;
        this.checkElevator = checkElevator;
        this.companyPublishId=companyPublishId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public Boolean getCheckRamp() {
        return checkRamp;
    }

    public void setCheckRamp(Boolean checkRamp) {
        this.checkRamp = checkRamp;
    }

    public Boolean getCheckElevator() {
        return checkElevator;
    }

    public void setCheckElevator(Boolean checkElevator) {
        this.checkElevator = checkElevator;
    }

    public String getCompanyPublishId() {
        return companyPublishId;
    }

    public void setCompanyPublishId(String companyPublishId) {
        this.companyPublishId = companyPublishId;
    }
}

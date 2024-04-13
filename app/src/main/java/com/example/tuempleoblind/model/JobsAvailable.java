package com.example.tuempleoblind.model;

public class JobsAvailable {
    String title, category, salary, companyPublishId;
    Boolean checkRamp, checkElevator;
    public JobsAvailable(){}
        public JobsAvailable(String title,String category, String salary,String companyPublishId){
            this.title=title;
            this.category= category;
            this.salary=salary;
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

    public String getCompanyPublishId() {
        return companyPublishId;
    }

    public void setCompanyPublishId(String companyPublishId) {
        this.companyPublishId = companyPublishId;
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
}

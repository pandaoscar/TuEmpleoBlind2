package com.example.tuempleoblind.model;

public class JobsAvailable {
    String title;
    String category;
    String salary;
    String companyPublishId;
    String typeJob;
    String description;
    String levelEducation;
    String experienceLab;
    String location;
    String habilities;
    Boolean checkRamp;
    Boolean checkElevator;
    public JobsAvailable(){}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevelEducation() {
        return levelEducation;
    }

    public void setLevelEducation(String levelEducation) {
        this.levelEducation = levelEducation;
    }

    public String getExperienceLab() {
        return experienceLab;
    }

    public void setExperienceLab(String experienceLab) {
        this.experienceLab = experienceLab;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHabilities() {
        return habilities;
    }

    public void setHabilities(String habilities) {
        this.habilities = habilities;
    }

    public JobsAvailable(String title, String category, String salary, String companyPublishId, String typeJob,
                         String description, String levelEducation, String experienceLab, String location,
                         String habilities){
            this.title=title;
            this.category= category;
            this.salary=salary;
            this.companyPublishId=companyPublishId;
            this.typeJob=typeJob;
            this.description=description;
            this.levelEducation=levelEducation;
            this.experienceLab=experienceLab;
            this.location=location;
            this.habilities=habilities;

        }

    public String getTypeJob() {
        return typeJob;
    }

    public void setTypeJob(String typeJob) {
        this.typeJob = typeJob;
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

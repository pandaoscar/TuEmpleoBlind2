package com.example.tuempleoblind;

public class Job {
    private String title;
    private String category;
    private String location;
    private String salary;
    private boolean checkElevator;
    private boolean checkRamp;

    public Job(String title, String category, String location, String salary, boolean checkElevator, boolean checkRamp) {
        this.title = title;
        this.category = category;
        this.location = location;
        this.salary = salary;
        this.checkElevator = checkElevator;
        this.checkRamp = checkRamp;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public String getSalary() {
        return salary;
    }

    public boolean hasElevator() {
        return checkElevator;
    }

    public boolean hasRamp() {
        return checkRamp;
    }
}

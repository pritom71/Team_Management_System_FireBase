package com.stamasoft.ptithom.firebase_all_libraries;

public class Employee {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String designation;
    private String gender;
    private String bloodGroup;
    private String imageUrl; // New field for storing the image URL

    public Employee() {
        // Default constructor required for calls to DataSnapshot.getValue(Employee.class)
    }

    public Employee(String id, String name, String email, String phone, String designation, String gender, String bloodGroup, String imageUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.designation = designation;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.imageUrl = imageUrl; // Assigning the image URL
    }

    // Getters and Setters for all fields

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
package com.mavi.wishlist.model;

import java.util.Objects;

public class User {

    private Integer id;
    private String firstName;
    private String lastName;
    private String mail;
    private String password;

    //Constructors

    //getters & setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String passwordHash) {
        this.password = passwordHash;
    }

    //Equals and HashCode methods to reliably use Maps, HashMaps etc and prevent duplications
    //Ensures that two objects referencing the same database entry can be identified preventing duplicate memory should the both be loaded.
    //Allow us to compare objects during integration tests for better test cases.
    @Override
    public boolean equals(Object o){
        if (this == o) return true;

        if(o == null || getClass() != o.getClass()) return false;
        User that = (User) o;

        //Checks for persistent objects
        if(this.getId() != null && that.getId() != null) {
           return this.getId().equals(that.getId());
        }

        //Compares new objects without IDs
        return Objects.equals(getMail(), that.getMail()) && Objects.equals(getPassword(), that.getPassword());
    }

    @Override
    public int hashCode(){
        Integer userID = getId();
        return userID != null ? userID.hashCode() : 0;
    }
}

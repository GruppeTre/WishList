package com.mavi.wishlist.model;

public class User {

    private Integer id;
    private String firstName;
    private String lastName;
    private String mail;
    private String passwordHash;

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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }


    //Equals and HashCode methods to reliably use Maps, HashMaps etc and prevent duplications
    //Ensures that two objects referencing the same database entry can be identified preventing duplicate memory should the both be loaded.
    //Allow us to compare objects during integration tests for better test cases.
    @Override
    public boolean equals(Object o){
        if (this == o) return true;

        if(o == null || getClass() != o.getClass()) return false;
        User that = (User) o;

        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode(){
        Integer userID = getId();
        return userID != null ? userID.hashCode() : 0;
    }
}

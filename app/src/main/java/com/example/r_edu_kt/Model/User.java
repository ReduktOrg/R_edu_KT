package com.example.r_edu_kt.Model;

public class User {
    private String date,email,fullName,gender,profileimage,password,userName,phoneNo,userid;
    public User(){
    }

    public User(String date, String email, String fullName, String gender, String profileimage, String password, String userName, String phoneNo, String userid) {
        this.date = date;
        this.email = email;
        this.fullName = fullName;
        this.gender = gender;
        this.profileimage = profileimage;
        this.password = password;
        this.userName = userName;
        this.phoneNo = phoneNo;
        this.userid = userid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
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

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}

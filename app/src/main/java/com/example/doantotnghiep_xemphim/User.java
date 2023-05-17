package com.example.doantotnghiep_xemphim;

public class User {
    public String name, email, birthday, joindate, avatar;
    public int Admin;

    public User(){

    }

    public User(String name, String email, String birthday, String joindate, int Admin, String avatar) {
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.joindate = joindate;
        this.Admin = Admin;
        this.avatar = avatar;
    }
}

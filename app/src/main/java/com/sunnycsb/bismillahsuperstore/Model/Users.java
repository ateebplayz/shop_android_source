package com.sunnycsb.bismillahsuperstore.Model;

public class Users {
    private String name, password, phone;

    public Users(String name, String password, String phone) {
        this.name = name;
        this.password = password;
        this.phone = phone;
    }

    public Users() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

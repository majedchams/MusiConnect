package com.example.user.register_majed;

/**
 * blog class
 */

public class Blog {
    private String title;
    private String desc;
    private String image;
    private String username;
    private String profile_image;
    private String uid;
    private String profilepic;
    private String location;
    private String phone;
    private String price;
    private String date;
//constructor
    public Blog(){

    }


    public Blog(String image, String desc, String title, String username, String profile_image, String uid, String profilepic, String location, String phone,String price, String date) {
        this.image = image;
        this.desc = desc;
        this.title = title;
        this.username = username;
        this.profile_image = profile_image;
        this.uid = uid;
        this.profilepic = profilepic;
        this.location = location;
        this.phone = phone;
        this.price = price;
        this.date= date;
    }
    //getters
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getlocation() {
        return location;
    }

    public void setlocation(String location) {
        this.location = location;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }




    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setUserimage(String image) {
        this.image = image;
    }


    public String getUsername() {
        return username;
    }
}

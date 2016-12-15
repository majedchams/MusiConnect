package com.example.user.register_majed;

/**
 * Created by majed on 22-Nov-16. event
 */

public class event {
    private String name;
    private String desc;
    private String username;
    private String uid;
    private String location;
    private String startDate;
    private String endDAte;
    private String startTime;
    private String endTime;
    private String HostedBy;
    //constructor
    public event(){

    }

    public event(String name, String desc,String HostedBy,  String username, String uid, String location, String startDate,String endDAte, String startTime, String endTime) {

        this.name = name;
        this.desc = desc;
        this.username = username;
        this.uid = uid;
        this.location = location;
        this.startDate = startDate;
        this.endDAte = endDAte;
        this.endTime = endTime;
        this.startTime = startTime;
        this.HostedBy = HostedBy;

    }

    public String getHostedBy() {
        return HostedBy;
    }
    public void setHostedBy(String HostedBy ) {
        this.HostedBy = HostedBy;
    }



    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getstartDate() {
        return startDate;
    }
    public void setstartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getstartTime() {
        return startTime;
    }
    public void setstartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setendDate(String endDate) {
        this.endDAte = endDate;
    }
    public String getendDate() {
        return endDAte;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public String getEndTime() {
        return endTime;
    }


    public String getlocation() {
        return location;
    }
    public void setlocation(String location) {
        this.location = location;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }



}


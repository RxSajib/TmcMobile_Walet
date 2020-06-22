package com.tmc.tmcmobilewallet;

public class ChatModal {

    String location, name, usercoin;

    public ChatModal(){

    }

    public ChatModal(String location, String name, String usercoin) {
        this.location = location;
        this.name = name;
        this.usercoin = usercoin;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsercoin() {
        return usercoin;
    }

    public void setUsercoin(String usercoin) {
        this.usercoin = usercoin;
    }
}

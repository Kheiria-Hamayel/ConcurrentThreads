package com.example.project2.model;

import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

public class VirtualServer {
private  ServerPool serverPool;
ArrayList<UserRequest> userRequests = new ArrayList<>();


    public VirtualServer(ServerPool serverPool) {
        this.serverPool = serverPool;

    }

    public ServerPool getServerPool() {
        return serverPool;
    }

    public void setServerPool(ServerPool serverPool) {
        this.serverPool = serverPool;
    }

    public ArrayList<UserRequest> getUserRequests() {
        return userRequests;
    }

    public void setUserRequests(UserRequest userRequest) {
        this.userRequests.add(userRequest);
    }

}

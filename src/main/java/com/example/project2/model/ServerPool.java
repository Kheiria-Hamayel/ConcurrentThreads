package com.example.project2.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;
import javax.persistence.*;
@Entity
public class ServerPool {

    @Id
    private int serverId;
    private int serverMeme;


    public ServerPool( int serverMeme) {

        this.serverMeme = serverMeme;
    }

    public ServerPool(int serverId, int serverMeme) {
        this.serverId = serverId;
        this.serverMeme = serverMeme;
    }

    public ServerPool() {
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getServerMeme() {
        return serverMeme;
    }

    public void setServerMeme(int serverMeme) {
        this.serverMeme = serverMeme;
    }



     public synchronized int updateMem(int amount){
         serverMeme = serverMeme - amount;

        return serverMeme;
     }

     public synchronized void askMem(){
         serverMeme = serverMeme + 100;
     }





    public   int getMemSize(){
         return serverMeme;
     }


}

package com.example.project2.Service;

import com.example.project2.model.ServerPool;
import com.example.project2.model.UserRequest;

import com.example.project2.repository.ServersPoolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ResourceManagementService {

    private int count = 1;

    @Autowired
    private ServersPoolRepository serverRepository;

    Logger logger = LoggerFactory.getLogger(Service.class);
    List<ServerPool> serverList =new ArrayList<>();
    ExecutorService executorService = Executors.newFixedThreadPool(4);

    public ResourceManagementService(ServersPoolRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @PostConstruct
    public void initial(){
        ServerPool serverPool = new ServerPool();
        serverPool.setServerMeme(40);
        serverPool.setServerId(count);
        count++;
        serverRepository.save(serverPool);
        serverList.add(serverPool);
    }


    public ServerPool find(UserRequest userRequest) {

        for (int i = 0; i < serverList.size(); i++) {
            if (userRequest.getAmount() <= serverList.get(i).getMemSize()) {

                return serverList.get(i);
            }
        }
        return null;
    }

    public void allocate(UserRequest userRequest) throws InterruptedException {
ServerPool serverPool = find(userRequest);

      //  logger.info("Getting memory value before updating" + serverPool.getMemSize());

        if ( serverPool!= null) {
            executorService.submit(() -> {
                int val = serverPool.updateMem(userRequest.getAmount());
                serverRepository.save(serverPool);
                logger.info("from executor" + serverPool.getMemSize());
            });
        }
        else synchronized (this){
            if(  find(userRequest) == null) {
                Thread.sleep(10000);
                ServerPool serverPool2 = new ServerPool(count,50);
                serverList.add(serverPool2);
                serverRepository.save(serverPool2);
                count++;

                logger.info("from else section ");
                allocate(userRequest);
            }
            else if( find(userRequest)!= null) {
                logger.info("Testing ////purpose2");
                allocate(userRequest);
            }
        }


    }
}
/*

    public  void allocate (UserRequest userRequest) throws InterruptedException {
        logger.info("Getting memory value before updating" + serverPool.getMemSize());
        if( userRequest.getAmount()<= serverPool.getMemSize()) {

             executorService.submit(() -> {
                int val =  serverPool.updateMem(userRequest.getAmount());
                logger.info("from executor" + serverPool.getMemSize());
            });
        }
        else synchronized (this){
            if( userRequest.getAmount() > serverPool.getMemSize()) {
                Thread.sleep(10000);

                serverPool.askMem();
                logger.info("from else section ");
                allocate(userRequest);
            }
            else if( userRequest.getAmount()<= serverPool.getMemSize()) {
                logger.info("Testing ////purpose2");
                allocate(userRequest);
            }
        }

    }
 */
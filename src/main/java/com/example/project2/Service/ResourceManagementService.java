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
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ResourceManagementService {
    Logger logger = LoggerFactory.getLogger(Service.class);
    List<ServerPool> serverList = new ArrayList<>();
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    private ReentrantLock lock = new ReentrantLock();
    private int count = 1;
    @Autowired
    private ServersPoolRepository serverRepository;

    public ResourceManagementService(ServersPoolRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @PostConstruct
    public void initial() {
        ServerPool serverPool = new ServerPool();
        serverPool.setServerMeme(50);
        serverPool.setServerId(count);
        count++;
        serverRepository.save(serverPool);
        serverList.add(serverPool);
    }

    public ServerPool reCheck(UserRequest userRequest) {

        for (int i = 0; i < serverList.size(); i++) {
            if (userRequest.getAmount() <= serverList.get(i).getMemSize()) {

                return serverList.get(i);
            }
        }
        return null;
    }


    public void allocate(UserRequest userRequest) {
        executorService.submit(() -> {
            lock.lock();
            ServerPool serverPool = reCheck(userRequest);
            if (serverPool != null) {
                int val = serverPool.updateMem(userRequest.getAmount());
                serverRepository.save(serverPool);
                logger.info("from executor" + serverPool.getMemSize());
            } else {
                if (reCheck(userRequest) == null && userRequest.getAmount() <= 50) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ServerPool serverPool2 = new ServerPool(count, 50);
                    serverList.add(serverPool2);
                    serverRepository.save(serverPool2);
                    count++;
                    logger.info("from else section ");
                    allocate(userRequest);
                } else if (reCheck(userRequest) != null) {
                    logger.info("Testing ////purpose2");
                    allocate(userRequest);
                }
            }
            lock.unlock();
        });
    }
}

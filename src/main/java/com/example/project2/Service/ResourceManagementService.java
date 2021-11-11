package com.example.project2.Service;

import com.example.project2.model.ServerPool;
import com.example.project2.model.UserRequest;

import com.example.project2.model.VirtualServer;
import com.example.project2.repository.ServersPoolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ResourceManagementService {
    Logger logger = LoggerFactory.getLogger(Service.class);
    List<ServerPool> serverList = new ArrayList<>();
    List<VirtualServer> virtualServerArrayList = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private Condition condition2 = lock.newCondition();
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

    // searching inside the virtual server
    public VirtualServer VirReCheck(UserRequest userRequest) {
        if (virtualServerArrayList.isEmpty())
            return null;
        for (int i = 0; i < virtualServerArrayList.size(); i++) {
            if (userRequest.getAmount() <= virtualServerArrayList.get(i).getServerPool().getMemSize()) {

                return virtualServerArrayList.get(i);
            }
        }
        return null;
    }

    public ServerPool reCheck(UserRequest userRequest) {

        for (int i = 0; i < serverList.size(); i++) {
            if (userRequest.getAmount() <= serverList.get(i).getMemSize()) {

                return serverList.get(i);
            }
        }
        return null;
    }
    // allocation from the real servers
    public void consume(UserRequest userRequest, ServerPool serverPool) {
        if (serverPool != null) {
            serverPool.updateMem(userRequest.getAmount());
            serverPool.setUserRequests(userRequest);
            serverRepository.save(serverPool);
            logger.info("case 1 : enough space + list size " + serverPool.getUserRequests().size());
            lock.unlock();
        }
    }

    public void allocate(UserRequest userRequest) throws InterruptedException {
        lock.lock();
        logger.info("the thread entered");
        ServerPool serverPool = reCheck(userRequest);
        consume(userRequest, serverPool);
        VirtualServer virServerPool1 = VirReCheck(userRequest);
        if (serverPool == null && virServerPool1 == null) {    // no space neither in real list nor in the virtual one
            ServerPool server = new ServerPool(count, 50);
            server.updateMem(userRequest.getAmount());
            server.setUserRequests(userRequest);
            virtualServerArrayList.add(new VirtualServer(server));
            logger.info("case 2 : no enough space neither in real list nor in the virtual one ");
            count++;
            condition.await(10000, TimeUnit.MILLISECONDS);
            condition2.signalAll();
            logger.info("lock released after 10 sec");
            lock.unlock();
            logger.info("lock released");
            serverList.add(server); // the real list
            serverRepository.save(server); // into database // the request wil l served while in the virtual
            logger.info("Server arraylist size " + serverList.size()); // and then moved into database after serving
            logger.info("virtual arraylist size " + virtualServerArrayList.size());
        }
        if (virServerPool1 != null && serverPool == null) {
            if (userRequest.getAmount() <= virServerPool1.getServerPool().getMemSize()) {
                virServerPool1.getServerPool().updateMem(userRequest.getAmount());
                virServerPool1.getServerPool().setUserRequests(userRequest);
                logger.info("the number of unserved threads is " + virServerPool1.getServerPool().getUserRequests().size());
            }
            condition2.await();
            lock.unlock();
        }
        logger.info("the thread passed");
    }
}
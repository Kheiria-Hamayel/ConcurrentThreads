package com.example.project2.cotroller;


import com.example.project2.Service.ResourceManagementService;
import com.example.project2.model.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    public final ResourceManagementService resourceManagementService;

    public Controller(ResourceManagementService resourceManagementService) {
       this.resourceManagementService = resourceManagementService;
    }

    @GetMapping("/allocation")
    public void allocate(@RequestBody UserRequest userRequest) throws InterruptedException {
      //  Thread resourceManagementService = new Thread(new ResourceManagementService());
      //  resourceManagementService.start();
        resourceManagementService.allocate(userRequest);

    }



}

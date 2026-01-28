package com.example.service.vet;

import com.example.service.adoptions.DogAdoptedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class Dogtor {

    @ApplicationModuleListener
    void checkup (DogAdoptedEvent event) {
        IO.println("checking up on " +event);
    }
}

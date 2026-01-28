package com.example.adoptions.adoptions;

import com.example.adoptions.adoptions.validation.Validation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@Transactional
class AdoptionsController {

    private final Validation validation ;
    private final DogRepository repository;
    private final ApplicationEventPublisher applicationEventPublisher;

    AdoptionsController(Validation validation, DogRepository repository, ApplicationEventPublisher applicationEventPublisher) {
        this.validation = validation;
        this.repository = repository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @PostMapping("/dogs/{dogId}/adoptions")
    void adopt(@PathVariable int dogId, @RequestParam String owner) {
        this.repository.findById(dogId).ifPresent(dog -> {
            var updated = this.repository.save(
                    new Dog(dog.id(), dog.name(), owner, dog.description()));
            IO.println("adopted " + updated);
            this.applicationEventPublisher.publishEvent(
                    new DogAdoptedEvent(dogId));
        });
    }

}

interface DogRepository extends ListCrudRepository<Dog, Integer> {
}

// look mom, no Lombok!
record Dog(@Id int id, String name, String owner, String description) {
}

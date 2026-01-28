package com.example.service.cats;

import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.registry.ImportHttpServices;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@EnableResilientMethods
@ResponseBody
@ImportHttpServices(CatClient.class)
class CatController {

    private final CatClient client;

    private final AtomicInteger counter = new AtomicInteger();

    CatController(CatClient client) {
        this.client = client;
    }

    @Retryable(maxRetries = 4, includes = IllegalStateException.class)
    @GetMapping("/cats/facts")
    CatFacts facts() {
        if (this.counter.incrementAndGet() < 4) {
            IO.println("incrementing!");
            throw new IllegalStateException("oops!");
        }
        IO.println("facts");
        return client.facts();
    }
}


interface CatClient {

    @GetExchange("https://www.catfacts.net/api")
    CatFacts facts();

}

record CatFacts(Collection<CatFact> facts) {
}

record CatFact(String fact) {
}
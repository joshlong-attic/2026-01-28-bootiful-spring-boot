package com.example.adoptions.cats;


import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.registry.ImportHttpServices;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

@EnableResilientMethods
@ImportHttpServices(CatFactsClient.class)
@Controller
@ResponseBody
class CatFactsController {

    private final CatFactsClient client;

    private final AtomicInteger counter = new AtomicInteger(0);

    CatFactsController(CatFactsClient client) {
        this.client = client;
    }

    @ConcurrencyLimit(10)
    @Retryable(maxRetries = 4, includes = IllegalStateException.class)
    @GetMapping("/cats/facts")
    CatFacts facts() {
        if (this.counter.incrementAndGet() < 4) {
            IO.println("oops!");
            throw new IllegalStateException("Simulated error");
        }
        IO.println("facts!");
        return this.client.facts();
    }
}

// https://www.catfacts.net/api

record CatFact(String fact) {
}

record CatFacts(Collection<CatFact> facts) {
}


interface CatFactsClient {

    @GetExchange("https://www.catfacts.net/api")
    CatFacts facts();
}
/*

@Component
class CatFactsClient {

    private final RestClient http;

    CatFactsClient(RestClient.Builder http) {
        this.http = http.build();
    }

    CatFacts facts() {
        return this.http
                .get()
                .uri("https://www.catfacts.net/api")
                .retrieve()
                .body(CatFacts.class);

    }

}*/

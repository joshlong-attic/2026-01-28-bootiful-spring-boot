package com.example.adoptions.cats;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@EnableResilientMethods
@ImportHttpServices(value = CatFactClient.class)
class CatFactsConfiguration {



}

@Controller
@ResponseBody
class CatFactsController {

    private final CatFactClient catFactClient;

    private final AtomicInteger counter = new AtomicInteger();

    CatFactsController(CatFactClient catFactClient) {
        this.catFactClient = catFactClient;
    }

    @ConcurrencyLimit(10)
    @Retryable(maxRetries = 4, includes = IllegalStateException.class)
    @GetMapping("/cats/facts")
    CatFacts facts() {
      /*  if (this.counter.incrementAndGet() < 4) {
            IO.println("simulating failure");
            throw new IllegalStateException("simulated failure");
        }*/
        IO.println("facts!");
        return catFactClient.facts();
    }
}

interface CatFactClient {

    @GetExchange("https://www.catfacts.net/api")
    CatFacts facts();

}

record CatFact(String fact) {
}

record CatFacts(Collection<CatFact> facts) {
}
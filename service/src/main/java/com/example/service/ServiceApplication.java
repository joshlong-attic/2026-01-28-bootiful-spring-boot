package com.example.service;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.BeanRegistrar;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Import(RunnerBeanRegistrar.class)
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}


class RunnerBeanRegistrar implements BeanRegistrar {

    @Override
    public void register(@NonNull BeanRegistry registry, @NonNull Environment env) {
        for (var i = 0; i < 10; i++) {
            var ctr = i;
            registry.registerBean(Runner.class, spec -> spec
                    .supplier(_ -> new Runner("hello, #" + ctr)));
        }
    }
}

class Runner implements ApplicationRunner {

    private final String message;

    Runner(String message) {
        this.message = message;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        IO.println(">" + message + "<");
    }
}
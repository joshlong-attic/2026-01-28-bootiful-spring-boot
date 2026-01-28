package com.example.adoptions;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.BeanRegistrar;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

//@Import(RunnerBeanRegistrar.class)
@SpringBootApplication
public class AdoptionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdoptionsApplication.class, args);
    }

}


class RunnerBeanRegistrar implements BeanRegistrar {
    // JSpecify

    @Override
    public void register(@NonNull BeanRegistry registry,
                         @NonNull Environment env) {

        class Foo {
        }
        registry.registerBean(Foo.class);

        for (var i = 0; i < 10; i++) {
            var ctr = i;
            registry.registerBean(Runner.class, spec -> spec
                    .supplier(supplierContext -> new Runner(ctr)));
        }


    }
}

class Runner implements ApplicationRunner {

    private final int count;

    Runner(int count) {
        this.count = count;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        IO.println("hi," + this.count + ", from the runner");
    }
}

// BeanFactoryPostProcessor
// component scanning
// java config
// BeanRegistrar
/*
@Configuration
class MyConfig {

    @Bean
    ApplicationRunner runner() {
        return args -> IO.println("hi from the runner");
    }
}

//  UML "stereotype"
@TorontoService
class Foo {

    Foo() {
        IO.println("hi");
    }
}*/


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@interface TorontoService {
    @AliasFor(
            annotation = Component.class
    )
    String value() default "";
}

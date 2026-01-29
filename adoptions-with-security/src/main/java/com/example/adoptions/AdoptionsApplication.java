package com.example.adoptions;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.BeanRegistrar;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jdbc.core.dialect.JdbcPostgresDialect;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

@EnableMultiFactorAuthentication(authorities = {
        FactorGrantedAuthority.PASSWORD_AUTHORITY,
        FactorGrantedAuthority.OTT_AUTHORITY
})
@SpringBootApplication
public class AdoptionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdoptionsApplication.class, args);
    }

/*	@Scheduled (cron = "* * * * * *")
	void youIncompleteMeRunner (){
		return a -> {
			eventPublications
					.resubmitIncompletePublications( e -> true);
		} ;
	}*/

    @Bean
    JdbcPostgresDialect jdbcPostgresDialect() {
        return JdbcPostgresDialect.INSTANCE;
    }

}

class MyBeanRegistrar implements BeanRegistrar {

    @Override
    public void register(@NonNull BeanRegistry registry, @NonNull Environment env) {

        for (var i = 0; i < 10; i++) {
            var ctr = i;
            registry.registerBean(MyComponent.class, spc -> spc
                    .order(ctr)
                    .description("the description")
                    .supplier(supplierContext -> new MyComponent(ctr)));
        }
    }
}

//@Import(MyBeanRegistrar.class)
@Configuration
class SecurityConfiguration {

    @Bean
    JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        var jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setEnableUpdatePassword(true);
        return jdbcUserDetailsManager;
    }


    @Bean
    Customizer<HttpSecurity> httpSecurityCustomizer() {
        return http -> http
                .webAuthn(a -> a
                        .rpId("localhost")
                        .rpName("bootiful")
                        .allowedOrigins("http://localhost:8080")
                )
                .oneTimeTokenLogin(
                        ott -> ott
                                .tokenGenerationSuccessHandler((request, response, oneTimeToken) -> {

                                    response.getWriter().println("you've got console mail!");
                                    response.setContentType(MediaType.TEXT_PLAIN_VALUE);

                                    IO.println("please go to http://localhost:8080/login/ott?token=" +
                                            oneTimeToken.getTokenValue());
                                })
                );
    }


}


class MyComponent {

    MyComponent(int counter) {
        IO.println("my component created, no.#" + counter);
    }
}
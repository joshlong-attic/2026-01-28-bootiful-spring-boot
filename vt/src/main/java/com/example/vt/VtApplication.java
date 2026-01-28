package com.example.vt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class VtApplication {

    public static void main(String[] args) {
        SpringApplication.run(VtApplication.class, args);
    }

}

@Controller
@ResponseBody
class VirtualThreadsController {

    private final RestClient http;

    VirtualThreadsController(RestClient.Builder http) {
        this.http = http.build();
    }

    @GetMapping("/delay")
    String delay() {
        var msg = Thread.currentThread() + ":";
        var r = this.http.get()
                .uri("http://localhost:80/delay/5")
                .retrieve()
                .body(String.class);
        msg += Thread.currentThread();
        IO.println(msg);
        return r;

    }
}

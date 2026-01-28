package com.example.scheduler;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
public class SchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulerApplication.class, args);
	}

}

@Service
class DogAdoptionScheduler {

	@McpTool(description = """
            provide a time for an appointment to pick up or adopt a dog from a Pooch Palace location
            """)
	String schedule(@McpToolParam(description = "the id of the dog") int dogId,
					@McpToolParam(description = "the name of the dog") String dogName) {
		var i = Instant
				.now()
				.plus(3, ChronoUnit.DAYS)
				.toString();
		IO.println("scheduling " + dogId +'/' +dogName +
				" for " + i);
		return i;
	}
}

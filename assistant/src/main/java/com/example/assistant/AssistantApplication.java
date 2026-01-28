package com.example.assistant;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.dialect.JdbcPostgresDialect;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.util.List;

@SpringBootApplication
public class AssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssistantApplication.class, args);
    }

    @Bean
    JdbcPostgresDialect jdbcPostgresDialect (){
        return JdbcPostgresDialect.INSTANCE;
    }

    @Bean
    QuestionAnswerAdvisor questionAnswerAdvisor(VectorStore vector) {
        return QuestionAnswerAdvisor.builder(vector).build();
    }

    @Bean
    PromptChatMemoryAdvisor promptChatMemoryAdvisor(DataSource dataSource) {
        var jdbc = JdbcChatMemoryRepository
                .builder()
                .dataSource(dataSource)
                .build();
        var cm = MessageWindowChatMemory
                .builder()
                .chatMemoryRepository(jdbc)
                .build();
        return PromptChatMemoryAdvisor.builder(cm).build();
    }
}


interface DogRepository extends ListCrudRepository<Dog, Integer> {
}

record Dog(@Id int id, String name, String description) {
}

@Controller
@ResponseBody
class AssistantController {

    private final ChatClient ai;

    AssistantController(PromptChatMemoryAdvisor promptChatMemoryAdvisor,
                        QuestionAnswerAdvisor questionAnswerAdvisor,
                        DogRepository repository, VectorStore vectorStore,
                        List<McpSyncClient> mcpSyncClients,
                        ChatClient.Builder ai) {
        if (false) {
            repository.findAll().forEach(dog -> {
                var dogument = new Document("id: %s, name: %s, description: %s".formatted(
                        dog.id(), dog.name(), dog.description()
                ));
                vectorStore.add(List.of(dogument));
            });
        }

        this.ai = ai
                .defaultToolCallbacks(SyncMcpToolCallbackProvider.syncToolCallbacks(mcpSyncClients))
                .defaultAdvisors(promptChatMemoryAdvisor, questionAnswerAdvisor)
                .defaultSystem("""
                        
                            You are an AI powered assistant to help people adopt a dog from the adoptions agency named 
                            Pooch Palace with locations in Toronto, Seoul, Tokyo, Singapore, Paris, Mumbai, New Delhi, 
                            Barcelona, San Francisco, and London. Information about the dogs availables will be presented below. 
                            If there is no information, then return a polite response suggesting we don't have any dogs available.
                        
                        If somebody asks for a time to pick up the dog, don't ask other questions: simply provide a time by 
                        consulting the tools you have available.
                        
                        
                        """)
                .build();
    }

    @GetMapping("/{user}/ask")
    String ask(@PathVariable String user,
               @RequestParam String question) {
        return this.ai
                .prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, user))
                .user(question)
                .call()
                .content();

    }
}


record DogAdoptionSuggestion(String id, String description, String name) {
}
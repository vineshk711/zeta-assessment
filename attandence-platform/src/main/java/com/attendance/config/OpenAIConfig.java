package com.attendance.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.theokanning.openai.service.OpenAiService;
import java.time.Duration;

@Configuration
public class OpenAIConfig {

    @Value("${openai.api-key}")
    private String openaiApiKey;

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(openaiApiKey, Duration.ofSeconds(30));
    }
}
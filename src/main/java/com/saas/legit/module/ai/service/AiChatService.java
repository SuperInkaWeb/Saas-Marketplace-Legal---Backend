package com.saas.legit.module.ai.service;

import com.saas.legit.module.ai.model.AiChatMessage;
import com.saas.legit.module.ai.model.AiMessageRole;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiChatService {

    @Value("${AI.GROQ_API_KEY}")
    private String openAiApiKey;

    private ChatLanguageModel chatModel;

    @PostConstruct
    public void init() {
        this.chatModel = OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .apiKey(openAiApiKey)
                .modelName("llama-3.3-70b-versatile")
                .temperature(0.0)
                .build();
    }

    public String chatWithAi(List<AiChatMessage> history, String newUserMessage) {
        List<ChatMessage> messages = new ArrayList<>();
        
        // System Prompt para el Asistente Jurídico
        String systemPrompt = "Eres un Asistente Jurídico de alto nivel. Tus funciones principales son:\n" +
                "1. Resolver consultas legales.\n" +
                "2. Facilitar la investigación legal en base a Leyes, Jurisprudencia y Doctrina.\n" +
                "Responde con precisión profesional, tono formal, y apóyate en el conocimiento jurídico.";
        
        messages.add(new SystemMessage(systemPrompt));

        // Load history
        for (AiChatMessage msg : history) {
            if (msg.getRole() == AiMessageRole.USER) {
                messages.add(new UserMessage(msg.getContent()));
            } else if (msg.getRole() == AiMessageRole.ASSISTANT) {
                messages.add(new AiMessage(msg.getContent()));
            }
        }

        // Add current message
        messages.add(new UserMessage(newUserMessage));

        // Get AI Response
        Response<AiMessage> response = chatModel.generate(messages);
        
        return response.content().text();
    }
}

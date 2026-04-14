package com.saas.legit.module.ai.service;

import com.saas.legit.module.ai.model.AiChatMessage;
import com.saas.legit.module.ai.model.AiChatSession;
import com.saas.legit.module.ai.model.AiMessageRole;
import com.saas.legit.module.identity.model.User;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .modelName("llama-3.3-70b-versatile") // Modelo veloz y actualizado de Groq
                .temperature(0.0) // Reduce radicalmente las "alucinaciones" (invención de datos)
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
                messages.add(new dev.langchain4j.data.message.UserMessage(msg.getContent()));
            } else if (msg.getRole() == AiMessageRole.ASSISTANT) {
                messages.add(new AiMessage(msg.getContent()));
            }
        }

        // Add current message
        messages.add(new dev.langchain4j.data.message.UserMessage(newUserMessage));

        // Get AI Response
        dev.langchain4j.model.output.Response<AiMessage> response = chatModel.generate(messages);
        
        return response.content().text();
    }
}

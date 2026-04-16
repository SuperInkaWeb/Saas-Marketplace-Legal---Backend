package com.saas.legit.module.ai.controller;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.ai.dto.ChatRequest;
import com.saas.legit.module.ai.dto.ChatResponse;
import com.saas.legit.module.ai.dto.DocumentAnalysisResponse;
import com.saas.legit.module.ai.model.AiChatMessage;
import com.saas.legit.module.ai.model.AiChatSession;
import com.saas.legit.module.ai.model.AiMessageRole;
import com.saas.legit.module.ai.repository.AiChatMessageRepository;
import com.saas.legit.module.ai.repository.AiChatSessionRepository;
import com.saas.legit.module.ai.service.AiChatService;
import com.saas.legit.module.ai.service.DocumentAnalysisService;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiChatService aiChatService;
    private final DocumentAnalysisService documentAnalysisService;
    private final AiChatSessionRepository sessionRepository;
    private final AiChatMessageRepository messageRepository;
    private final UserRepository userRepository;

    @PostMapping("/chat")
    @Transactional
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request, Authentication authentication) {
        // En un caso real, validaríamos aquí que el usuario tiene suscripción.
        // Lo haremos a través de un interceptor o anotación en el siguiente paso.

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AiChatSession session;
        List<AiChatMessage> history = Collections.emptyList();

        if (request.getSessionPublicId() != null && !request.getSessionPublicId().isEmpty()) {
            UUID sessionId = UUID.fromString(request.getSessionPublicId());
            session = sessionRepository.findByPublicId(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Chat session not found"));
            history = messageRepository.findBySessionIdOrderByCreatedAtAsc(session.getId());
        } else {
            session = new AiChatSession();
            session.setUser(user);
            session.setTitle("Nueva Consulta Legal"); // Idealmente generado por IA
            session = sessionRepository.save(session);
        }

        // Save User Message
        AiChatMessage userMessage = new AiChatMessage();
        userMessage.setSession(session);
        userMessage.setRole(AiMessageRole.USER);
        userMessage.setContent(request.getMessage());
        messageRepository.save(userMessage);

        // Call AI
        String aiResponseText = aiChatService.chatWithAi(history, request.getMessage());

        // Save AI Response
        AiChatMessage aiMessage = new AiChatMessage();
        aiMessage.setSession(session);
        aiMessage.setRole(AiMessageRole.ASSISTANT);
        aiMessage.setContent(aiResponseText);
        messageRepository.save(aiMessage);

        return ResponseEntity.ok(new ChatResponse(session.getPublicId().toString(), aiResponseText));
    }

    @PostMapping("/analyze-document")
    public ResponseEntity<DocumentAnalysisResponse> analyzeDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "prompt", required = false) String prompt) {
        try {
            String analysisResult = documentAnalysisService.analyzeDocument(file, prompt);
            return ResponseEntity.ok(new DocumentAnalysisResponse(analysisResult));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new DocumentAnalysisResponse("Error analyzing document: " + e.getMessage()));
        }
    }
}

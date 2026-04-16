package com.saas.legit.module.ai.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class DocumentAnalysisService {

    @Value("${GROQ_API_KEY}")
    private String openAiApiKey;

    private ChatLanguageModel chatModel;
    private ApachePdfBoxDocumentParser pdfParser;

    @PostConstruct
    public void init() {
        this.chatModel = OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .apiKey(openAiApiKey)
                .modelName("llama-3.3-70b-versatile")
                .temperature(0.0)
                .build();
        this.pdfParser = new ApachePdfBoxDocumentParser();
    }

    public String analyzeDocument(MultipartFile file, String userPrompt) throws Exception {
        // Extraer texto del documento PDF
        String docText = "";
        try (InputStream is = file.getInputStream()) {
            Document parsedDoc = pdfParser.parse(is);
            docText = parsedDoc.text();
        }

        // Limit the text size for MVP to avoid exceeding token limits easily
        if (docText.length() > 20000) {
            docText = docText.substring(0, 20000);
        }

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Actúa como un Asistente Jurídico experto de nivel senior.\n")
                .append("Tu tarea es analizar el documento legal adjunto.\n\n");

        if (userPrompt != null && !userPrompt.isBlank()) {
            promptBuilder.append("Instrucciones específicas del usuario:\n")
                    .append(userPrompt).append("\n\n")
                    .append("Por favor, prioriza responder a las instrucciones del usuario, pero mantén un formato estructurado y profesional en Markdown.\n\n");
        } else {
            promptBuilder.append("Directrices del análisis estándar:\n")
                    .append("1. Resumen principal: Redacta un párrafo claro, objetivo y conciso sobre el propósito del documento.\n")
                    .append("2. Cláusulas de riesgo: Identifica y enumera contingencias, penalidades, obligaciones financieras o términos ambiguos.\n")
                    .append("3. Recomendaciones: Proporciona pasos accionables a nivel legal para mitigar los riesgos encontrados.\n\n");
        }

        promptBuilder.append("Reglas estrictas:\n")
                .append("- Si el texto proporcionado no es un documento legal o es ilegible, infórmalo cortésmente.\n")
                .append("- Mantén un tono profesional, pero comprensible.\n")
                .append("- Responde utilizando formato Markdown enriquecido (negritas, listas, subtítulos).\n")
                .append("- NO devuelvas JSON. Responde con texto directo y bien estructurado.\n\n")
                .append("Documento:\n").append(docText);

        return chatModel.generate(promptBuilder.toString());
    }
}

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

    public String analyzeDocument(MultipartFile file) throws Exception {
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

        String prompt = "Actúa como un Asistente Jurídico experto.\n" +
                "A continuación se adjunta el contenido de un documento legal. \n" +
                "Realiza un análisis detallado que incluya:\n" +
                "- Resumen principal del documento.\n" +
                "- Identificación de cláusulas de riesgo o compromisos.\n" +
                "- Recomendaciones a nivel legal.\n\n" +
                "Documento:\n" + docText;

        return chatModel.generate(prompt);
    }
}

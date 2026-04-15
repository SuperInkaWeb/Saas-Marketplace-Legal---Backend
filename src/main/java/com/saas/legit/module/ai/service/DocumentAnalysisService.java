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

        String prompt = "Actúa como un Asistente Jurídico experto de nivel senior.\n" +
                "Tu tarea es analizar el documento legal adjunto y extraer la información clave.\n\n" +
                "Directrices del análisis:\n" +
                "1. Resumen principal: Redacta un párrafo claro, objetivo y conciso sobre el propósito del documento.\n" +
                "2. Cláusulas de riesgo: Identifica y enumera contingencias, penalidades, obligaciones financieras o términos ambiguos.\n" +
                "3. Recomendaciones: Proporciona pasos accionables a nivel legal para mitigar los riesgos encontrados.\n\n" +
                "Reglas estrictas:\n" +
                "- Si el texto proporcionado no es un documento legal o es ilegible, devuelve un error indicándolo en el campo 'error'.\n" +
                "- Mantén un tono profesional, pero comprensible.\n" +
                "- Responde ÚNICAMENTE con un objeto JSON válido, sin texto adicional ni bloques de código markdown, usando la siguiente estructura:\n" +
                "{\n" +
                "  \"resumen\": \"texto\",\n" +
                "  \"riesgos\": [\"riesgo 1\", \"riesgo 2\"],\n" +
                "  \"recomendaciones\": [\"rec 1\", \"rec 2\"],\n" +
                "  \"error\": null\n" +
                "}\n\n" +
                "Documento:\n" + docText;

        return chatModel.generate(prompt);
    }
}

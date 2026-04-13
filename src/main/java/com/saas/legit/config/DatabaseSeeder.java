package com.saas.legit.config;

import com.saas.legit.module.document.model.DocumentTemplate;
import com.saas.legit.module.document.repository.DocumentTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final DocumentTemplateRepository documentTemplateRepository;

    @Override
    public void run(String... args) throws Exception {
        seedDocumentTemplates();
    }

    private void seedDocumentTemplates() {
        if (documentTemplateRepository.findByCode("NDA_STANDARD").isEmpty()) {
            log.info("Seeding NDA_STANDARD template...");
            DocumentTemplate template = new DocumentTemplate();
            template.setPublicId(UUID.randomUUID());
            template.setName("Acuerdo de Confidencialidad Básico (NDA)");
            template.setCode("NDA_STANDARD");
            template.setJurisdiction("Global");
            template.setContent(
                    "CONTRATO DE CONFIDENCIALIDAD (NDA)\n\n" +
                    "Entre {{EMPRESA_A}} (en adelante, \"La Empresa\") y {{EMPRESA_B}} (en adelante, \"El Receptor\"), " +
                    "se acuerda mutuamente mantener en estricta reserva toda información calificada como confidencial.\n\n" +
                    "1. Objeto: El Receptor se compromete a no divulgar secretos comerciales de La Empresa.\n" +
                    "2. Vigencia: Este acuerdo tiene validez durante {{MESES_VIGENCIA}} meses desde su firma.\n" +
                    "3. Penalidad: Ante un incumplimiento, se fijará la sanción de {{MONTO_PENALIDAD}} USD.\n\n" +
                    "Para cualquier controversia, revisar con fecha: [COMPLETAR: FECHA_RESOLUCION_CONFLICTOS]."
            );
            documentTemplateRepository.save(template);
            log.info("NDA_STANDARD template seeded successfully.");
        }
    }
}

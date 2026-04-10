package com.saas.legit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

/**
 * Configures a standalone SpringTemplateEngine that processes
 * HTML strings from the database (not files on disk).
 * Uses SpEL (Spring Expression Language) instead of OGNL.
 */
@Configuration
public class ThymeleafDocumentConfig {

    @Bean("documentTemplateEngine")
    public SpringTemplateEngine documentTemplateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();

        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);

        engine.setTemplateResolver(resolver);
        return engine;
    }
}

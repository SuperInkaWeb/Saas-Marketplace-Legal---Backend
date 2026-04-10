package com.saas.legit.module.document.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@Slf4j
public class DocumentPdfService {

    /**
     * Generates a PDF from the already-rendered HTML content.
     * No Markdown conversion needed — the content is pure HTML from Thymeleaf.
     */
    public byte[] generatePdfFromHtml(String htmlContent, String title) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            // Wrap the rendered fragment in a full A4-branded document shell
            String fullHtml = buildBrandedHtml(htmlContent, title);

            // openhtmltopdf requires strict XHTML. Jsoup cleans and converts.
            Document doc = Jsoup.parse(fullHtml, "UTF-8");
            doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
            doc.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
            String xhtml = doc.html();

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(xhtml, "/");
            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF", e);
            throw new RuntimeException("Could not generate PDF", e);
        }
    }

    private String buildBrandedHtml(String content, String title) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8"/>
            <style>
                @page {
                    size: A4;
                    margin: 2.5cm;
                    @bottom-right {
                        content: "Página " counter(page) " de " counter(pages);
                        font-family: 'serif';
                        font-size: 9pt;
                        color: #64748b;
                    }
                    @top-left {
                        content: "AbogHub - Documento Legal";
                        font-family: 'serif';
                        font-size: 9pt;
                        color: #94a3b8;
                    }
                }
                body {
                    font-family: 'serif', 'Times New Roman', serif;
                    line-height: 1.6;
                    color: #1e293b;
                    font-size: 11pt;
                }
                .header {
                    text-align: center;
                    border-bottom: 2px solid #e2e8f0;
                    margin-bottom: 30pt;
                    padding-bottom: 10pt;
                }
                .logo {
                    font-size: 24pt;
                    font-weight: bold;
                    color: #4f46e5;
                    margin-bottom: 5pt;
                }
                .document-title {
                    font-size: 18pt;
                    font-weight: bold;
                    margin-top: 10pt;
                    text-transform: uppercase;
                }
                h1, h2, h3 { color: #0f172a; }
                p { margin-bottom: 12pt; text-align: justify; }
                .footer {
                    margin-top: 50pt;
                    font-size: 9pt;
                    color: #94a3b8;
                    text-align: center;
                    border-top: 1px solid #f1f5f9;
                    padding-top: 10pt;
                }
            </style>
        </head>
        <body>
            <div class="header">
                <div class="logo">AbogHub</div>
                <div class="document-title">%s</div>
            </div>
            
            <div class="content">
                %s
            </div>
            
            <div class="footer">
                Este documento fue generado automáticamente por la plataforma AbogHub.<br/>
                La validez legal de este borrador depende de su revisión final y firma.
            </div>
        </body>
        </html>
        """.formatted(title, content);
    }
}

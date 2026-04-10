package com.saas.legit.core.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "resource_type", "auto",
                        "access_mode", "public"
                ));
        return uploadResult.get("secure_url").toString();
    }

    public void deleteFile(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public byte[] downloadPrivateFile(String url) throws IOException {
        // Extract public ID and resource type from signature
        // Example: https://res.cloudinary.com/cloud_name/image/upload/v1/folder/file.pdf
        String[] parts = url.split("/");
        String resourceType = "image"; // default
        int uploadIndex = -1;
        
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("upload")) {
                uploadIndex = i;
                if (i > 0) resourceType = parts[i-1];
                break;
            }
        }
        
        if (uploadIndex == -1 || uploadIndex >= parts.length - 1) {
            throw new IOException("Invalid Cloudinary URL format");
        }
        
        // Extract version if present
        String version = null;
        int publicIdStart = uploadIndex + 1;
        if (parts[uploadIndex + 1].startsWith("v")) {
            version = parts[uploadIndex + 1].substring(1);
            publicIdStart = uploadIndex + 2;
        }
        
        StringBuilder publicIdBuilder = new StringBuilder();
        for (int i = publicIdStart; i < parts.length; i++) {
            if (i > publicIdStart) publicIdBuilder.append("/");
            publicIdBuilder.append(parts[i]);
        }
        
        String publicIdWithExtension = publicIdBuilder.toString();
        String extension = "";
        String publicId = publicIdWithExtension;
        
        if (publicIdWithExtension.contains(".")) {
            int lastDot = publicIdWithExtension.lastIndexOf(".");
            publicId = publicIdWithExtension.substring(0, lastDot);
            extension = publicIdWithExtension.substring(lastDot + 1);
        }

        // Generate a signed URL that exactly matches the resource (including version)
        var urlBuilder = cloudinary.url()
                .resourceType(resourceType)
                .signed(true);
        
        if (version != null) {
            urlBuilder.version(version);
        }

        String signedUrl;
        if ("image".equals(resourceType) && !extension.isEmpty()) {
            // For images/pdfs, format is passed separately to the builder
            signedUrl = urlBuilder.format(extension).generate(publicId);
        } else {
            // For raw files, the extension is part of the public ID
            signedUrl = urlBuilder.generate(publicIdWithExtension);
        }

        // Fetch using a simple URL stream
        try (java.io.InputStream is = java.net.URI.create(signedUrl).toURL().openStream()) {
            return is.readAllBytes();
        }
    }
}

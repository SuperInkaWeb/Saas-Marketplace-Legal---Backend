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
        // Files are uploaded with access_mode=public, so we can download directly.
        // Use Java's built-in HTTP client for a reliable fetch.
        try (java.io.InputStream is = java.net.URI.create(url).toURL().openStream()) {
            return is.readAllBytes();
        }
    }
}

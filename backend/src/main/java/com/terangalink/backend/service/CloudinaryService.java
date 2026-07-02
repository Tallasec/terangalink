package com.terangalink.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private static final String HOUSING_FOLDER = "terangalink/housings";

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public UploadResult uploadImage(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", HOUSING_FOLDER, "resource_type", "image")
            );
            return new UploadResult(
                    String.valueOf(result.get("public_id")),
                    String.valueOf(result.get("secure_url"))
            );
        } catch (IOException ex) {
            throw new IllegalStateException("Impossible d'uploader l'image sur Cloudinary.", ex);
        }
    }

    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException ex) {
            throw new IllegalStateException("Impossible de supprimer l'image sur Cloudinary.", ex);
        }
    }

    public record UploadResult(String publicId, String imageUrl) {
    }
}

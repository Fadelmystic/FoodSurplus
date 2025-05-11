package com.foodsurplus.listing_service.application.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageUploadService {
    private final Cloudinary cloudinary;

    public ImageUploadService() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dr9z2qewx",
            "api_key", "728125366569481",
            "api_secret", "4WO-x8b7FYUc4b9eaEsSiS1JZ_c"
        ));
    }

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("secure_url");
    }
}
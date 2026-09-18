package com.govskill.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

public class FileStorageUtil {

    private static final String UPLOAD_DIR = "uploads";

    public static String saveBase64File(String base64Data, String originalFilename) {
        if (base64Data == null || base64Data.trim().isEmpty()) {
            return "/uploads/sample_certificate.pdf";
        }

        try {
            // Strip data:application/pdf;base64, or similar prefix
            String cleanData = base64Data;
            String extension = ".pdf";
            if (cleanData.contains(",")) {
                String meta = cleanData.substring(0, cleanData.indexOf(","));
                if (meta.contains("image/png")) extension = ".png";
                else if (meta.contains("image/jpeg") || meta.contains("image/jpg")) extension = ".jpg";
                cleanData = cleanData.substring(cleanData.indexOf(",") + 1);
            } else if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            byte[] decodedBytes = Base64.getDecoder().decode(cleanData.trim());

            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filename = "cert_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            File targetFile = new File(dir, filename);

            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                fos.write(decodedBytes);
            }

            return "/" + UPLOAD_DIR + "/" + filename;
        } catch (Exception e) {
            System.err.println("Failed to save base64 file: " + e.getMessage());
            return "/uploads/sample_certificate.pdf";
        }
    }
}

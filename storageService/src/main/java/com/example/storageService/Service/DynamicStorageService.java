package com.example.storageService.Service;

import com.example.storageService.Model.FileVersion;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class DynamicStorageService implements StorageService {

    private final StorageService b2Service;
    private final StorageService r2Service;

    public DynamicStorageService(B2StorageService b2Service, R2StorageService r2Service) {
        this.b2Service = b2Service;
        this.r2Service = r2Service;
    }

    @Override
    public FileVersion uploadFile(String fileName, InputStream inputStream, long contentLength, String contentType, Long id, String context)
            throws  Exception {
//        if (contentType != null && (contentType.equals("application/pdf") || contentType.startsWith("video/"))) {
//            return r2Service.uploadFile(fileName, inputStream, contentLength, contentType, id, context);
//        } else {
//            return b2Service.uploadFile(fileName, inputStream, contentLength, contentType, id, context);
//        }
            return r2Service.uploadFile(fileName, inputStream, contentLength, contentType, id, context);
    }

    @Override
    public void deleteFile(String fileName, String fileId) throws Exception {
        // Basic rule: use extension to decide
        r2Service.deleteFile(fileName,fileId);
//        if (fileName != null && (fileName.endsWith(".pdf") || fileName.endsWith(".mp4") || fileName.endsWith(".mkv"))) {
//            r2Service.deleteFile(fileName, fileId);
//        } else {
//            b2Service.deleteFile(fileName, fileId);
//        }
    }


    @Override
    public String getAuthToken() throws Exception {
        return "";
    }

    // Implement other methods if required
}

package com.example.storageService.Service;

import com.example.storageService.Model.FileVersion;
import java.io.IOException;
import java.io.InputStream;

public interface StorageService {
    FileVersion uploadFile(String fileName, InputStream inputStream, long contentLength, String contentType, Long id, String context)
            throws IOException, Exception;
    void deleteFile(String fileName, String fileId) throws Exception;
    String getAuthToken() throws Exception;
}

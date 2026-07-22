package com.example.demo.controller;

import com.example.demo.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @PostMapping("/public/upload")
    public ResponseEntity<Map<String,String>> uploadPublic(@RequestParam("file")MultipartFile file) throws IOException {
        String staticUrl = s3Service.uploadPublicFile(file);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Arquivo público enviado com sucesso!");
        response.put("url",staticUrl);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/private/upload")
    public ResponseEntity<Map<String,String>> uploadPrivate(@RequestParam("file")MultipartFile file) throws IOException {
        String s3Key = s3Service.uploadPrivateFile(file);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Arquivo privado enviado!");
        response.put("s3Key",s3Key);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/private/view")
    public ResponseEntity<Map<String, String>> viewPrivateFile(@RequestParam("s3Key") String s3Key) {
        String presignedUrl = s3Service.generatePresignedUrl(s3Key,5);

        Map<String, String> response = new HashMap<>();
        response.put("s3Key", s3Key);
        response.put("presignedUrl", presignedUrl);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable String filename) {
        byte[] data = s3Service.dowloadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=" + filename)
                .body(data);
    }
}

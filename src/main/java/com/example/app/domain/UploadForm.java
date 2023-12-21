package com.example.app.domain;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UploadForm {
private String id;
private MultipartFile upData; 
}

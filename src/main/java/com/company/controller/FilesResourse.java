package com.company.controller;

import com.company.entity.Files;
import com.company.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/file")
public class FilesResourse {

    @Autowired
    FileService fileService;


    @Value("${upload.folder}")
    private String uploadFile;

    @PostMapping
    public ResponseEntity save(@RequestParam(value = "file") MultipartFile multipartFile){
        fileService.save(multipartFile);
        return ResponseEntity.ok(multipartFile.getOriginalFilename()+" saqlandi");
    }

    @GetMapping("/{hashId}")
    public ResponseEntity findByHashId(@PathVariable String hashId) throws MalformedURLException {
        Files f=fileService.findBYHashId(hashId);
        return  ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"inline; fileName\""
        + URLEncoder.encode(f.getName())).contentType(MediaType.parseMediaType(f.getContentType()))
                .contentLength(f.getFileSize()).body(new FileUrlResource(String.format("%s/%s",uploadFile,f.getUploadPath())));

    }

    @GetMapping("/download/{hashId}")
    public ResponseEntity download(@PathVariable String hashId) throws MalformedURLException {
        Files files=fileService.findBYHashId(hashId);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; fileName\""
        +URLEncoder.encode(files.getName())).contentType(MediaType.parseMediaType(files.getContentType()))
                .contentLength(files.getFileSize()).body(new FileUrlResource(String.format("%s/%s",uploadFile,files.getUploadPath())));
    }

    @DeleteMapping("/{hashId}")
    public ResponseEntity delete(@PathVariable String hashId){
        fileService.delete(hashId);
        return ResponseEntity.ok("file ochirildi");
    }


}

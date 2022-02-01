package com.company.service;

import com.company.entity.FileStatus;
import com.company.entity.Files;
import com.company.repository.FilesRepository;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class FileService {

    @Autowired
    FilesRepository filesRepository;
     Hashids hashids;

     @Value("${upload.folder}")
     private String uploadFile;

    public FileService() {
        this.hashids=new Hashids(getClass().getName(),6);
    }

    public void save(MultipartFile multipartFile){
        Files files=new Files();
        files.setName(multipartFile.getOriginalFilename());
        files.setContentType(multipartFile.getContentType());
        files.setExtention(getExt(multipartFile.getOriginalFilename()));
        files.setFileSize(multipartFile.getSize());
        files.setFileStatus(FileStatus.DREFT);
        filesRepository.save(files);

        String encode = hashids.encode(files.getId());
        files.setHashid(encode);
        LocalDate localDate=LocalDate.now();
        File file=new File(String.format("%s/Fayllar/%d/%d/%d",this.uploadFile,localDate.getYear(),localDate.getMonthValue()
        ,localDate.getDayOfMonth()));
        if (!file.exists()){
            file.mkdirs();
        }
           files.setUploadPath(String.format("Fayllar/%d/%d/%d/%s.%s",localDate.getYear(),localDate.getMonthValue()
                ,localDate.getDayOfMonth(),files.getHashid(),files.getExtention()));
           filesRepository.save(files);
           file=file.getAbsoluteFile();
           File file1=new File(String.format("%s/%s.%s",file,files.getHashid(),files.getExtention()));
        try {
            multipartFile.transferTo(file1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Transactional(readOnly = true)
    public Files findBYHashId(String hashId){
        Files byHashid = filesRepository.findByHashid(hashId);
        return byHashid;
    }

    public String getExt(String name){
        String replace = name.replace(".", ",");
        String[] split = replace.split(",");
        return  split[1];
    }

    public void delete(String hashId){
        Files byHashid = filesRepository.findByHashid(hashId);
        File file=new File(uploadFile,byHashid.getUploadPath());
        if (file.delete()){
            filesRepository.delete(byHashid);
        }
    }

    @Scheduled(cron = "0 2 17 * * *")
    public void deleteAllDRAFT(){
        List<Files> all = filesRepository.findAllByFileStatus(FileStatus.DREFT);
        for (Files files:all){
            delete(files.getHashid());
        }
    }

}

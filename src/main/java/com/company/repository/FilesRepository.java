package com.company.repository;

import com.company.entity.FileStatus;
import com.company.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilesRepository extends JpaRepository<Files,Long> {

     Files findByHashid(String hashId);
     List<Files>findAllByFileStatus(FileStatus fileStatus);
}

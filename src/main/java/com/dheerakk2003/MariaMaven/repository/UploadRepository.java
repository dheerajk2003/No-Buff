package com.dheerakk2003.MariaMaven.repository;

import com.dheerakk2003.MariaMaven.models.Upload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadRepository extends JpaRepository<Upload, Long> {
    Iterable<Upload> findByUserId(Long userId);
    Upload findByFilename(String filename);
}

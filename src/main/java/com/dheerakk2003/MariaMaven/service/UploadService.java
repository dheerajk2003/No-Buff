package com.dheerakk2003.MariaMaven.service;

import com.dheerakk2003.MariaMaven.models.Upload;
import com.dheerakk2003.MariaMaven.repository.UploadRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UploadService {
    private final UploadRepository ur;
    public UploadService(UploadRepository ur){
        this.ur = ur;
    }

    public Iterable<Upload> findByUserId(Long userId){
        return ur.findByUserId(userId);
    }

    public Iterable<Upload> getVids(Long id){
        return ur.findAll();
    }

    public Optional<Upload> get(Long id){
        Optional<Upload> u = ur.findById(id);
        if(u.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return u;
    }

    public Upload findByName(String filename){
        return ur.findByFilename(filename);
    }

    public void save(Upload u){
        if(u == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        ur.save(u);
    }

    public void remove(String filename){
        ur.delete(ur.findByFilename(filename));
    }
}

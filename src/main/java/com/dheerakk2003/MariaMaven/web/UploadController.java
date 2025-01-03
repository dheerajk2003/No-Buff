package com.dheerakk2003.MariaMaven.web;

import com.dheerakk2003.MariaMaven.models.Upload;
import com.dheerakk2003.MariaMaven.service.ResizeService;
import com.dheerakk2003.MariaMaven.service.UploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

@RestController
public class UploadController {

    private static final Path UploadDir = Paths.get("uploads/uploaded/");

    private final UploadService us;

    public UploadController(UploadService us){
        this.us = us;
    }

    @GetMapping("/file/{id}")
    public String get(@PathVariable Long id){
        Optional<Upload> u = us.get(id);
        return u.get().getFilename();
    }

    @PostMapping("/file")
    public ResponseEntity<String> upload(@RequestParam("chunk") MultipartFile chunk,@RequestParam("userId") Long userId, @RequestParam("filename") String filename) throws IOException {
        try{
            if(us.findByName(filename) != null){
                Upload u = new Upload(userId, filename);
                us.save(u);
            }

            Path UploadPath = UploadDir.resolve(filename);
            Files.createDirectories(UploadPath.getParent());
            Files.write(UploadPath, chunk.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return ResponseEntity.ok("Successfull");
        }
        catch (Exception e){
            us.remove(filename);
            Path deletePath = UploadDir.resolve(filename);
            Files.deleteIfExists(deletePath);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage() + "failed to upload chunks");
        }
    }

    @GetMapping("/uploaded/{fname}")
    public String uploaded(@PathVariable String fname){
        Path uploadPath = UploadDir.resolve(fname);
        File file = new File(uploadPath.toString());
        if(file.exists() && !file.isDirectory()){
            ResizeService.resizeVideo(uploadPath.toString(),"uploads/resized/myMid"+fname,480, 360);
            throw new ResponseStatusException(HttpStatus.OK);
        }
        else
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

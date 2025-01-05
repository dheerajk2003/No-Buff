package com.dheerakk2003.MariaMaven.web;

import com.dheerakk2003.MariaMaven.models.Upload;
import com.dheerakk2003.MariaMaven.service.ResService;
import com.dheerakk2003.MariaMaven.service.ResizeService;
import com.dheerakk2003.MariaMaven.service.StreamService;
import com.dheerakk2003.MariaMaven.service.UploadService;
import jdk.jfr.StackTrace;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.UUID;

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

    @PostMapping("/upload")
    public String uploadVideo(@RequestBody Upload up){
        try{
            String filename = UUID.randomUUID().toString()+".mp4";
            up.setFilename(filename);
            us.save(up);
            return filename;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/file")
    public ResponseEntity<String> upload(@RequestParam("chunk") MultipartFile chunk, @RequestParam("filename") String filename) throws IOException {
        try{
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
        int resolution[] = ResService.CheckRes(uploadPath.toString());
        if(resolution == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        float ratio = (float)resolution[0] / resolution[1];
        int width = (int)(360 * ratio);
        int taskCount = 1;
        ResizeService.resizeVideo(uploadPath.toString(),"uploads/resized/360p/"+fname,width, 360, 1000_00);

        if(resolution[1] >= 720 ){
            System.out.println("entered 720");
            width = (int)(720 * ratio);
            ResizeService.resizeVideo(uploadPath.toString(),"uploads/resized/720p/"+fname,width, 720, 3500_00);
            taskCount++;
        }

        if(resolution[1] >= 960){
            System.out.println("entered 1080");
            width = (int)(960  * ratio);
            ResizeService.resizeVideo(uploadPath.toString(),"uploads/resized/1080p/"+fname,width, 960, 10000_00);
            taskCount++;
        }
        ResizeService.awaitCompletion(uploadPath.toString(), taskCount);
        throw new ResponseStatusException(HttpStatus.OK);



    }

    @GetMapping("/360p/{filename}")
    public Mono<ResponseEntity<byte[]>> ServeVideo3(@RequestHeader(value = "Range", required = false) String rangeHeader, @PathVariable String filename) throws IOException{
        long start = 0;
        long end = 0;
        if(rangeHeader != null && rangeHeader.startsWith("bytes=")){
            String[] ranges = rangeHeader.substring(6).split("-");
            start = Long.parseLong(ranges[0]);
            if(ranges.length > 1 && !ranges[1].isEmpty()){
                end = Long.parseLong(ranges[1]);
            }
        }
        return StreamService.ServeVid(start, end, "360p/"+filename);
    }
    @GetMapping("/720p/{filename}")
    public Mono<ResponseEntity<byte[]>> ServeVideo7(@RequestHeader(value = "Range", required = false) String rangeHeader, @PathVariable String filename) throws IOException{
        long start = 0;
        long end = 0;
        if(rangeHeader != null && rangeHeader.startsWith("bytes=")){
            String[] ranges = rangeHeader.substring(6).split("-");
            start = Long.parseLong(ranges[0]);
            if(ranges.length > 1 && !ranges[1].isEmpty()){
                end = Long.parseLong(ranges[1]);
            }
        }
        return StreamService.ServeVid(start, end, "720p/"+filename);
    }
    @GetMapping("/1080p/{filename}")
    public Mono<ResponseEntity<byte[]>> ServeVideo10(@RequestHeader(value = "Range", required = false) String rangeHeader, @PathVariable String filename) throws IOException{
        long start = 0;
        long end = 0;
        if(rangeHeader != null && rangeHeader.startsWith("bytes=")){
            String[] ranges = rangeHeader.substring(6).split("-");
            start = Long.parseLong(ranges[0]);
            if(ranges.length > 1 && !ranges[1].isEmpty()){
                end = Long.parseLong(ranges[1]);
            }
        }
        return StreamService.ServeVid(start, end, "1080p/"+filename);
    }
}


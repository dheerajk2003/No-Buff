package com.dheerakk2003.MariaMaven.service;

import com.dheerakk2003.MariaMaven.models.Upload;
import com.dheerakk2003.MariaMaven.repository.UploadRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UploadService {
    private final UploadRepository ur;
    public UploadService(UploadRepository ur){
        this.ur = ur;
    }
    private static final Path UploadDir = Paths.get("uploads/uploaded/");
    private static final Path imageDir = Paths.get("uploads/images");

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

    public ResponseEntity<String> file(MultipartFile chunk, String filename) throws IOException{
        try{
            Path UploadPath = UploadDir.resolve(filename);
            Files.createDirectories(UploadPath.getParent());
            Files.write(UploadPath, chunk.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return ResponseEntity.ok("Successfull");
        }
        catch (Exception e){
            remove(filename);
            Path deletePath = UploadDir.resolve(filename);
            Files.deleteIfExists(deletePath);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage() + "failed to upload chunks");
        }
    }

    public String uploaded(String fname){

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

    public ResponseEntity<String> save(Long userId, String title, MultipartFile image){

        try{
            Upload up = new Upload();
            String filename = UUID.randomUUID().toString();
            up.setFilename(filename+".mp4");
            up.setImage(filename);
            up.setTitle(title);
            up.setUserId(userId);
            Path imgPath = imageDir.resolve(filename+".jpeg");
            Files.write(imgPath, image.getBytes());
            ur.save(up);
            return ResponseEntity.ok(filename+".mp4");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public Mono<ResponseEntity<byte[]>> ServeVideo(String rangeHeader, String filename) throws IOException {

        long start = 0;
        long end = 0;
        if(rangeHeader != null && rangeHeader.startsWith("bytes=")){
            String[] ranges = rangeHeader.substring(6).split("-");
            start = Long.parseLong(ranges[0]);
            if(ranges.length > 1 && !ranges[1].isEmpty()){
                end = Long.parseLong(ranges[1]);
            }
        }
        return StreamService.ServeVid(start, end, filename);
    }

    public void remove(String filename){
        ur.delete(ur.findByFilename(filename));
    }
}

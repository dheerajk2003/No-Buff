package com.dheerakk2003.MariaMaven.web;

import com.dheerakk2003.MariaMaven.models.Upload;
import com.dheerakk2003.MariaMaven.models.User;
import com.dheerakk2003.MariaMaven.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.UUID;

//@RestController
@Controller
public class UploadController {

    private static final Path UploadDir = Paths.get("uploads/uploaded/");
    private static final Path imageDir = Paths.get("uploads/images");

    private final UploadService us;

    private final UserService userService;
    public UploadController(UploadService us, UserService userService){
        this.us = us;
        this.userService = userService;
    }

    @GetMapping("/file/{id}")
    @ResponseBody
    public Upload get(@PathVariable Long id){
        Optional<Upload> u = us.get(id);
        return u.get();
    }

    @GetMapping("/allvid")
    @ResponseBody
    public Iterable<Upload> getVids(@RequestHeader("id") Long id){
        User user = userService.get(id);
        if(user != null){
            return us.getVids(id);
        }
        else{
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestParam("userId") Long userId, @RequestParam("title") String title, @RequestParam("image") MultipartFile image){
        return us.save(userId, title, image);
    }

    @PostMapping("/file")
    public ResponseEntity<String> upload(@RequestParam("chunk") MultipartFile chunk, @RequestParam("filename") String filename) throws IOException {
        return us.file(chunk, filename);
    }

    @GetMapping("/uploaded/{fname}")
    public String uploaded(@PathVariable String fname){
      return us.uploaded(fname);
    }

    @GetMapping("/360p/{filename}")
    public Mono<ResponseEntity<byte[]>> ServeVideo3(@RequestHeader(value = "Range", required = false) String rangeHeader, @PathVariable String filename) throws IOException{
        return us.ServeVideo(rangeHeader, "360p/"+filename);
    }
    @GetMapping("/720p/{filename}")
    public Mono<ResponseEntity<byte[]>> ServeVideo7(@RequestHeader(value = "Range", required = false) String rangeHeader, @PathVariable String filename) throws IOException{
        return us.ServeVideo(rangeHeader, "720p/"+filename);
    }
    @GetMapping("/1080p/{filename}")
    public Mono<ResponseEntity<byte[]>> ServeVideo10(@RequestHeader(value = "Range", required = false) String rangeHeader, @PathVariable String filename) throws IOException{
        return us.ServeVideo(rangeHeader, "1080p/"+filename);
    }

    @GetMapping("/getimg/{imgname}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imgname){
        return StreamService.getImage(imgname);
    }

    @GetMapping("/watch/{id}")
    public String watchVid(@PathVariable Long id, Model model){
        model.addAttribute("id", id);
        return "watch";
    }
}


package com.dheerakk2003.MariaMaven.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Service
public class StreamService {
    private static final String DirPath = "uploads/resized/";
    public static Mono<ResponseEntity<byte[]>> ServeVid(Long start, Long end, String filename, Integer hd) throws IOException {
        String VidPath = (hd == 1) ? "720p/" : "360p/";
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(Paths.get(DirPath+VidPath+filename));
        long filelength = fileChannel.size();
        if(end == 0 || end >= filelength)
            end = filelength - 1;
        long chunkSize = end - start +1;
        ByteBuffer buffer = ByteBuffer.allocate((int) chunkSize);

        CompletableFuture<byte[]> futureData = CompletableFuture.supplyAsync(() -> {
           try{
               fileChannel.read(buffer, start).get();
               buffer.flip();
               byte[] data = new byte[buffer.remaining()];
               buffer.get(data);
               return data;
           }
           catch(Exception e){
               throw new RuntimeException("Failed to read video chunk", e);
           }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "video/mp4");
        headers.set("Accept-Ranges", "bytes");
        headers.set("Content-Length", String.valueOf(chunkSize));
        headers.set("Content-Range", String.format("bytes %d-%d/%d", start, end, filelength));


        return  Mono.fromCallable(() -> {
            try{
                byte[] data = futureData.join();
                fileChannel.close();
                return new ResponseEntity<>(data, headers, HttpStatus.PARTIAL_CONTENT);
            }
            catch(Exception e){
                throw new RuntimeException("Error processing video stream", e);
            }
        });
    }
}

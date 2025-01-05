package com.dheerakk2003.MariaMaven.service;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;

import static org.bytedeco.opencv.global.opencv_imgproc.resize;
//@Service
public class ResizeService {
    private static final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final CompletionService<Boolean> completionService = new ExecutorCompletionService<>(pool);

    public static void resizeVideo(String inputFileName, String outputFileName, int width, int height, int bitrate){
        Runnable task = new Task(inputFileName, outputFileName, width, height, bitrate);
        completionService.submit(task, true);
    }

    public static boolean deleteVideo(String filename){
        Path filepath = Paths.get(filename);
        try{
            return Files.deleteIfExists(filepath);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static void awaitCompletion(String originalFile, int taskCount){
        try{
            for(int i=0; i<taskCount; i++){
                Future<Boolean> result = completionService.take();
                if(!result.get()){
                    throw new RuntimeException("task failed");
                }
            }
            deleteVideo(originalFile);
        }
        catch (Exception e){
            System.out.println("Error waiting for tasks to complete : " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

class Task implements Runnable{
    String inputFileName, outputFileName;
    int height, width, bitrate;

    public Task(String inputFileName, String outputFileName, int width, int height, int bitrate) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.height = height;
        this.width = width;
        this.bitrate = bitrate;
    }



    public void run()
    {
        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFileName)){
            grabber.start();
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFileName, width, height);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("mp4");
            recorder.setFrameRate((grabber.getFrameRate() <= 30) ? grabber.getFrameRate() : 30);
            recorder.setVideoBitrate(bitrate);
            recorder.start();

            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

            Frame frame;
            while((frame = grabber.grabImage()) != null){
                Mat mat = converter.convert(frame);
                Mat resizeMat = new Mat();
                resize(mat, resizeMat, new Size(width, height));
                Frame resizedFrame = converter.convert(resizeMat);
                recorder.record(resizedFrame);
                mat.release();
                resizeMat.release();
            }
            recorder.stop();
            recorder.release();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }
}
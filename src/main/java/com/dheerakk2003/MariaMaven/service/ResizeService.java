package com.dheerakk2003.MariaMaven.service;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.bytedeco.opencv.global.opencv_imgproc.resize;
//@Service
public class ResizeService {
    public static void resizeVideo(String inputFileName, String outputFileName, int width, int height, int bitrate){
        Runnable r = new Task(inputFileName, outputFileName, width, height, bitrate);
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        pool.execute(r);
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
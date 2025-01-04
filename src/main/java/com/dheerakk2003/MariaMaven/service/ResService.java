package com.dheerakk2003.MariaMaven.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;

public class ResService {
    public static int[] CheckRes(String filepath){
        try{
            ProcessBuilder pb = new ProcessBuilder(
                    "ffprobe", "-v", "error", "-select_streams", "v:0",
                    "-show_entries", "stream=width,height", "-of", "csv=p=0", filepath
            );
            Process p = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String resolution = br.readLine();
            if(resolution != null){
                String dimensions[] = resolution.split(",");
                return new int[]{Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1])};
            }
        }
        catch (Exception e){
            System.out.println("File doesn't exist " + e.getStackTrace());
        }
        return null;
    }
}

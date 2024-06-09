package com.alexandra.Astro.WorkClasses;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InteractionPhoto {

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\";

    public static void checkMainDir() {
        File dir = new File(UPLOAD_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void createDir(Long animalId) {
        String pathDir = UPLOAD_DIRECTORY + "\\" + animalId;
        File dirPhotos = new File(pathDir);
        dirPhotos.mkdirs();
    }

    public static void deleteDir(Long animalId) {
        String pathDir = UPLOAD_DIRECTORY + "\\" + animalId;
        File dirPhotos = new File(pathDir);
        File[] allContents = dirPhotos.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                file.delete();
            }
        }
        dirPhotos.delete();
    }

    public static String uploadImage(MultipartFile file, Long animalId) throws IOException {
        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY + "\\" + animalId, file.getOriginalFilename());
        Files.write(fileNameAndPath, file.getBytes());
        return file.getOriginalFilename();
    }

    public static String getPhoto(String photo) throws IOException {
//        String pathFile = UPLOAD_DIRECTORY + photo;
        return toBase64(photo);
    }

    public static void deletePhoto(String photo) {
        String pathFile = UPLOAD_DIRECTORY + photo;
        File file = new File(pathFile);
        file.delete();
    }

    private static String toBase64(String path) throws IOException {
        File file = new File(path);
        byte[] fileContent = Files.readAllBytes(file.toPath());
        Base64 base64 = new Base64();
        byte[] encodeBase64 = base64.encode(fileContent);
        return new String(encodeBase64, StandardCharsets.UTF_8);
    }
}

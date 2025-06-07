package com.example.eLearningPlatform.config.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class S3Service {

    @Autowired
    private AmazonS3 amazonS3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String upload(MultipartFile file, String fileName) throws AmazonServiceException, SdkClientException, IOException, InterruptedException {

        String contentType = file.getContentType();
        if (!contentType.toLowerCase().startsWith("video")) {
            throw new IllegalArgumentException("Uploaded file is not a video");
        }

        String originalFileName = file.getOriginalFilename();
        if (!originalFileName.toLowerCase().endsWith(".mp4")) {
            throw new IllegalArgumentException("Uploaded file must be an MP4 video");
        }

        File rawFile = convertMultiPartFileToFile(file);
        File optimizedFile = File.createTempFile("optimized-", ".mp4");

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-y",
                    "-i", rawFile.getAbsolutePath(),
                    "-c:v", "copy",
                    "-c:a", "copy",
                    "-movflags", "+faststart",
                    optimizedFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Start reading ffmpeg output in another thread
            Thread readerThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[FFmpeg] " + line); // or use a logger
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readerThread.start();

            // Wait for ffmpeg to finish, with timeout
            boolean finished = process.waitFor(5, TimeUnit.MINUTES);
            readerThread.join(); // ensure output reader finishes too

            if (!finished) {
                process.destroy();
                throw new RuntimeException("FFmpeg process timeout exceeded");
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg optimization failed. Exit code: " + exitCode);
            }

            // Upload optimized file to S3
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, optimizedFile));

        } finally {
            if (rawFile.exists()) rawFile.delete();
            if (optimizedFile.exists()) optimizedFile.delete();
        }

        return fileName;
    }




    public String download(String fileName) throws AmazonServiceException, SdkClientException, IOException {

        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileName)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);

        return amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();

    }

    public void delete(String fileName) {
        amazonS3Client.deleteObject(bucketName, fileName);
    }

    private File convertMultiPartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

}

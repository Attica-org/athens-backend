package com.attica.athens.domain.agora.application;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.attica.athens.domain.agora.dao.AgoraThumbnailRepository;
import com.attica.athens.domain.agora.domain.AgoraThumbnail;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3ThumbnailService {

    private final AmazonS3 s3client;
    private final AgoraThumbnailRepository agoraThumbnailRepository;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Transactional
    public AgoraThumbnail getAgoraThumbnail(MultipartFile file) {
        return Optional.ofNullable(file)
                .filter(f -> !f.isEmpty())
                .map(this::saveFile)
                .orElse(null);
    }

    private AgoraThumbnail saveFile(MultipartFile file) {
        String imageUrl = uploadToS3Bucket(file);
        AgoraThumbnail thumbnail = new AgoraThumbnail(file.getOriginalFilename(), imageUrl);

        return agoraThumbnailRepository.save(thumbnail);
    }

    private String uploadToS3Bucket(MultipartFile file) {
        String key = generateRandomFileName(file.getOriginalFilename());

        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType(file.getContentType());
        meta.setContentLength(file.getSize());

        PutObjectRequest por;
        try {
            por = new PutObjectRequest(bucket, key, file.getInputStream(), meta);
            s3client.putObject(por);

            return s3client.getUrl(bucket, key).toString();
        } catch (IOException e) {
            throw new AmazonS3Exception(e.getMessage());
        }
    }

    private String generateRandomFileName(String originName) {
        return UUID.randomUUID() + "." + extractExtension(originName);
    }

    private String extractExtension(String originName) {
        return originName.substring(originName.lastIndexOf(".") + 1);
    }
}

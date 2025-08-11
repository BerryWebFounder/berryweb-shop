package com.berryweb.shop.service;

import com.berryweb.shop.dto.ProductDto;
import com.berryweb.shop.dto.ReviewDto;
import com.berryweb.shop.entity.Product;
import com.berryweb.shop.entity.ProductImage;
import com.berryweb.shop.entity.Review;
import com.berryweb.shop.entity.ReviewImage;
import com.berryweb.shop.exception.CustomException;
import com.berryweb.shop.exception.ErrorCode;
import com.berryweb.shop.repository.ProductImageRepository;
import com.berryweb.shop.repository.ReviewImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.max-size}")
    private long maxFileSize;

    @Value("${file.upload.max-count}")
    private int maxFileCount;

    @Value("${file.upload.allowed-extensions}")
    private String allowedExtensions;

    private final ProductImageRepository productImageRepository;
    private final ReviewImageRepository reviewImageRepository;

    @Transactional
    public List<ProductDto.ProductImageInfo> saveProductImages(Product product, MultipartFile[] files, Long userId) {
        if (files.length > maxFileCount) {
            throw new CustomException(ErrorCode.FILE_COUNT_EXCEEDED);
        }

        List<String> allowedExts = Arrays.asList(allowedExtensions.split(","));
        List<ProductDto.ProductImageInfo> imageInfos = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            if (file.isEmpty()) continue;

            if (file.getSize() > maxFileSize) {
                throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);

            if (!allowedExts.contains(extension.toLowerCase())) {
                throw new CustomException(ErrorCode.FILE_EXTENSION_NOT_ALLOWED);
            }

            try {
                String storedFilename = generateStoredFilename(originalFilename);
                String filePath = uploadPath + "/" + storedFilename;

                // 디렉토리 생성
                Path uploadDir = Paths.get(uploadPath);
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                // 파일 저장
                Path targetPath = Paths.get(filePath);
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // 첫 번째 이미지를 메인 이미지로 설정
                boolean isMain = (i == 0);

                ProductImage productImage = ProductImage.builder()
                        .product(product)
                        .originalFilename(originalFilename)
                        .storedFilename(storedFilename)
                        .filePath(filePath)
                        .fileSize(file.getSize())
                        .isMain(isMain)
                        .sortOrder(i)
                        .createdBy(userId)
                        .build();

                productImage = productImageRepository.save(productImage);

                ProductDto.ProductImageInfo imageInfo = ProductDto.ProductImageInfo.builder()
                        .id(productImage.getId())
                        .originalFilename(productImage.getOriginalFilename())
                        .storedFilename(productImage.getStoredFilename())
                        .fileSize(productImage.getFileSize())
                        .isMain(productImage.getIsMain())
                        .altText(productImage.getAltText())
                        .sortOrder(productImage.getSortOrder())
                        .createdAt(productImage.getCreatedAt())
                        .build();

                imageInfos.add(imageInfo);

            } catch (IOException e) {
                log.error("Failed to save product image: {}", originalFilename, e);
                throw new CustomException(ErrorCode.FILE_SAVE_FAILED);
            }
        }

        return imageInfos;
    }

    @Transactional
    public List<ReviewDto.ReviewImageInfo> saveReviewImages(Review review, MultipartFile[] files) {
        if (files.length > 5) { // 리뷰 이미지는 최대 5개
            throw new CustomException(ErrorCode.FILE_COUNT_EXCEEDED);
        }

        List<String> allowedExts = Arrays.asList(allowedExtensions.split(","));
        List<ReviewDto.ReviewImageInfo> imageInfos = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            if (file.isEmpty()) continue;

            if (file.getSize() > maxFileSize) {
                throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);

            if (!allowedExts.contains(extension.toLowerCase())) {
                throw new CustomException(ErrorCode.FILE_EXTENSION_NOT_ALLOWED);
            }

            try {
                String storedFilename = generateStoredFilename(originalFilename);
                String filePath = uploadPath + "/" + storedFilename;

                // 디렉토리 생성
                Path uploadDir = Paths.get(uploadPath);
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                // 파일 저장
                Path targetPath = Paths.get(filePath);
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                ReviewImage reviewImage = ReviewImage.builder()
                        .review(review)
                        .originalFilename(originalFilename)
                        .storedFilename(storedFilename)
                        .filePath(filePath)
                        .fileSize(file.getSize())
                        .sortOrder(i)
                        .build();

                reviewImage = reviewImageRepository.save(reviewImage);

                ReviewDto.ReviewImageInfo imageInfo = ReviewDto.ReviewImageInfo.builder()
                        .id(reviewImage.getId())
                        .originalFilename(reviewImage.getOriginalFilename())
                        .storedFilename(reviewImage.getStoredFilename())
                        .fileSize(reviewImage.getFileSize())
                        .sortOrder(reviewImage.getSortOrder())
                        .createdAt(reviewImage.getCreatedAt())
                        .build();

                imageInfos.add(imageInfo);

            } catch (IOException e) {
                log.error("Failed to save review image: {}", originalFilename, e);
                throw new CustomException(ErrorCode.FILE_SAVE_FAILED);
            }
        }

        return imageInfos;
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private String generateStoredFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + (extension.isEmpty() ? "" : "." + extension);
    }

}

package com.helpnearby.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.helpnearby.dto.PresignedUpload;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class S3UploadService {

	@Value(value = "${aws.s3.accessKeyId}")
	private String accessKeyId;

	@Value(value = "${aws.s3.secretAccessKey}")
	private String accessSecret;

	private S3Presigner presigner;
	private final String bucket = "helpnearby";
	
	@Autowired
	private S3Client s3Client;

	@PostConstruct
	public void init() {
		AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, accessSecret);
		this.presigner = S3Presigner.builder().region(Region.US_EAST_2)
				.credentialsProvider(StaticCredentialsProvider.create(credentials)).build();
	}

	public PresignedUpload generatePresignedUrl(String fileName, String contentType) {

		String key = "requests/" + UUID.randomUUID() + "/" + fileName;

		PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucket).key(key).contentType(contentType)
				.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(5)).putObjectRequest(objectRequest).build();

		PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);

		return new PresignedUpload(presigned.url().toString(), "https://" + bucket + ".s3.amazonaws.com/" + key);
	}

	public PresignedUpload generatePresignedUrlProfilePicture(String fileName, String contentType) {

		String key = "profile_picture/" + UUID.randomUUID() + "/" + fileName;

		PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucket).key(key).contentType(contentType)
				.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(5)).putObjectRequest(objectRequest).build();

		PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);

		return new PresignedUpload(presigned.url().toString(), "https://" + bucket + ".s3.amazonaws.com/" + key);
	}
	
	/**
	 * Delete a file from S3 using its full URL
	 * @param s3Url The full S3 URL (e.g., https://helpnearby.s3.amazonaws.com/requests/uuid/filename.jpg)
	 */
	public void deleteFileFromS3(String s3Url) {
		if (s3Url == null || s3Url.isEmpty()) {
			return;
		}
		
		try {
			// Extract the S3 key from the URL
			// URL format: https://helpnearby.s3.amazonaws.com/requests/uuid/filename.jpg
			// Key format: requests/uuid/filename.jpg
			String key = extractS3KeyFromUrl(s3Url);
			if (key == null || key.isEmpty()) {
				System.err.println("Could not extract S3 key from URL: " + s3Url);
				return;
			}
			
			DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.build();
			
			s3Client.deleteObject(deleteRequest);
			System.out.println("Successfully deleted file from S3: " + key);
		} catch (Exception e) {
			System.err.println("Error deleting file from S3: " + s3Url + " - " + e.getMessage());
			// Don't throw exception - log and continue
		}
	}
	
	/**
	 * Extract S3 key from full S3 URL
	 * @param s3Url Full S3 URL
	 * @return S3 key or null if extraction fails
	 */
	private String extractS3KeyFromUrl(String s3Url) {
		if (s3Url == null || s3Url.isEmpty()) {
			return null;
		}
		
		// Handle different URL formats:
		// https://helpnearby.s3.amazonaws.com/requests/uuid/filename.jpg
		// https://helpnearby.s3.us-east-2.amazonaws.com/requests/uuid/filename.jpg
		String prefix = bucket + ".s3";
		int prefixIndex = s3Url.indexOf(prefix);
		if (prefixIndex == -1) {
			return null;
		}
		
		// Find the first '/' after the bucket name
		int keyStart = s3Url.indexOf('/', prefixIndex + prefix.length());
		if (keyStart == -1) {
			return null;
		}
		
		// Extract everything after the first '/' after bucket name
		String key = s3Url.substring(keyStart + 1);
		
		// Remove query parameters if any
		int queryIndex = key.indexOf('?');
		if (queryIndex != -1) {
			key = key.substring(0, queryIndex);
		}
		
		return key;
	}
}

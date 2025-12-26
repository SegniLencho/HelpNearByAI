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
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class S3UploadService {

	@Value(value = "${aws.s3.accessKeyId}")
	private String accessKeyId;

	@Value(value = "${aws.s3.secretAccessKey}")
	private String accessSecret;

	private S3Presigner presigner;
	private final String bucket = "helpnearby";

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
}


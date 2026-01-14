package com.helpnearby.dto;

public record PresignedUpload(String s3Key,String uploadUrl, String fileUrl) {
}
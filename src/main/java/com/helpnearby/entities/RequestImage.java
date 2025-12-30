package com.helpnearby.entities;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "request_images")
public class RequestImage {

	
	@Id
	@GeneratedValue
	@Column(columnDefinition = "uuid")
	private UUID id;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "is_primary")
    private boolean primaryImage;
  
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isPrimaryImage() {
        return primaryImage;
    }

    public void setPrimaryImage(boolean primaryImage) {
        this.primaryImage = primaryImage;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}

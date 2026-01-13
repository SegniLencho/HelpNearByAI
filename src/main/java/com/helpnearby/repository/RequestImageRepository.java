package com.helpnearby.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.helpnearby.entities.RequestImage;

@Repository
public interface RequestImageRepository extends JpaRepository<RequestImage, String> {

	
}

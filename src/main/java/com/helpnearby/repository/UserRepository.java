
package com.helpnearby.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.helpnearby.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	@Modifying
	@Transactional
	@Query("UPDATE User u SET u.fcmToken = null WHERE u.fcmToken IN :tokens")
	int clearFcmTokens(List<String> tokens);

}

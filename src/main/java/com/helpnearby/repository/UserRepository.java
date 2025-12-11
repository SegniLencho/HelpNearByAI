
package com.helpnearby.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.helpnearby.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}

package com.ulbra.metroplanpdfautomation.repositories;

import com.ulbra.metroplanpdfautomation.domain.entities.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  UserEntity findByEmailAndPassword(final String email, final String password);

  Optional<UserEntity> findByEmail(final String email);

  //  @Query("SELECT u.roles FROM UserEntity u WHERE u.username = :username")
  //  String findRolesByUsername(@Param("username") String username);
}

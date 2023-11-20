package com.ulbra.metroplanpdfautomation.repositories;

import com.ulbra.metroplanpdfautomation.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  UserEntity findByEmailAndPassword(final String email, final String password);

  UserEntity findByEmail(final String email);
}

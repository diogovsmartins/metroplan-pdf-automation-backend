package com.ulbra.metroplanpdfautomation.services;

import com.ulbra.metroplanpdfautomation.domain.DTOs.UserEntityDTO;
import com.ulbra.metroplanpdfautomation.domain.businessEnums.Roles;
import com.ulbra.metroplanpdfautomation.domain.entities.UserEntity;
import com.ulbra.metroplanpdfautomation.repositories.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserEntityService {

  private final UserRepository userRepository;

  @Autowired
  public UserEntityService(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserEntityDTO getUserRolesByEmailAndPassword(final UserEntityDTO userEntityDTO) {
    UserEntity correspondentUser =
        userRepository.findByEmailAndPassword(
            userEntityDTO.getEmail(), userEntityDTO.getPassword());

    return UserEntityDTO.builder()
        .email(correspondentUser.getEmail())
        .roles(correspondentUser.getRoles())
        .build();
  }

  public void saveNewUser(final UserEntityDTO userEntityDTO, final String callerRoles) {
    UserEntity userEntityToSave =
        UserEntity.builder()
            .email(userEntityDTO.getEmail())
            .password(new BCryptPasswordEncoder().encode(userEntityDTO.getPassword()))
            .build();

    checkCallerRoles(userEntityDTO, callerRoles, userEntityToSave);

    userRepository.save(userEntityToSave);
  }

  public void updateUserByEmail(final UserEntityDTO userEntityDTO, final String callerRoles) {
    Optional<UserEntity> userToBeUpdated = userRepository.findByEmail(userEntityDTO.getEmail());
    if (userToBeUpdated.isEmpty()) {
      // If I have enough time, create a global handler and some custom business exceptions to throw
      // this will make it easer for the user to understand what's wrong
      return;
    }
    userToBeUpdated.get().setEmail(userEntityDTO.getEmail());
    userToBeUpdated.get().setPassword(userEntityDTO.getPassword());
    checkCallerRoles(userEntityDTO, callerRoles, userToBeUpdated.get());
    userRepository.save(userToBeUpdated.get());
  }

  private static void checkCallerRoles(
      final UserEntityDTO userEntityDTO,
      final String callerRoles,
      final UserEntity userToBeUpdated) {
    if (callerRoles.contains(Roles.COORDINATOR.name())
        || callerRoles.contains(Roles.PROFESSOR.name())) {
      userToBeUpdated.setRoles(userEntityDTO.getRoles());
    } else {
      userToBeUpdated.setRoles(Roles.STUDENT.name());
    }
  }
}

package com.ulbra.metroplanpdfautomation.controllers;

import com.ulbra.metroplanpdfautomation.domain.DTOs.UserEntityDTO;
import com.ulbra.metroplanpdfautomation.services.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(value = "/user")
public class UserController {

  private UserEntityService userEntityService;

  @Autowired
  public UserController(final UserEntityService userEntityService) {
    this.userEntityService = userEntityService;
  }

  @PostMapping("/create")
  public ResponseEntity<String> createNewUser(
      @RequestBody final UserEntityDTO userToBeCreated, @RequestParam final String callerRoles) {
    userEntityService.saveNewUser(userToBeCreated, callerRoles);
    return ResponseEntity.ok().body("User Persisted Successfully.");
  }

  @PutMapping("/update")
  public ResponseEntity<String> updateAnUser(
      @RequestBody final UserEntityDTO userToBeCreated, @RequestParam final String callerRoles) {
    userEntityService.updateUserByEmail(userToBeCreated, callerRoles);
    return ResponseEntity.ok().body("User Updated Successfully.");
  }

  @GetMapping("/roles")
  public ResponseEntity<UserEntityDTO> checkUserRoles(
      @RequestParam final String email, @RequestParam final String password) {

    return ResponseEntity.ok()
        .body(
            userEntityService.getUserRolesByEmailAndPassword(
                UserEntityDTO.builder().email(email).password(password).build()));
  }
}

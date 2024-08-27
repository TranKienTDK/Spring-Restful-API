package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.annotation.apiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
public class UserController {

  private final UserService userService;

  private final PasswordEncoder passwordEncoder;

  public UserController(UserService userService, PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/users")
  public ResponseEntity<User> createNewUser(@RequestBody User postManUser) {
    String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
    postManUser.setPassword(hashPassword);
    User apiUser = userService.handleCreateUser(postManUser);
    return ResponseEntity.status(HttpStatus.CREATED).body(apiUser);
  }

  @DeleteMapping("/users/{id}")
  public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
    if (id >= 1500) {
      throw new IdInvalidException("Id khong lon hown 1501");
    }
    this.userService.handleDeleteUser(id);
    return ResponseEntity.status(HttpStatus.OK).body("Delete user with id: " + id);
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
    User fetchUser = this.userService.getUserById(id);
    return ResponseEntity.status(HttpStatus.OK).body(fetchUser);
  }

  @GetMapping("/users")
  @ApiMessage("Fetch user successfully")
  public ResponseEntity<ResultPaginationDTO> getAllUsers(
      @Filter Specification<User> spec,
      Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK).body(this.userService.getAllUsers(spec, pageable));
  }

  @PutMapping("/users")
  public ResponseEntity<User> updateUser(@RequestBody User requestUser) {
    User currentUser = userService.handleUpdateUser(requestUser);
    return ResponseEntity.status(HttpStatus.OK).body(currentUser);
  }

}

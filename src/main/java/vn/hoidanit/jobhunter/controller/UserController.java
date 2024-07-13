package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;

@RestController
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/user")
  public User createNewUser(@RequestBody User postManUser) {
    User apiUser = userService.handleCreateUser(postManUser);
    return apiUser;
  }

  @DeleteMapping("/user/{id}")
  public String deleteUser(@PathVariable("id") long id) {
    this.userService.handleDeleteUser(id);
    return "Delete user";
  }

  @GetMapping("/user/{id}")
  public User getUserById(@PathVariable("id") long id) {
    return this.userService.getUserById(id);
  }

  @GetMapping("/user")
  public List<User> getAllUsers() {
    return this.userService.getAllUsers();
  }

  @PutMapping("/user")
  public User updateUser(@RequestBody User requestUser) {
    User currentUser = userService.handleUpdateUser(requestUser);
    return currentUser;
  }

}

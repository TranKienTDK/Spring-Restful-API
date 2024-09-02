package vn.hoidanit.jobhunter.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResFetchUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.EmailAlreadyExistException;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {

  private final UserService userService;

  private final PasswordEncoder passwordEncoder;

  public UserController(UserService userService, PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/users")
  @ApiMessage("create user success")
  public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User postManUser)
      throws EmailAlreadyExistException {
    if (this.userService.handleValidateExistByEmail(postManUser.getEmail())) {
      throw new EmailAlreadyExistException(
          "Email: " + postManUser.getEmail() + " đã tồn tại. Vui lòng chọn email khác.");
    }
    String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
    postManUser.setPassword(hashPassword);
    User apiUser = userService.handleCreateUser(postManUser);
    ResCreateUserDTO userDTO = this.userService.handleTransferUserDTO(apiUser);
    return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
  }

  @DeleteMapping("/users/{id}")
  @ApiMessage("Delete a user")
  public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
    User fetchUser = this.userService.getUserById(id);
    if (fetchUser == null) {
      throw new IdInvalidException("ID: " + id + " không tìm thấy.");
    }
    this.userService.handleDeleteUser(id);
    return ResponseEntity.status(HttpStatus.OK).body(null);
  }

  @GetMapping("/users/{id}")
  @ApiMessage("Fetch user by id")
  public ResponseEntity<ResFetchUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
    User fetchUser = this.userService.getUserById(id);
    if (fetchUser == null) {
      throw new IdInvalidException("ID: " + id + " không tìm thấy.");
    }
    ResFetchUserDTO fetchUserDTO = this.userService.handleTransferFetchUserDTO(fetchUser);
    return ResponseEntity.status(HttpStatus.OK).body(fetchUserDTO);
  }

  @GetMapping("/users")
  @ApiMessage("Fetch user success")
  public ResponseEntity<ResultPaginationDTO> getAllUsers(
      @Filter Specification<User> spec,
      Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK).body(this.userService.getAllUsers(spec, pageable));
  }

  @PutMapping("/users")
  @ApiMessage("update user success")
  public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User requestUser) throws IdInvalidException {
    if (!this.userService.handleValidateExistById(requestUser.getId())) {
      throw new IdInvalidException("ID: " + requestUser.getId() + " không tìm thấy.");
    }
    User currentUser = userService.handleUpdateUser(requestUser);
    ResUpdateUserDTO updateUserDTO = this.userService.handleTransfUpdateUserDTO(currentUser);
    return ResponseEntity.status(HttpStatus.OK).body(updateUserDTO);
  }

}

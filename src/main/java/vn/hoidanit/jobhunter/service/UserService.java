package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User handleCreateUser(User user) {
    return this.userRepository.save(user);
  }

  public void handleDeleteUser(long id) {
    this.userRepository.deleteById(id);
  }

  public User getUserById(Long id) {
    Optional<User> userOptional = this.userRepository.findById(id);
    if (userOptional.isPresent()) {
      return userOptional.get();
    }
    return null;
  }

  public List<User> getAllUsers() {
    return this.userRepository.findAll();
  }

  public User handleUpdateUser(User requestUser) {
    User currentUser = this.getUserById(requestUser.getId());
    if (currentUser != null) {
      currentUser.setName(requestUser.getName());
      currentUser.setEmail(requestUser.getEmail());
      currentUser.setPassword(requestUser.getPassword());

      // update user
      currentUser = this.userRepository.save(currentUser);
    }
    return currentUser;
  }
}

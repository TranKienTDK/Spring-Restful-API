package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import javax.naming.spi.DirStateFactory.Result;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
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

  public ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable) {
    Page<User> pageUser = this.userRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();
    Meta mt = new Meta();

    mt.setPage(pageable.getPageNumber() + 1);
    mt.setPageSize(pageable.getPageSize());

    mt.setPages(pageUser.getTotalPages());
    mt.setTotal(pageUser.getTotalElements());

    rs.setMeta(mt);
    rs.setResult(pageUser.getContent());

    return rs;
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

  public User handleGetUserByUsername(String username) {
    return this.userRepository.findByEmail(username);
  }
}

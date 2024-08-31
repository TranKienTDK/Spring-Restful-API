package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.CreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.FetchUserDTO;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.dto.UpdateUserDTO;
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
    List<FetchUserDTO> listFetchUserDTOs = new ArrayList<FetchUserDTO>();

    mt.setPage(pageable.getPageNumber() + 1);
    mt.setPageSize(pageable.getPageSize());

    mt.setPages(pageUser.getTotalPages());
    mt.setTotal(pageUser.getTotalElements());

    rs.setMeta(mt);
    List<User> listUsers = pageUser.getContent();
    for (User u : listUsers) {
      FetchUserDTO fetchUserDTO = this.handleTransferFetchUserDTO(u);
      listFetchUserDTOs.add(fetchUserDTO);
    }
    rs.setResult(listFetchUserDTOs);

    return rs;
  }

  public User handleUpdateUser(User requestUser) {
    User currentUser = this.getUserById(requestUser.getId());
    if (currentUser != null) {
      currentUser.setName(requestUser.getName());
      currentUser.setGender(requestUser.getGender());
      currentUser.setAge(requestUser.getAge());
      currentUser.setAddress(requestUser.getAddress());

      // update user
      currentUser = this.userRepository.save(currentUser);
    }
    return currentUser;
  }

  public User handleGetUserByUsername(String username) {
    return this.userRepository.findByEmail(username);
  }

  public boolean handleValidateExistByEmail(String email) {
    return this.userRepository.existsByEmail(email);
  }

  public boolean handleValidateExistById(Long id) {
    return this.userRepository.existsById(id);
  }

  public CreateUserDTO handleTransferUserDTO(User user) {
    CreateUserDTO userDTO = new CreateUserDTO();

    userDTO.setId(user.getId());
    userDTO.setName(user.getName());
    userDTO.setEmail(user.getEmail());
    userDTO.setGender(user.getGender());
    userDTO.setAddress(user.getAddress());
    userDTO.setCreatedAt(user.getCreatedAt());

    return userDTO;
  }

  public UpdateUserDTO handleTransfUpdateUserDTO(User user) {
    UpdateUserDTO updateUserDTO = new UpdateUserDTO();

    updateUserDTO.setId(user.getId());
    updateUserDTO.setName(user.getName());
    updateUserDTO.setGender(user.getGender());
    updateUserDTO.setAddress(user.getAddress());
    updateUserDTO.setUpdateAt(user.getUpdateAt());

    return updateUserDTO;
  }

  public FetchUserDTO handleTransferFetchUserDTO(User user) {
    FetchUserDTO fetchUserDTO = new FetchUserDTO();

    fetchUserDTO.setId(user.getId());
    fetchUserDTO.setEmail(user.getEmail());
    fetchUserDTO.setName(user.getName());
    fetchUserDTO.setGender(user.getGender());
    fetchUserDTO.setAge(user.getAge());
    fetchUserDTO.setAddress(user.getAddress());
    fetchUserDTO.setUpdatedAt(user.getUpdateAt());
    fetchUserDTO.setCreatedAt(user.getCreatedAt());

    return fetchUserDTO;
  }

}

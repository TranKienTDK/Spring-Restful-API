package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResFetchUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;

  private final CompanyService companyService;

  public UserService(UserRepository userRepository, CompanyService companyService) {
    this.userRepository = userRepository;
    this.companyService = companyService;
  }

  public User handleCreateUser(User user) {

    // check company
    if (user.getCompany() != null) {
      Optional<Company> companyOptional = this.companyService.findById(user.getCompany().getId());
      user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
    }

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
    ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

    mt.setPage(pageable.getPageNumber() + 1);
    mt.setPageSize(pageable.getPageSize());

    mt.setPages(pageUser.getTotalPages());
    mt.setTotal(pageUser.getTotalElements());

    rs.setMeta(mt);

    // remove sensitive data
    List<ResFetchUserDTO> listUser = pageUser.getContent()
        .stream().map(item -> new ResFetchUserDTO(
            item.getId(),
            item.getEmail(),
            item.getName(),
            item.getGender(),
            item.getAddress(),
            item.getAge(),
            item.getUpdatedAt(),
            item.getCreatedAt(),
            new ResFetchUserDTO.CompanyUser(
                item.getCompany() != null ? item.getCompany().getId() : 0,
                item.getCompany() != null ? item.getCompany().getName() : null)))
        .collect(Collectors.toList());

    rs.setResult(listUser);
    return rs;
  }

  public User handleUpdateUser(User requestUser) {
    User currentUser = this.getUserById(requestUser.getId());
    if (currentUser != null) {
      currentUser.setName(requestUser.getName());
      currentUser.setGender(requestUser.getGender());
      currentUser.setAge(requestUser.getAge());
      currentUser.setAddress(requestUser.getAddress());

      // check company exist
      if (requestUser.getCompany() != null) {
        Optional<Company> companyOptional = this.companyService.findById(requestUser.getCompany().getId());
        requestUser.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
      }

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

  public ResCreateUserDTO handleTransferUserDTO(User user) {
    ResCreateUserDTO userDTO = new ResCreateUserDTO();
    ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();

    userDTO.setId(user.getId());
    userDTO.setName(user.getName());
    userDTO.setEmail(user.getEmail());
    userDTO.setAge(user.getAge());
    userDTO.setGender(user.getGender());
    userDTO.setAddress(user.getAddress());
    userDTO.setCreatedAt(user.getCreatedAt());

    if (user.getCompany() != null) {
      com.setId(user.getCompany().getId());
      com.setName(user.getCompany().getName());
      userDTO.setCompany(com);
    }
    return userDTO;
  }

  public ResUpdateUserDTO handleTransfUpdateUserDTO(User user) {
    ResUpdateUserDTO updateUserDTO = new ResUpdateUserDTO();
    ResUpdateUserDTO.CompanyUser com = new ResUpdateUserDTO.CompanyUser();
    if (user.getCompany() != null) {
      com.setId(user.getCompany().getId());
      com.setName(user.getCompany().getName());
      updateUserDTO.setCompany(com);
    }

    updateUserDTO.setId(user.getId());
    updateUserDTO.setName(user.getName());
    updateUserDTO.setGender(user.getGender());
    updateUserDTO.setAddress(user.getAddress());
    updateUserDTO.setUpdateAt(user.getUpdatedAt());

    return updateUserDTO;
  }

  public ResFetchUserDTO handleTransferFetchUserDTO(User user) {
    ResFetchUserDTO fetchUserDTO = new ResFetchUserDTO();

    fetchUserDTO.setId(user.getId());
    fetchUserDTO.setEmail(user.getEmail());
    fetchUserDTO.setName(user.getName());
    fetchUserDTO.setGender(user.getGender());
    fetchUserDTO.setAge(user.getAge());
    fetchUserDTO.setAddress(user.getAddress());
    fetchUserDTO.setUpdatedAt(user.getUpdatedAt());
    fetchUserDTO.setCreatedAt(user.getCreatedAt());

    return fetchUserDTO;
  }

  public void updateUserToken(String token, String email) {
    User currentUSer = this.handleGetUserByUsername(email);
    if (currentUSer != null) {
      currentUSer.setRefreshToken(token);
      this.userRepository.save(currentUSer);
    }
  }

  public User getUserByRefreshTokenAndEmail(String token, String email) {
    return this.userRepository.findByRefreshTokenAndEmail(token, email);
  }
}

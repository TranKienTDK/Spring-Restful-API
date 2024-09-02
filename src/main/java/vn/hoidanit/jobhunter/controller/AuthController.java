package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResLoginDTO;
import vn.hoidanit.jobhunter.domain.dto.request.ReqLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final SecurityUtil securityUtil;
  private final UserService userService;

  @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
  private long refreshTokenExpiration;

  public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
      UserService userService) {
    this.authenticationManagerBuilder = authenticationManagerBuilder;
    this.securityUtil = securityUtil;
    this.userService = userService;
  }

  @PostMapping("/auth/login")
  public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDto) {
    // Nạp input gồm username/password vào Security
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        loginDto.getUsername(), loginDto.getPassword());
    // xác thực người dùng => cần viết hàm loadUserByUsername
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    // set thong tin nguoi dung dang nhap vao context
    SecurityContextHolder.getContext().setAuthentication(authentication);

    ResLoginDTO res = new ResLoginDTO();
    User currentUserDB = this.userService.handleGetUserByUsername(loginDto.getUsername());
    if (currentUserDB != null) {
      ResLoginDTO.UserLogin user = new ResLoginDTO.UserLogin(currentUserDB.getId(), currentUserDB.getEmail(),
          currentUserDB.getName());
      res.setUser(user);
    }

    // create a token
    String access_token = this.securityUtil.createAccessToken(authentication.getName(), res.getUser());
    res.setAccessToken(access_token);

    // create refresh token
    String refresh_token = this.securityUtil.createRefreshToken(loginDto.getUsername(), res);

    // update user
    this.userService.updateUserToken(refresh_token, loginDto.getUsername());

    // set cookies
    ResponseCookie resCookies = ResponseCookie.from("refresh_token", refresh_token)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(refreshTokenExpiration)
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, resCookies.toString())
        .body(res);
  }

  @GetMapping("/auth/account")
  @ApiMessage("Fetch account")
  public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
    String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
    User currentUserDB = this.userService.handleGetUserByUsername(email);
    ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
    ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();

    if (currentUserDB != null) {
      userLogin.setId(currentUserDB.getId());
      userLogin.setEmail(currentUserDB.getEmail());
      userLogin.setName(currentUserDB.getName());
      userGetAccount.setUser(userLogin);
    }

    return ResponseEntity.ok().body(userGetAccount);
  }

  @GetMapping("/auth/refresh")
  @ApiMessage("Get user by refresh token")
  public ResponseEntity<ResLoginDTO> getRefreshToken(
      @CookieValue(name = "refresh_token") String refresh_token) throws IdInvalidException {
    // check valid
    Jwt decodeToken = this.securityUtil.checkValidRefreshToken(refresh_token);
    String email = decodeToken.getSubject();

    // check by refresh token and email
    User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
    if (currentUser == null) {
      throw new IdInvalidException("Refresh token không hợp lệ");
    }

    // issue
    ResLoginDTO res = new ResLoginDTO();
    User currentUserDB = this.userService.handleGetUserByUsername(email);
    if (currentUserDB != null) {
      ResLoginDTO.UserLogin user = new ResLoginDTO.UserLogin(currentUserDB.getId(), currentUserDB.getEmail(),
          currentUserDB.getName());
      res.setUser(user);
    }

    // create a token
    String access_token = this.securityUtil.createAccessToken(email, res.getUser());
    res.setAccessToken(access_token);

    // create refresh token
    String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

    // update user
    this.userService.updateUserToken(new_refresh_token, email);

    // set cookies
    ResponseCookie resCookies = ResponseCookie.from("refresh_token", refresh_token)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(refreshTokenExpiration)
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, resCookies.toString())
        .body(res);
  }

  @PostMapping("/auth/logout")
  @ApiMessage("Logout User")
  public ResponseEntity<Void> logout() throws IdInvalidException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    if (email.equals(null)) {
      throw new IdInvalidException("Access Token is invalid");
    }

    User user = this.userService.handleGetUserByUsername(email);
    if (user != null) {
      user.setRefreshToken(null);
      this.userService.handleUpdateUser(user);
    }

    // remove refresh token from cookies
    ResponseCookie deleteSpringCookie = ResponseCookie
        .from("refresh_token", null)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0)
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
        .body(null);
  }
}

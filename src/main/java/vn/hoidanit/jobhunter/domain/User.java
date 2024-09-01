package vn.hoidanit.jobhunter.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;

  @NotBlank(message = "email không được để trống")
  private String email;

  @NotBlank(message = "password không được để trống")
  private String password;

  private int age;

  @Enumerated(EnumType.STRING)
  private GenderEnum gender;
  private String address;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String refreshToken;

  private Instant createdAt;
  private Instant updateAt;
  private String createdBy;
  private String updateBy;

  @PrePersist
  public void handleBeforeCreate() {
    this.createdAt = Instant.now();
    this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get()
        : "";
  }

  @PreUpdate
  public void handleBeforeUpdate() {
    this.updateAt = Instant.now();
    this.updateBy = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get()
        : "";
  }

}

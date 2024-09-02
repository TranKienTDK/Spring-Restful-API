package vn.hoidanit.jobhunter.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ResFetchUserDTO {
  private long id;
  private String email;
  private String name;
  private GenderEnum gender;
  private String address;
  private int age;
  private Instant updatedAt;
  private Instant createdAt;
}

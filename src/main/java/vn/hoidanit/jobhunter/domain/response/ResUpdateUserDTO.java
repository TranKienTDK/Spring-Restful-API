package vn.hoidanit.jobhunter.domain.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResUpdateUserDTO {
  private long id;
  private String name;
  private GenderEnum gender;
  private String address;
  private Instant updateAt;
  private CompanyUser company;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CompanyUser {
    private long id;
    private String name;
  }
}
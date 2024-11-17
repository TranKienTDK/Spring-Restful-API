package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
  private final SkillService skillService;

  public SkillController(SkillService skillService) {
    this.skillService = skillService;
  }

  @PostMapping("/skills")
  @ApiMessage("Create new skill")
  public ResponseEntity<Skill> createNewSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
    if (this.skillService.handleExistsByName(skill.getName())) {
      throw new IdInvalidException("Skill name = " + skill.getName() + " đã tồn tại.");
    }

    Skill resSkill = this.skillService.handleCreateSkill(skill);
    return ResponseEntity.ok().body(resSkill);
  }

  @PutMapping("/skills")
  @ApiMessage("Update a skill")
  public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
    Skill dbSkill = this.skillService.handleGetSkillById(skill.getId());
    if (dbSkill == null) {
      throw new IdInvalidException("Skill có ID = " + skill.getId() + " không tồn tại");
    }

    if (this.skillService.handleExistsByName(skill.getName())) {
      throw new IdInvalidException("Skill name = " + skill.getName() + " đã tồn tại.");
    }

    Skill updatedSkill = this.skillService.handleUpdateSkill(skill);
    return ResponseEntity.ok().body(updatedSkill);
  }

  @GetMapping("/skills")
  @ApiMessage("Get all skills")
  public ResponseEntity<ResultPaginationDTO> getAllSkills(@Filter Specification<Skill> spec, Pageable pageable) {
    return ResponseEntity.ok().body(this.skillService.handleGetAllSkill(spec, pageable));
  }
}

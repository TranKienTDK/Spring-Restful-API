package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class SkillService {
  private final SkillRepository skillRepository;

  public SkillService(SkillRepository skillRepository) {
    this.skillRepository = skillRepository;
  }

  public Skill handleCreateSkill(Skill skill) {
    return this.skillRepository.save(skill);
  }

  public boolean handleExistsByName(String name) {
    return this.skillRepository.existsByName(name);
  }

  public Skill handleUpdateSkill(Skill skill) {
    Skill dbSkill = this.handleGetSkillById(skill.getId());
    if (dbSkill != null) {
      dbSkill.setName(skill.getName());
    }

    return this.skillRepository.save(dbSkill);
  }

  public Skill handleGetSkillById(long id) {
    Optional<Skill> skillOptional = this.skillRepository.findById(id);
    if (skillOptional.isPresent()) {
      return skillOptional.get();
    }
    return null;
  }

  public ResultPaginationDTO handleGetAllSkill(Specification<Skill> spec, Pageable pageable) {
    Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();
    ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

    meta.setPage(pageable.getPageNumber() + 1);
    meta.setPageSize(pageable.getPageSize());

    meta.setPages(pageSkill.getTotalPages());
    meta.setTotal(pageSkill.getTotalElements());

    rs.setMeta(meta);
    rs.setResult(pageSkill.getContent());

    return rs;
  }

  public List<Skill> getListSkillInAJob(List<Long> id) {
    List<Skill> skillsList = new ArrayList<Skill>();

    for (Long perId : id) {
      if (this.skillRepository.findById(perId).isPresent()) {
        skillsList.add(this.skillRepository.findById(perId).get());
      }
    }

    return skillsList;
  }
}
